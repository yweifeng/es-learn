import org.apache.http.HttpHost;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
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
}
