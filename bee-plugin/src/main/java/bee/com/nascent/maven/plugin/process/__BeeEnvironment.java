package bee.com.nascent.maven.plugin.process;

import bee.com.nascent.maven.plugin.process.json.JackjsonJsonParser;
import bee.com.nascent.maven.plugin.process.json.JsonParser;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * .
 *
 * @author zhangmsh
 * @version 1.0.0
 * @date 2020/5/17
 */
public class __BeeEnvironment {

    private __BeeEnvironment() {

    }

    public static class Elapsed extends TreeMap<String, Long> {

        private static final ThreadLocal<Elapsed> THREAD_LOCAL = new ThreadLocal();
        private static final String TU = "ms\n";

        public static final String BODY_EXCEPTION = "异常处理（内耗）";
        public static final String HTTP_PARAM = "处理参数用时（内耗）";
        public static final String BODY_SUPPORT = "响应结果组装（内耗）";
        public static final String HTTP_CONNECTION = "Http连接用时";
        public static final String HTTP_WRITE = "Http写数据用时";
        public static final String HTTP_REQUEST = "Http请求用时";
        public static final String HTTP_RESPOSE = "Http读取用时";
        public static final String ALL = "总用时";

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append(ALL).append(this.get(ALL)).append(TU);
            builder.append("+----")
                .append(HTTP_PARAM).append(this.get(HTTP_PARAM)).append(TU);
            builder.append("+----")
                .append(HTTP_REQUEST).append(this.get(HTTP_REQUEST)).append(TU);
            builder.append("+--------")
                .append(HTTP_CONNECTION).append(this.get(HTTP_CONNECTION)).append(TU);
            builder.append("+--------")
                .append(HTTP_WRITE).append(this.get(HTTP_WRITE)).append(TU);
            builder.append("+--------")
                .append(HTTP_RESPOSE).append(this.get(HTTP_RESPOSE)).append(TU);
            builder.append("+----")
                .append(BODY_SUPPORT).append(this.get(BODY_SUPPORT)).append(TU);
            if (this.getOrDefault(BODY_EXCEPTION, 0L) > 0) {
                builder.append("+----")
                    .append(BODY_EXCEPTION).append(
                    this.getOrDefault(BODY_EXCEPTION, 0L)).append(TU);
            }
            return builder.toString();
        }

        public static final Elapsed get() {
            Elapsed o = THREAD_LOCAL.get();
            if (o == null) {
                o = new Elapsed();
                THREAD_LOCAL.set(o);
            }
            return o;
        }

        public long start(String key) {
            long now = System.currentTimeMillis();
            this.put(key, now);
            return now;
        }

        public long end(String key) {
            long start = this.getOrDefault(key, 0L);
            if (start < 1) {
                return -1L;
            }
            long timeUsed = System.currentTimeMillis() - start;
            this.replace(key, timeUsed);
            return timeUsed;
        }

