REST PUT和POST的用法区别
---

    PUT是幂等方法，而POST并不是。所以PUT用于更新操作、
    POST用于新增操作比较合适。
    PUT，DELETE操作是幂等的。所谓幂等是指不管进行多少次操作，
    结果都一样。比如我用PUT修改一篇文章，然后在做同样的操作，
    每次操作后的结果并没有不同，DELETE也是一样。
    POST操作不是幂等的，比如常见的POST重复加载问题：当我们
    多次发出同样的POST请求后，其结果是创建出了若干的资源。
    还有一点需要注意的就是，创建操作可以使用POST，也可以使用
    PUT，区别在于POST是作用在一个集合资源之上的（/articles），
    而PUT操作是作用在一个具体资源之上的（/articles/123），比如说
    很多资源使用数据库自增主键作为标识信息，而创建的资源的标识
    信息到底是什么只能由服务端提供，这个时候就必须使用POST。
    
 查询索引-GET
---
  
根据员工id查询
######

    curl -XGET 'http://localhost:9200/test/user/1'
    {"_index":"test","_type":"user","_id":"1","_version":4,"_seq_no":5,"_primary_term":1,"found":true,"_source":{"name" : "jack","age" : 26}}

在任意的查询字符串中添加pretty参数，es可以得到易于识别的json结果
######

    curl -XGET 'http://localhost:9200/test/user/1?pretty'
    {
      "_index" : "test",
      "_type" : "user",
      "_id" : "1",
      "_version" : 4,
      "_seq_no" : 5,
      "_primary_term" : 1,
      "found" : true,
      "_source" : {
        "name" : "jack",
        "age" : 26
      }
    }

检索文档中的一部分，如果只需要显示指定字段
######

    curl -XGET 'http://localhost:9200/test/user/1?_source=name&pretty'
    {
      "_index" : "test",
      "_type" : "user",
      "_id" : "1",
      "_version" : 4,
      "_seq_no" : 5,
      "_primary_term" : 1,
      "found" : true,
      "_source" : {
        "name" : "jack"
      }
    }
 