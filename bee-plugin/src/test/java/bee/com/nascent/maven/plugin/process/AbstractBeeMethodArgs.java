package bee.com.nascent.maven.plugin.process;

import bee.com.nascent.maven.plugin.process.__BeeEnvironment.DataProvider;
import bee.com.nascent.maven.plugin.process.__BeeEnvironment.Elapsed;
import bee.com.nascent.maven.plugin.process.json.JsonParser;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
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
    private Map<String, String> _bee_request_Headers;
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
    public BeeHttpRequestSupport header(String key, String value) {
        if (_bee_request_Headers == null) {
            _bee_request_Headers = new HashMap<>();
        }
        _bee_request_Headers.put(key, value);
        return this;
    }

    @Override
    public BeeHttpRequestSupport onException(Consumer<Throwable> e) {
        this._bee_http_Request_Exception_Handler = e;
        return this;
    }

    @Override
    public BeeHttpRequestBodySupport doPost(int connectionTimeOut, int readTimeOut) {
        Elapsed elapsed = Elapsed.get();
        elapsed.start(Elapsed.ALL);
        try {
            if (this._bee_mock_Data != null) {
                return new DefaultBeeHttpRequestBodySupport<T>()
                    .fromBody(this._bee_mock_Data);
            }
            Map params = this.postRequest(connectionTimeOut, readTimeOut);
            elapsed.start(Elapsed.BODY_SUPPORT);
            return new DefaultBeeHttpRequestBodySupport<T>(
                this._bee_data_Addition._bee_Return_Type_Class())
                .fromString(this._bee_http_Request_Result_Body)
                .onPost(_bee_data_Addition._bee_Mapping_url())
                .requestParams(params);
        } catch (Throwable e) {
            if (_bee_http_Request_Exception_Handler != null) {
                _bee_http_Request_Exception_Handler.accept(e);
            } else if (__BeeEnvironment.getGlobalException() != null) {
                __BeeEnvironment.getGlobalException().accept(e);
            }
        } finally {
            elapsed.end(Elapsed.BODY_SUPPORT);
            elapsed.end(Elapsed.ALL);
        }
        return new DefaultBeeHttpRequestBodySupport<T>();
    }


    /**
     * 将附加参数附加到请求参数中，如果需要，同时生成签名
     *
     * @param params
     */
    private <T> Map<String, T> additionParams(Map<String, T> params) {
        DataProvider dataProvider = __BeeEnvironment.getDataProvider();
        JsonParser jsonParser = __BeeEnvironment.getJsonParser();
        Map<String, String> strValueMap;
        if (params == null) {
            params = new HashMap<>();
        }
        if (!params.isEmpty()) {
            strValueMap = new HashMap<>(params.size(), 1);
            final Map<String, T> paramsRef = params;
            paramsRef.forEach((key, value) -> {
                value = (T) dataProvider.getData(key, value);
                paramsRef.replace(key, value);
                if (value == null) {
                    strValueMap.put(key, null);
                } else {
                    strValueMap.put(key, value instanceof CharSequence
                        ? value.toString() : jsonParser.writeValueAsString(value));
                }
            });
        } else {
            strValueMap = new HashMap<>();
        }
        //计算必须参数
        Set<String> requiredParams = dataProvider.getRequiredParams();
        for (String requiredParam : requiredParams) {
            if (params.containsKey(requiredParam)) {
                continue;
            }
            T value = (T) dataProvider.getData(requiredParam, null);
            params.put(requiredParam, value);
            if (value == null) {
                strValueMap.put(requiredParam, null);
            } else {
                strValueMap.put(requiredParam, value instanceof CharSequence
                    ? value.toString() : jsonParser.writeValueAsString(value));
            }
        }
        String sign = __BeeEnvironment.getSignature().sign(strValueMap);
        if (sign != null) {
            params.put(__BeeEnvironment.getSignature().signKey(), (T) sign);
        }

        return params;
    }

    /**
     * 将附加参数附加到请求参数中，，如果需要，同时生成签名。该方法用来附加JSON
     *
     * @param data
     * @return
     */
    private Map<String, Object> additionParamJson(final Object data) {
        Map<String, Object> params = __BeeEnvironment.getJsonParser().writeValueAsMap(data);
        return this.additionParams(params);
    }


    @Override
    public BeeHttpRequestBodySupport doGet(int connectionTimeOut, int readTimeOut) {
        Elapsed elapsed = Elapsed.get();
        elapsed.start(Elapsed.ALL);
        try {
            if (this._bee_mock_Data != null) {
                return new DefaultBeeHttpRequestBodySupport<T>()
                    .fromBody(this._bee_mock_Data);
            }
            Map params = this.getRequest(connectionTimeOut, readTimeOut);
            elapsed.start(Elapsed.BODY_SUPPORT);
            return new DefaultBeeHttpRequestBodySupport<T>(
                this._bee_data_Addition._bee_Return_Type_Class())
                .fromString(_bee_http_Request_Result_Body)
                .onGet(_bee_data_Addition._bee_Mapping_url())
                .requestParams(params);
        } catch (Throwable e) {
            if (_bee_http_Request_Exception_Handler != null) {
                _bee_http_Request_Exception_Handler.accept(e);
            } else if (__BeeEnvironment.getGlobalException() != null) {
                __BeeEnvironment.getGlobalException().accept(e);
            }
        } finally {
            elapsed.end(Elapsed.BODY_SUPPORT);
            elapsed.end(Elapsed.ALL);
        }
        return new DefaultBeeHttpRequestBodySupport<T>();
    }

    private Map postRequest(int connectionTimeOut, int readTimeOut) throws IOException {
        Elapsed elapsed = Elapsed.get();
        Map params = null;
        String mappingUrl = _bee_data_Addition._bee_Mapping_url();
        if (!_bee_is_request_Body || this._bee_is_Empty_Params()) {
            //如果不是RequestBody或者没有请求数据。
            elapsed.start(Elapsed.HTTP_PARAM);
            if (!this._bee_is_Empty_Params()) {
                params = new HashMap<>(_bee_request_Params.size(), 1);
                Set<Entry<String, Object>> entries = _bee_request_Params.entrySet();
                for (Entry<String, Object> entry : entries) {
                    params.put(entry.getKey(), entry.getValue().toString());
                }
                params = this.additionParams(params);
            }
            elapsed.end(Elapsed.HTTP_PARAM);
            this._bee_http_Request_Result_Body = WebUtils.formPost(
                mappingUrl, params, connectionTimeOut, readTimeOut, _bee_request_Headers);
        } else {
            elapsed.start(Elapsed.HTTP_PARAM);
            Object data = this.getFirstParamData();
            params = this.additionParamJson(data);
            byte[] bytes = __BeeEnvironment.getJsonParser().writeValueAsByte(params);
            elapsed.end(Elapsed.HTTP_PARAM);
            this._bee_http_Request_Result_Body = WebUtils.jsonBodyPost(
                mappingUrl, bytes, connectionTimeOut, readTimeOut, _bee_request_Headers);
        }
        return params;
    }

    private Map getRequest(int connectionTimeOut, int readTimeOut) throws IOException {
        Elapsed elapsed = Elapsed.get();
        Map params = null;
        String mappingUrl = _bee_data_Addition._bee_Mapping_url();
        if (!_bee_is_request_Body || this._bee_is_Empty_Params()) {
            //如果不是RequestBody或者没有请求数据。
            elapsed.start(Elapsed.HTTP_PARAM);
            if (!this._bee_is_Empty_Params()) {
                params = new HashMap<>(_bee_request_Params.size(), 1);
                Set<Entry<String, Object>> entries = _bee_request_Params.entrySet();
                for (Entry<String, Object> entry : entries) {
                    params.put(entry.getKey(), entry.getValue().toString());
                }
            }
            params = this.additionParams(params);
            elapsed.end(Elapsed.HTTP_PARAM);
            this._bee_http_Request_Result_Body = WebUtils.doGet(
                mappingUrl, params, connectionTimeOut, readTimeOut, _bee_request_Headers);
        } else {
            elapsed.start(Elapsed.HTTP_PARAM);
            Object data = this.getFirstParamData();
            params = this.additionParamJson(data);
            byte[] bytes = __BeeEnvironment.getJsonParser().writeValueAsByte(params);
            elapsed.end(Elapsed.HTTP_PARAM);
            this._bee_http_Request_Result_Body = WebUtils.jsonBodyGet(
                mappingUrl, bytes, connectionTimeOut, readTimeOut, _bee_request_Headers);
        }
        return params;
    }

    private Object getFirstParamData() {
        Set<Entry<String, Object>> entries = _bee_request_Params.entrySet();
        Object data = null;
        for (Entry<String, Object> entry : entries) {
            if (data != null) {
                break;
            }
            data = entry.getValue();
        }
        return data;
    }

    @Override
    public BeeHttpRequestSupport<T> toRequestBody() {
        if (this._bee_is_Empty_Params()) {
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

    private boolean _bee_is_Empty_Params() {
        return _bee_request_Params == null || _bee_request_Params.isEmpty();
    }

    protected String get_bee_http_Request_Result_Body() {
        return _bee_http_Request_Result_Body;
    }

}
