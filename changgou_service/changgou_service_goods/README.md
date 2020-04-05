# 二级缓存
来减少下游redis系统的服务压力
```$xslt
/usr/local/server/lua65/update_content.lua
/usr/local/server/lua65/read_content.lua
vim /usr/local/openresty/nginx/conf/nginx.conf
        #表示所有以 localhost/read_content的请求都由该配置处理
        location /read_content {
            #使用指定限流配置,burst=4表示允许同时有4个并发连接,如果不能同时处理，则会放入队列，等请求处理完成后，再从队列中拿请求
            #nodelay 并行处理所有请求
            limit_req zone=contentRateLimit burst=4 nodelay;
            #content_by_lua_file:所有请求都交给指定的lua脚本处理(/root/lua/read_content.lua)
            content_by_lua_file /usr/local/server/lua65/read_content.lua;
        }

        #表示所有以 localhost/update_content的请求都由该配置处理
        location /update_content {
            #content_by_lua_file:所有请求都交给指定的lua脚本处理(/root/lua/update_content.lua)
            content_by_lua_file /usr/local/server/lua65/update_content.lua;
        }
./nginx -s reload

http://10.33.72.96/read_content?id=1#  第一次取nginx缓存没有从mysql取后存到redis，第二次取nginx缓存从redis中取并存到nginx缓存，第三次从redis缓存中取
http://10.33.72.96/update_content?id=1#

```

# nginx限流
漏桶算法实现控制速率限流
```$xslt
limit_req_zone $binary_remote_addr zone=contentRateLimit:10m rate=2r/s;
```

limit_req zone=contentRateLimit
binary_remote_addr 是一种key，表示基于 remote_addr(客户端IP) 来做限流，
zone：定义共享内存区来存储访问信息， myRateLimit:10m 表示一个大小为10M，名字为 myRateLimit的内存区域。1M能存储16000 IP地址的访问信息，10M可以存储16W IP地址访 问信息。
rate 用于设置最大访问速率，rate=10r/s 表示每秒最多处理10个请求。Nginx 实际上以 毫秒为粒度来跟踪请求信息，因此 10r/s 实际上是限制：每100毫秒处理一个请求。这意味 着，自上一个请求处理完后，若后续100毫秒内又有请求到达，将拒绝处理该请求.我们这里 设置成2 方便测试。
上面例子限制 2r/s，如果有时正常流量突然增大，超出的请求将被拒绝，无法处理突发 流量，可以结合 burst 参数使用来解决该问题。

limit_req zone=myRateLimit burst=5;
burst 译为突发、爆发，表示在超过设定的处理速率后能额外处理的请求数,当 rate=2r/s 时，将1s拆成2份，即每500ms可处理1个请求。
burst=5，若同时有6个请求到达，Nginx 会处理第一个请求，剩余5个请求将放 入队列，然后每隔500ms从队列中获取一个请求进行处理。若请求数大于6，将拒绝处理 多余的请求，直接返回503
单独使用 burst 参数并不实用。假设 burst=50 ，rate为10r/s，排队中的50个请 求虽然每100ms会处理一个，但第50个请求却需要等待 50 * 100ms即 5s，这么长的处 理时间自然难以接受

limit_req zone=myRateLimit burst=5 nodelay;
burst 往往结合 nodelay 一起使用,处理突发5个请求的时候，没有延迟，等到完成之后，按照正常的速率处理。