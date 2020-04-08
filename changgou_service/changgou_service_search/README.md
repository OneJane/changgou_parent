java -Dfile.encoding=utf-8 -jar markdown-1.0.jar -h 第1天.md 第1天.html

docker run -di --name=changgou_elasticsearch -p 9200:9200 -p 9300:9300 elasticsearch:5.6.8
http://10.33.72.96:9200/ 但是程序无法连接5.x默认关闭远程连接

docker exec -it changgou_elasticsearch bash
vi config/elasticsearch.yml

http.host: 0.0.0.0
transport.host: 0.0.0.0
#discovery.zen.minimum_master_nodes: 1
cluster.name: my-application
http.cors.enabled: true
http.cors.allow-origin: "*"
network.host: 10.33.72.96

vi /etc/elasticsearch/jvm.options
-Xms2g
-Xmx2g


vi /etc/security/limits.conf 
* soft nofile 65536
* hard nofile 65536

vi /etc/sysctl.conf 
vm.max_map_count=655360

sysctl -p

docker update --restart=always changgou_elasticsearch
reboot

https://github.com/medcl/elasticsearch-analysis-ik/releases/download/v5.6.8/elasticsearch-analysis-ik-5.6.8.zip
unzip elasticsearch-analysis-ik-5.6.8.zip -d ./ik 
cd ik && mv elasticsearch ik 
docker cp ik changgou_elasticsearch:/user/share/elasticsearch/plugins
docker restart changgou_elasticsearch

docker run -di --name elasticsearch-head -p 9100:9100 mobz/elasticsearch-head:5

http://10.33.72.96:9200/_analyze?analyzer=ik_smart&pretty=true&text=我是程序员
http://10.33.72.96:9200/_analyze?analyzer=ik_max_word&pretty=true&text=我是程序员

docker run -it -d -e ELASTICSEARCH_URL=http://10.33.72.96:9200 --name kibana --restart=always -p 5601:5601 kibana:5.6.8
http://10.33.72.96:5601/app/kibana#/management/kibana/index?_g=()
Management-Index Patterns-Create Index Pattern-userinfo  填写userinfo索引
Discover选择userinfo索引后即可查询


Dev Tools
#  获取所有索引库信息
GET _cat/indices?v

# 删除索引库
DELETE /userinfo

# 新增索引库
PUT /user

# 创建映射
PUT /user/userinfo/_mapping
{
	"properties": {
		"name": {
			"type": "text",
			"analyzer": "ik_smart",
			"search_analyzer": "ik_smart",
			"store": false
		},
		"city": {
			"type": "text",
			"analyzer": "ik_smart",
			"search_analyzer": "ik_smart",
			"store": false
		},
		"age": {
			"type": "long",
			"store": false
		},
		"description": {
			"type": "text",
			"analyzer": "ik_smart",
			"search_analyzer": "ik_smart",
			"store": false
		}
	}
}

# 添加/修改文档数据
PUT /user/userinfo/1
{
  "name":"onejane",
  "age":18,
  "city":"南京",
  "description":"李四来自武汉"
}


# 查询数据
GET /user/userinfo/1

# 更新数据
POST /user/userinfo/1/_update
{
  "doc":{
    "name":"张三丰",
    "description":"来自苏州"
  }
}

# 删除数据
DELETE /user/userinfo/1

# 查询数据
GET /user/_search


# 根据age降序
GET /user/userinfo/_search
{
  "query":{
    "match_all": {}
  },
  "sort":
    {
      "age":{
        "order":"asc"
      }
    } ,
    "from": 0,
    "size": 20
}


# 过滤查询term主要用于分词精确匹配，如字符串、数值、日期等（不适合情况：1.列中除英文字符外有其它值 2.字符串值中有冒号或中文 3.系统自带属性如_version）
GET _search
{
  "query":{
    "term":{
      "city":"武汉"
    }
  }
}

# range 范围过滤
GET _search
{
  "query":{
    "range": {
      "age": {
        "gte": 30,
        "lte": 57
      }
    }
  }
}

#过滤搜索 exists：是指包含某个域的数据检索
GET _search
{
  "query": {
    "exists":{
      "field":"address"
    }
  }
}

#过滤搜索 bool 
#must : 多个查询条件的完全匹配,相当于 and。
#must_not : 多个查询条件的相反匹配，相当于 not。
#should : 至少有一个查询条件匹配, 相当于 or。
GET _search
{
  "query": {
    "bool": {
      "must": [
        {
          "term": {
            "city": {
              "value": "深圳"
            }
          }
        },
        {
          "range":{
            "age":{
              "gte":20,
              "lte":99
            }
          }
        }
      ]
    }
  }
}


#查询所有 match_all
GET _search
{
  "query": {
    "match_all": {}
  }
}


#字符串匹配
GET _search
{
  "query": {
    "match": {
      "description": "武汉"
    }
  }
}


#前缀匹配 prefix
GET _search
{
  "query": {
    "prefix": {
      "name": {
        "value": "赵"
      }
    }
  }
}

#多个域匹配搜索
GET _search
{
  "query": {
    "multi_match": {
      "query": "深圳",
      "fields": [
        "city",
        "description"
      ]
    }
  }
}

