REST PUT和POST的用法区别
###
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