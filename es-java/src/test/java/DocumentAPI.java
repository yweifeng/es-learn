import org.apache.http.HttpHost;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.Sum;
import org.elasticsearch.search.aggregations.metrics.SumAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class DocumentAPI {

    private RestHighLevelClient client;

    @Before
    public void init() {
        HttpHost host = new HttpHost("192.168.137.128", 9200, "http");
        client = new RestHighLevelClient(RestClient.builder(host));
    }

    @After
    public void release() {
        if (client != null) {
            try {
                client.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 新增或者修改
     * @throws Exception
     */
    @Test
    public void index() throws Exception{
        IndexRequest request = new IndexRequest("test", "user", "10");
        Map<String, Object> mapSource = new HashMap<String, Object>();
        mapSource.put("name", "天才3");
        mapSource.put("age", 16);
        request.source(mapSource);
        IndexResponse indexResponse = client.index(request, RequestOptions.DEFAULT);
        System.out.println(indexResponse.getId());
    }

    /**
     * 查询
     * @throws Exception
     */
    @Test
    public void get() throws Exception {
        GetRequest request = new GetRequest("test", "user", "10");
        GetResponse response = client.get(request, RequestOptions.DEFAULT);
        System.out.println(response.getSource());
    }

    /**
     * 判断是否存在
     * @throws Exception
     */
    @Test
    public void exists() throws Exception {
        GetRequest request = new GetRequest("test", "user", "10");
        request.fetchSourceContext(new FetchSourceContext(false));
        request.storedFields("_none_");
        boolean exist = client.exists(request, RequestOptions.DEFAULT);
        System.out.println("exist = " + exist);
    }

    /**
     * 修改
     * @throws Exception
     */
    @Test
    public void update() throws Exception {
        UpdateRequest request = new UpdateRequest("test", "user", "10");
        Map<String, Object> updateMap = new HashMap<String, Object>();
        updateMap.put("name", "张三");
        request.doc(updateMap);

        UpdateResponse response = client.update(request, RequestOptions.DEFAULT);
        System.out.println(response.status());
    }

    /**
     * 删除
     * @throws Exception
     */
    @Test
    public void delete() throws Exception {
        DeleteRequest request = new DeleteRequest("test", "user", "10");
        DeleteResponse response = client.delete(request, RequestOptions.DEFAULT);
        System.out.println(response.status());
    }

    /**
     * SearchType类型4-DFS query then fetch
     * 比第2种方式多了一个DFS步骤。
     *  实现原理
     * 第一步：先对所有分片发送请求，把所有分片中的词频和文档
     * 频率等打分依据全部汇总到一块。
     * 第二步：然后再执行后面的操作后续操作
     *  优点：
     * 返回的数据量是准确的，数据排名也是准确的。
     *  缺点：
     * 性能最差【这个最差只是表示在这四种查询方式中性能最慢，
     * 也不至于不能忍受，如果对查询性能要求不是非常高，而对查询准确
     * 度要求比较高的时候可以考虑这个】
     * @throws Exception
     */
    @Test
    public void searchTypeDFSQueryThenFetch() throws Exception {
        SearchRequest request = new SearchRequest();
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(QueryBuilders.termQuery("name", "jack"));
        request.indices("test");
        request.source(sourceBuilder);
        request.searchType(SearchType.DFS_QUERY_THEN_FETCH);

        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        SearchHits searchHits = response.getHits();
        System.out.println(searchHits.getTotalHits());
    }

    /**
     * 实现原理
     * 第一步，先向所有的shard发出请求，各分片只返回文档id(注
     * 意，不包括文档document)和排名相关的信息(也就是文档对应的分值)，
     * 然后按照各分片返回的文档的分数进行重新排序和排名，取前size个
     * 文档。
     * 第二步，根据文档id去相关的shard取document。这种方式返
     * 回的document数量与用户要求的大小是相等的。
     *  优点：
     * 返回的数据量是准确的。
     *  缺点：
     * 数据排名不准确且性能一般。
     * @throws Exception
     */
    @Test
    public void searchTypeQueryThenFetch() throws Exception {
        SearchRequest request = new SearchRequest();
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(QueryBuilders.termQuery("name", "jack"));
        request.indices("test");
        request.source(sourceBuilder);
        request.searchType(SearchType.QUERY_THEN_FETCH);

        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        SearchHits searchHits = response.getHits();
        System.out.println(searchHits.getTotalHits());
    }

    /**
     * 查询，支持分页和排序以及条件过滤
     */
    @Test
    public void query() throws Exception {
        SearchRequest request = new SearchRequest();
        request.indices("test");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(QueryBuilders.matchAllQuery())
                .from(0)
                .size(2)
                .sort("age", SortOrder.ASC)
                .postFilter(QueryBuilders.termQuery("name", "jack"))
                .explain(true);
        request.source(sourceBuilder);

        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        SearchHits searchHits = response.getHits();
        for (SearchHit hit:
             searchHits) {
            System.out.println(hit.getSourceAsString());
        }
    }

    /**
     * 分组统计 按年龄
     * @throws Exception
     */
    @Test
    public void aggregation() throws Exception {
        SearchRequest request = new SearchRequest();
        request.indices("test");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        AggregationBuilder aggregationBuilder = AggregationBuilders.terms("by_age").field("age");
        sourceBuilder.aggregation(aggregationBuilder);
        request.source(sourceBuilder);

        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        Terms terms = response.getAggregations().get("by_age");
        for (Terms.Bucket entry: terms.getBuckets()) {
            Object key = entry.getKey();
            long count = entry.getDocCount();
            System.out.println(key + "@" + count);
        }
    }


    /**
     * 分组统计 先按姓名 后按年龄统计
     * @throws Exception
     */
    @Test
    public void aggregationSum() throws Exception {
        SearchRequest request = new SearchRequest();
        request.indices("test");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        AggregationBuilder aggregationBuilder = AggregationBuilders.terms("by_name").field("name.keyword");
        SumAggregationBuilder sumAggregationBuilder = AggregationBuilders.sum("by_age").field("age");
        aggregationBuilder.subAggregation(sumAggregationBuilder);
        sourceBuilder.aggregation(aggregationBuilder);
        request.source(sourceBuilder);

        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        Terms terms = response.getAggregations().get("by_name");
        for (Terms.Bucket entry: terms.getBuckets()) {
            Sum sum = entry.getAggregations().get("by_age");
            System.out.println(entry.getKey() + "@" + sum.getValue());
        }
    }
}