        public void reset() {
            this.clear();
            THREAD_LOCAL.remove();
        }
    }

    public static class Data {

        private String matchValue;
        private DataMatcher dataMatcher;
        private Supplier<String> valueSupplier;
    }

    public static class Http {

        private static final Http http = new Http(3000, 3000);
        private int readTimeout;
        private int connectionTimeout;


        public Http(int readTimeout, int connectionTimeout) {
            this.readTimeout = readTimeout;
            this.connectionTimeout = connectionTimeout;
        }

        public static final int readTimeOut() {
            return http.readTimeout;
        }

        public static final int connectionTimeOut() {
            return http.connectionTimeout;
        }

        public static void connectionTimeOut(int connectTimeout) {
            http.connectionTimeout = connectTimeout;
        }

        public static void readTimeout(int readTimeout) {
            http.readTimeout = readTimeout;
        }
    }

    public static class DataProvider {

        private Set<String> requiredParams;
        private Map<String, Data> paramDatas;

        public DataProvider addRequiredParam(String... paramNames) {
            if (requiredParams == null) {
                requiredParams = new HashSet<>();
            }
            requiredParams.addAll(Arrays.asList(paramNames));
            return this;
        }

        public DataProvider addData(String key, Supplier<String> value) {
            return this.addData(key, value, null, defaultDataMatcher);
        }

        public DataProvider addData(String key, String value) {
            return this.addData(key, value, null, defaultDataMatcher);
        }

        /**
         * 添加数据
         *
         * @param key     数据的Key
         * @param value   数据
         * @param matcher 数据匹配规则
         * @return this
         */
        public DataProvider addData(String key, String value, DataMatcher matcher) {
            return this.addData(key, value, null, matcher);
        }

        public DataProvider addData(String key, Supplier<String> value, DataMatcher matcher) {
            return this.addData(key, value, null, matcher);
        }

        public DataProvider addData(String key, String value,
            String matchValue) {
            return this.addData(key, value, matchValue, defaultDataMatcher);
        }

        public DataProvider addData(String key, Supplier<String> value,
            String matchValue) {
            return this.addData(key, value, matchValue, defaultDataMatcher);
        }

        public DataProvider addData(String key, String value,
            String matchValue, DataMatcher dataMatcher) {
            return this.addData(key, () -> value,
                matchValue, dataMatcher);
        }

        public DataProvider addData(String key, Supplier<String> value,
            String matchValue, DataMatcher dataMatcher) {
            Data data = new Data();
            data.valueSupplier = value;
            data.matchValue = matchValue;
            data.dataMatcher = dataMatcher;
            Optional.ofNullable(paramDatas)
                .orElseGet(() -> paramDatas = new HashMap(16))
                .put(key, data);
            return this;
        }

        public Object getData(String key, Object currentKeyValue) {
            if (paramDatas == null || paramDatas.isEmpty()) {
                return currentKeyValue;
            }
            Data data = paramDatas.get(key);
            if (data == null) {
                return currentKeyValue;
            }
            if (!data.dataMatcher.match(currentKeyValue, data.matchValue)) {
                return currentKeyValue;
            }
            return data.valueSupplier.get();
        }

        public Set<String> getRequiredParams() {
            if (requiredParams == null || requiredParams.isEmpty()) {
                return Collections.emptySet();
            }
            return new HashSet<>(requiredParams);
        }
    }

    public static class Signature {

        private static final Signature SIGNATURE = new Signature();

        private String signKey = "sign";
        private Function<Map<String, String>, String> signFun;

        public void config(String signKey,
            Function<Map<String, String>, String> signFun) {
            this.signKey = signKey;
            this.signFun = signFun;
        }

        public String sign(Map<String, String> params) {
            return signFun == null ? null : signFun.apply(params);
        }

        public String signKey() {
            return signKey;
        }

        public void putIfSignable(Map<String, String> params) {
            String sign = this.sign(params);
            if (sign != null && params != null) {
                params.put(this.signKey(), sign);
            }
        }
    }

    private static final DataProvider DATA_PROVIDER = new DataProvider();
    public static final DataMatcher defaultDataMatcher = Objects::equals;
    private static final List<String> GLOBAL_REQUESTPARAMS = new ArrayList<>();
    private static Consumer<Throwable> globalException;
    private static JsonParser jsonParser = new JackjsonJsonParser();

    public static Signature getSignature() {
        return Signature.SIGNATURE;
    }

    public static DataProvider getDataProvider() {
        return DATA_PROVIDER;
    }

    public static JsonParser getJsonParser() {
        return jsonParser;
    }

    public static void setJsonParser(JsonParser jsonParser) {
        __BeeEnvironment.jsonParser = jsonParser;
    }

    public static void addGlobalRequestParam(String[] paramNames) {
        Collections.addAll(GLOBAL_REQUESTPARAMS, paramNames);
    }

    public static void setGlobalException(Consumer<Throwable> consumer) {
        globalException = consumer;
    }

    public static Consumer<Throwable> getGlobalException() {
        return globalException;
    }

    public static String getLocation() {
        return EnvConfiguration.ENV_CONFIGURATION
            .location().getLocation();
    }
}
