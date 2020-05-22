package bee.com.nascent.maven.plugin.process;

import bee.com.nascent.maven.plugin.process.BeeEnvironment.DataProvider;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Consumer;

/**
 * .
 *
 * @author zhangmsh
 * @version 1.0.0
 * @date 2020/5/15
 */
@SuppressWarnings({"java:S1181", "java:S116", "java:S117", "java:S100"})
public abstract class AbstractBeeMethodArgs<T>
    implements BeeMethodArgs, BeeSpringRequestBodySupport,
    BeeHttpRequestSupport<T> {

    private DataAddition _bee_data_Addition;
    private Map<String, Object> _bee_request_Params;
    private boolean _bee_is_request_Body;
    private String _bee_http_Request_Result_Body;
    private Consumer<Throwable> _bee_http_Request_Exception_Handler;
    private T _bee_mock_Data;
    @SuppressWarnings("java:S1068")
    private Type _bee_T_Type;

    public AbstractBeeMethodArgs(DataAddition _bee_data_Addition) {
        this._bee_data_Addition = _bee_data_Addition;

        Type type = this.getClass().getGenericSuperclass();
        ParameterizedType parameterizedType = (ParameterizedType) type;
        Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
        this._bee_T_Type = actualTypeArguments[0];
    }

    protected void _bee_add_Reqest_Params(String key, Object value) {
        if (_bee_request_Params == null) {
            _bee_request_Params = new HashMap<>();
        }
        if (_bee_is_request_Body && !_bee_request_Params.isEmpty()) {
            throw new BeeRequestBodyException("无法在RequestBody之后添加参数");
        }
        _bee_request_Params.put(key, value);
    }


    @Override
    public BeeHttpRequestSupport onException(Consumer<Throwable> e) {
        this._bee_http_Request_Exception_Handler = e;
        return this;
    }

    @Override
    public BeeHttpRequestBodySupport doPost(int connectionTimeOut, int readTimeOut) {
        try {
            if (this._bee_mock_Data != null) {
                return new DefaultBeeHttpRequestBodySupport<T>()
                    .fromBody(this._bee_mock_Data);
            }
            long s = System.currentTimeMillis();
            if (!_bee_is_request_Body || this._bee_empty_Params()) {
                this.doPostOnParams(connectionTimeOut, readTimeOut);
            } else {
                this.doPostOnContent(connectionTimeOut, readTimeOut);
            }
            long e = System.currentTimeMillis();
            return new DefaultBeeHttpRequestBodySupport<T>(
                this._bee_data_Addition._bee_Return_Type_Class())
                .timeUsed(e - s)
                .fromString(this._bee_http_Request_Result_Body);
        } catch (Throwable e) {
            if (_bee_http_Request_Exception_Handler != null) {
                _bee_http_Request_Exception_Handler.accept(e);
            } else if (BeeEnvironment.getGlobalException() != null) {
                BeeEnvironment.getGlobalException().accept(e);
            }
        }
        return new DefaultBeeHttpRequestBodySupport<T>();
    }

    private void doPostOnContent(int connectionTimeOut, int readTimeOut) throws IOException {
        Set<Entry<String, Object>> entries = _bee_request_Params.entrySet();
        Object data = null;
        for (Entry<String, Object> entry : entries) {
            if (data != null) {
                break;
            }
            data = entry.getValue();
        }
        byte[] bytes = this.additionParamJson(data);
        this._bee_http_Request_Result_Body = WebUtils.doPost(
            _bee_data_Addition._bee_Mapping_url(), WebUtils.CONTENT_TYPE_JSON,
            bytes, connectionTimeOut, readTimeOut);
    }

    @SuppressWarnings("java:S2259")
    private void doPostOnParams(int connectionTimeOut, int readTimeOut) throws IOException {
        Map<String, String> params = null;
        if (!this._bee_empty_Params()) {
            params = new HashMap<>(_bee_request_Params.size(), 1);
            Set<Entry<String, Object>> entries = _bee_request_Params.entrySet();
            for (Entry<String, Object> entry : entries) {
                params.put(entry.getKey(), entry.getValue().toString());
            }
        }
        this.additionParams(params);
        this._bee_http_Request_Result_Body = WebUtils.doPost(
            _bee_data_Addition._bee_Mapping_url(), params,
            connectionTimeOut, readTimeOut);
    }

    /**
     * 将附加参数附加到请求参数中，如果需要，同时生成签名
     *
     * @param params
     */
    private <T> void additionParams(final Map<String, T> params) {
        DataProvider dataProvider = BeeEnvironment.getDataProvider();
        Map<String, String> strValueMap = new HashMap<>(params.size(), 1);
        JsonParser jsonParser = BeeEnvironment.getJsonParser();
        params.forEach((key, value) -> {
            params.replace(key, (T) dataProvider.getData(key, value));
            strValueMap.put(key, value instanceof Character
                ? value.toString() : jsonParser.writeValueAsString(value));
        });

        String sign = BeeEnvironment.getSignature().sign(strValueMap);
        if (sign != null) {
            params.put(BeeEnvironment.getSignature().signKey(), (T) sign);
        }
    }

    /**
     * 将附加参数附加到请求参数中，，如果需要，同时生成签名。该方法用来附加JSON
     *
     * @param data
     * @return
     */
    private byte[] additionParamJson(final Object data) {
        Map<String, Object> params = BeeEnvironment.getJsonParser().writeValueAsMap(data);
        this.additionParams(params);
        return BeeEnvironment.getJsonParser().writeValueAsByte(params);
    }

//    private byte[] getBytesBody(Object data) throws IOException {
//        ObjectMapper objectMapper = new ObjectMapper();
//        JsonNode jsonNode = objectMapper.readTree(
//            objectMapper.writeValueAsString(data));
//        if (BeeEnvironment.hasStepData()) {
//            ObjectNode stepData = BeeEnvironment.getStepData();
//            if (jsonNode instanceof ArrayNode) {
//                ((ArrayNode) jsonNode).forEach(node ->
//                    stepData.fieldNames().forEachRemaining(n -> {
//                        if (node.get(n).isNull()) {
//                            ((ObjectNode) node).putPOJO(n, stepData.get(n));
//                        }
//                    }));
//            } else {
//                stepData.fieldNames().forEachRemaining(n -> {
//                    if (jsonNode.get(n).isNull()) {
//                        ((ObjectNode) jsonNode).putPOJO(n, stepData.get(n));
//                    }
//                });
//            }
//        }
//        this.addSignParam(jsonNode);
//        return objectMapper.writeValueAsBytes(jsonNode);
//    }

//    private void addSignParam(JsonNode jsonNode) {
//        if (BeeEnvironment.getSignFun() != null) {
//            if (jsonNode instanceof ArrayNode) {
//                throw new JsonException("无法在数组中添加签名");
//            }
//            Map<String, String> params = new HashMap<>();
//            jsonNode.fields().forEachRemaining(entry ->
//                params.put(entry.getKey(), entry.getValue().toString()));
//            String sign = BeeEnvironment.getSignFun()
//                .apply(params);
//            if (sign != null) {
//                ((ObjectNode) jsonNode).put(BeeEnvironment.getSignKey(), sign);
//            }
//        }
//    }


    @Override
    public BeeHttpRequestBodySupport doGet(int connectionTimeOut, int readTimeOut) {
        try {
            if (this._bee_mock_Data != null) {
                return new DefaultBeeHttpRequestBodySupport<T>()
                    .fromBody(this._bee_mock_Data);
            }
            if (!_bee_is_request_Body || this._bee_empty_Params()) {
                this.doGetOnParams(connectionTimeOut, readTimeOut);
            } else {
                this.doGetOnContent(connectionTimeOut, readTimeOut);
            }
            return new DefaultBeeHttpRequestBodySupport<T>(
                this._bee_data_Addition._bee_Return_Type_Class())
                .fromString(_bee_http_Request_Result_Body);
        } catch (Throwable e) {
            if (_bee_http_Request_Exception_Handler != null) {
                _bee_http_Request_Exception_Handler.accept(e);
            } else if (BeeEnvironment.getGlobalException() != null) {
                BeeEnvironment.getGlobalException().accept(e);
            }
        }
        return new DefaultBeeHttpRequestBodySupport<T>();
    }

    private void doGetOnContent(int connectionTimeOut, int readTimeOut) throws IOException {
        Set<Entry<String, Object>> entries = _bee_request_Params.entrySet();
        Object data = null;
        for (Entry<String, Object> entry : entries) {
            if (data != null) {
                break;
            }
            data = entry.getValue();
        }
        byte[] bytes = this.additionParamJson(data);
        this._bee_http_Request_Result_Body = WebUtils.doGet(
            _bee_data_Addition._bee_Mapping_url(), WebUtils.CONTENT_TYPE_JSON,
            bytes, connectionTimeOut, readTimeOut);
    }

    @SuppressWarnings("java:S2259")
    private void doGetOnParams(int connectionTimeOut, int readTimeOut) throws IOException {
        Map<String, String> params = null;
        if (!this._bee_empty_Params()) {
            params = new HashMap<>(_bee_request_Params.size(), 1);
            Set<Entry<String, Object>> entries = _bee_request_Params.entrySet();
            for (Entry<String, Object> entry : entries) {
                params.put(entry.getKey(), entry.getValue().toString());
            }
        }
        this.additionParams(params);
        this._bee_http_Request_Result_Body = WebUtils.doGet(
            _bee_data_Addition._bee_Mapping_url(), params, StandardCharsets.UTF_8,
            connectionTimeOut, readTimeOut);
    }


    @Override
    public BeeHttpRequestSupport<T> toRequestBody() {
        if (this._bee_empty_Params()) {
            throw new BeeRequestBodyException("参数为空时不能使用RequestBody");
        }
        if (_bee_request_Params.size() > 1) {
            throw new BeeRequestBodyException("RequestBody只允许一个参数变量存在");
        }
        _bee_is_request_Body = true;
        return this;
    }

    @Override
    public BeeHttpRequestBodySupport mock(T mockData) {
        this._bee_mock_Data = mockData;
        return new DefaultBeeHttpRequestBodySupport<T>()
            .fromBody(this._bee_mock_Data);
    }

    private boolean _bee_empty_Params() {
        return _bee_request_Params == null || _bee_request_Params.isEmpty();
    }

    protected String get_bee_http_Request_Result_Body() {
        return _bee_http_Request_Result_Body;
    }

}
