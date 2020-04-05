ngx.header.content_type="application/json;charset=utf8"
local uri_args = ngx.req.get_uri_args();
local id = uri_args["id"];
--获取本地缓存
local cache_ngx = ngx.shared.dis_cache;
--根据ID 获取本地缓存数据
local contentCache = cache_ngx:get('content_cache_'..id);

if contentCache == "" or contentCache == nil then
    local redis = require("resty.redis");
    local red = redis:new()
    red:set_timeout(2000)
    red:connect("10.33.72.96", 6379)
    local rescontent=red:get("content_"..id);
    
    if ngx.null == rescontent or "" == rescontent or nil == rescontent then
        local cjson = require("cjson");
        local mysql = require("resty.mysql");
        local db = mysql:new();
        db:set_timeout(2000)
        local props = {
            host = "10.33.72.96",
            port = 3306,
            database = "changgou_content",
            user = "root",
            password = "123456"
        }
        local res = db:connect(props);
        local select_sql = "select url,pic from tb_content where status ='1' and category_id="..id.." order by sort_order";
        res = db:query(select_sql);
        local responsejson = cjson.encode(res);
        red:set("content_"..id,responsejson);
        ngx.say(responsejson.."1");
        db:close()
    else
        -- 2分钟后nginx缓存失效
        cache_ngx:set('content_cache_'..id, rescontent, 2*60);
        ngx.say(rescontent.."2")
    end
    red:close()
else
    ngx.say(contentCache.."3")
end
