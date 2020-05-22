# BEE MAVEN 插件
用来扫描特定的类生产对应的可匹配调用的接口信息。
（注：代码很乱...还没时间整理...）

# 使用示例
```maven
<plugin>
    <groupId>com.nascent.maven.plugin</groupId>
    <artifactId>bee-maven-plugin</artifactId>
    <version>1.1.1-SNAPSHOT</version>
    <executions>
        <execution>
            <goals>
                <goal>junit</goal>
            </goals>
        </execution>
    </executions>
    <configuration>
        <!-- 配置接口请求地址 -->
        <location>http://localhost:9091</location>
        <models>
            <!-- 配置项目的模块依赖 -->
            <model>../ecrp-saas-open-platform-base</model>
            <model>../../ecrp-saas-common</model>
            <model>../../ecrp-saas-outside/ecrp-saas-outside-api</model>
            <model>../ecrp-saas-open-platform-model</model>
            <model>../ecrp-saas-open-platform-service</model>
            <model>../ecrp-saas-open-platform-web</model>
            <model>../../ecrp-saas-core/ecrp-saas-core-model</model>
        </models>
    </configuration>
</plugin>
```

# 使用
运行`mvn bee:junit`，默认会在项目的测试目录下生成对应的测试依赖代码。

# 配置说明
## 参数动态附值
```java
 BeeApplication.globalRequestParamRegister()
    //如果有nonce字段，而且对象本身值为null,则使用该数据
    .onParam("nonce", () -> String.valueOf(System.currentTimeMillis()))
    //同上
    .onParam("appKey", APP_KEY)
    .onParam("groupId", String.valueOf(GROUP_ID))
    //如果遇到authType，并且值为1，则替换为3
    .onParam("authType", 3, 1)
    //设置签名算法，如果不设置，则不签名
    .sign("sign", maps -> {
        return RequestSign.sign(maps, APP_SECRET);
    });
```
## 请求示例
```java
RegisterAuthorityRequest request = new RegisterAuthorityRequest();
BeeAsserts<RegisterAuthorityResponse> asserts
    //调用的方法，所有的测试用Controller都会根据现有的控制器生成
    //生成规则为Bee+原有控制器名称 
    = BeeBasisController.registerAuthority()
    //进行参数编辑
    .args()
    //这里会根据参数的名称进行方法生成，即这里的参数名称为request，所以生成的方法为request
    .request(request)
    //标记为ReuqestBody
    .toRequestBody()
    //执行Post方法
    .doPost()
    //进行断言
    .asserts()
    //断言响应的结果中的success为true
    .isTrue(x -> x.getSuccess());
```

## 强化的参数动态附值
由于服务流程问题，其他接口的请求数据，可能需要经过请求某个接口之后的响应数据，
所以，在特定接口响应成功之后，需要将数据填冲到其他的请求接口中。

```java
RegisterAuthorityRequest request = new RegisterAuthorityRequest();
BeeAsserts<RegisterAuthorityResponse> asserts = BeeBasisController
    .registerAuthority()
    .args()
    .request(request)
    .toRequestBody()
    .doPost()
    .asserts()
    .isTrue(x -> x.getSuccess());

//将请求接口设置到前置步骤中
BeeApplication.stepPrepositionRegister()
    .register(asserts)
    //设置的数据为 accessToken， 值为响应结果中的result
    .onData(d -> new Param("accessToken", d.getResult()));
//这个步骤等价行 
//RegisterAuthorityResponse d = asserts.body();
//BeeApplication.globalRequestParamRegister().onParam("accessToken",d.getResult());
```

## 异步处理
### 方法
```java
BeeBasisController
    .registerAuthority()
    .args()
    .request(request)
    .toRequestBody()
    //处理异常
    .onException(e -> e.printStackTrace())
    .doPost()
    .asserts()
    .isTrue(x -> x.getSuccess());
```
### 全局
```java
 BeeApplication.configuration()
    .globalException(e -> e.printStackTrace());
```

## JSON配置处理
### 设置Date格式化
```java
BeeApplication.configuration()
            .getJsonParser().configuration().dateFormat("yyyy-MM-dd HH:mm:ss");
```

