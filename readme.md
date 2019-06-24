ES中的核心概念
---

**Shards**
###

1）代表索引分片，es可以把一个完整的索引分成多个分片，这样的好处是可以。把一个大的索引水平拆分成多个，分布到不同的节点上。构成分布式搜索，提高性能和吞吐量。
######
2）分片的数量只能在创建索引库时指定，索引库创建后不能更改。
######
3）默认是一个索引库有5个分片.每个分片中最多存储2,147,483,519条数据
######
    
    创建索引并设置分片
    curl -H "Content-Type:application/json" -XPUT 'http://localhost:9200/test3/' -d '{"settings":{"number_of_shards":3}}'
    
    
**Replicas**
###

1）代表索引副本，es可以给索引分片设置副本。
######
2）副本的作用：  
    一是提高系统的容错性，当某个节点某个分片损坏或丢失时可以从副本中恢复。  
    二是提高es的查询效率，es会自动对搜索请求进行负载均衡。【副本的数量可以随时修改】  
######
3）默认是一个分片有1个副本. 主分片和副本不会存在一个节点中。

    创建索引并设置副本
    curl -H "Content-Type:application/json" -XPUT 'http://localhost:9200/test4/' -d '{"settings":{"number_of_replicas":2}}'
    
    修改副本
    curl -H "Content-Type:application/json" -XPUT 'http://localhost:9200/test3/_settings' -d '{"index":{"number_of_replicas":3}}'
    
**recovery**
###

代表数据恢复或叫数据重新分布，es在有节点加入或退出时会根据机器的负载对索引分片进行重新分配，挂掉的节点重新启动时也会进行数据恢复。
######

**Gateway**
###

代表es索引的持久化存储方式，es默认是先把索引存放到内存中，当内存满了时再持久化到硬盘。当这个es集群关闭再重新启动时就会从gateway
中读取索引数据。es支持多种类型的gateway，有本地文件系统（默认），分布式文件系统，Hadoop的HDFS和Amazon的s3云存储服务。
######

**Transport**
###

代表es内部节点或集群与客户端的交互方式，默认内部是使用tcp协议进行交互，  
同时它支持http协议（json格式thrift、servlet、memcached、zeroMQ等的传输协议（通过插件方式集成）。
######
    
**settings**
###

索引配置信息，例如分片数量，副本数量
######

**mapping**
###

    查询索引库的mapping信息：
    curl -H "Content-Type:application/json" -XGET 'http://localhost:9200/test3/_mapping?pretty'
    
    创建不存在的索引:
    curl -H "Content-Type:application/json" -XPUT 'http://localhost:9200/test5/' -d '{"mappings": {"user": {"properties": {"name": {"type": "text"}}}}}'
    
    操作已存在的索引（修改）
    curl -H "Content-Type: application/json" -XPOST http://localhost:9200/test5/user/_mapping -d '{"properties":{"name":{"type":"text","analyzer": "ik_max_word"}}}'
    