Domain Specific Language领域特定语言
---

**创建索引**
######
    curl -XPUT http://localhost:9200/test2

**添加文档**
######

    curl -H "Content-Type:application/json" -XPUT http://localhost:9200/test2/user/9/_create -d '{"name": "ywf", "age": 27}'
    
    查询添加结果   
    curl -XGET 'http://localhost:9200/test2/user/_search?pretty&q=name:ywf'
   
    查询是否存在
    curl -i -XHEAD 'http://localhost:9200/test2/user/9'
    
**修改文档**
######
    
    局部更新，可以添加新字字段（必须使用POST）
    curl -H "Content-Type:application/json" -XPOST http://localhost:9200/test2/user/9/_update -d '{"doc": {"sex":"男"}}'
    
**删除文档**
######
    curl -H "Content-Type:application/json" -XDELETE http://localhost:9200/test2/user/9