## 关于测试
### 测试懒加载
公司开发框架中，会加载所有的类，并进行初始化。该情况会导致测试中的静态初始化模块
被执行而导致异常。

### 解析方式
进行类的懒初始化加载
```
//测试基础类示例
@Ignore
public class BasicTest {

    public static final String GROUP_NAME = "研发二部4";
    public static final String APP_KEY = "xx";
    public static final long GROUP_ID = 10086;
    public static final String APP_SECRET = "xxx";

    private static final class Init {

        static {
            BeeApplication.configuration()
                .globalException(e -> e.printStackTrace())
                .getJsonParser().configuration().dateFormat("yyyy-MM-dd HH:mm:ss");

            BeeApplication.globalRequestParamRegister()
                .onParam("nonce", () -> String.valueOf(System.currentTimeMillis()))
                .onParam("appKey", APP_KEY)
                .onParam("groupId", String.valueOf(GROUP_ID))
                .sign("sign", maps -> {
                    return RequestSign.sign(maps, APP_SECRET);
                });
            System.out.println("--beforeGetAuth:-------");
            RegisterAuthorityRequest request = new RegisterAuthorityRequest();
            BeeAsserts<RegisterAuthorityResponse> asserts = BeeBasisController
                .registerAuthority()
                .args()
                .request(request)
                .toRequestBody()
                .doPost()
                .asserts()
                .isTrue(x -> x.getSuccess());

            BeeApplication.stepPrepositionRegister()
                .register(asserts).onData(d -> new Param("accessToken", d.getResult()));
        }

        public static void init() {
            //ignore
        }
    }

    {   
        BasicTest.Init.init();
    }

}
```

## 测试用例
```java
@Slf4j
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class BasicControllerTest extends BasicTest {

    @Test
    public void groupInfoGet() {
        //取得集团信息
        GroupInfoGetRequest request = new GroupInfoGetRequest();
        request.setGroupName(GROUP_NAME);
        BeeBasisController.groupInfoGet()
            .args()
            .request(request)
            .toRequestBody()
            .doPost()
            .asserts()
            .isTrue(g -> g.getSuccess())
            .isTrue(g -> g.getResult().getGroupId() == GROUP_ID);
    }

    private static BrandInfo step1BrandInfo;

    @Test
    public void step1BrandListQuery() {
        //查询视角列表
        BrandListQueryRequest request = new BrandListQueryRequest();
        BrandListQueryResponse body = BeeBasisController.brandListQuery()
            .args()
            .request(request)
            .toRequestBody()
            .doPost()
            .timeUsed(l -> System.out.println("step1BrandListQuery time used " + l + "ms"))
            .asserts()
            .isTrue(g -> g.getSuccess())
            .isTrue(g -> !CollectionUtils.isEmpty(g.getResult()))
            .body();

        //查询店铺列表
        List<BrandInfo> result = body.getResult();
        step1BrandInfo = result.get(0);
        Assert.assertNotNull(step1BrandInfo);
    }

    private static BasisShopInfo step2BasisShopInfo;

    @Test
    public void step2ShopListQuery() {
        if (step1BrandInfo == null) {
            step1BrandListQuery();
        }
        Assert.assertNotNull(step1BrandInfo);
        ShopListQueryRequest shopListQueryRequest = new ShopListQueryRequest();
        shopListQueryRequest.setBrandId(step1BrandInfo.getBrandId());
        ShopListQueryResponse body = BeeBasisController.shopListQuery()
            .args()
            .request(shopListQueryRequest)
            .toRequestBody()
            .doPost(3000, 100000)
            .asserts()
//            .bodyJson(res -> System.out.println("step2ShopListQuery:" + res))
            .isTrue(res -> res.getSuccess())
            .isTrue(res -> !CollectionUtils.isEmpty(res.getResult()))
            .body();
        step2BasisShopInfo = body.getResult().get(0);
    }

    public BasisShopInfo getStep2BasisShopInfo() {
        return step2BasisShopInfo;
    }
}
```