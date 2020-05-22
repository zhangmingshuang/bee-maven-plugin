package bee.com.nascent.maven.plugin.process;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
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
public class BeeEnvironment {

    private BeeEnvironment() {

    }

    public static class Data {

        private String matchValue;
        private DataMatcher dataMatcher;
        private Supplier<String> valueSupplier;
    }

    public static final DataMatcher defaultDataMatcher = Objects::equals;

    public static class DataProvider {

        private Map<String, Data> paramDatas;

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
                .orElseGet(() -> paramDatas = new HashMap())
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


    private static final List<String> GLOBAL_REQUESTPARAMS = new ArrayList<>();
    //    private static final Map<String, Supplier<String>> PARAM_VALUE_SUPPLIER = new HashMap<>();
    private static Consumer<Throwable> globalException;
    private static String location;
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
        BeeEnvironment.jsonParser = jsonParser;
    }

    public static void addGlobalRequestParam(String[] paramNames) {
        Collections.addAll(GLOBAL_REQUESTPARAMS, paramNames);
    }

//    public static void addParamValueSupplier(String keyName, Supplier<String> valueSupplier) {
//        PARAM_VALUE_SUPPLIER.put(keyName, valueSupplier);
//    }

//    public static void setSign(String signKeyName, Function<Map<String, String>, String> fun) {
//        signKey = signKeyName;
//        signFun = fun;
//    }
//
//    public static Function<Map<String, String>, String> getSignFun() {
//        return signFun;
//    }
//
//    public static String getSignKey() {
//        return signKey;
//    }

//    public static boolean hasStepData() {
////        return STEP_DATE.size() > 0
////            || !PARAM_VALUE_SUPPLIER.isEmpty();
//    }

//    public static ObjectNode getStepData() {
//        ObjectNode jsonNodes = STEP_DATE.deepCopy();
//        if (!PARAM_VALUE_SUPPLIER.isEmpty()) {
//            PARAM_VALUE_SUPPLIER.forEach((k, supplier) -> {
//                String s = supplier.get();
//                if (s != null) {
//                    jsonNodes.put(k, s);
//                }
//            });
//        }
//        return jsonNodes;
//    }

    public static void setGlobalException(Consumer<Throwable> consumer) {
        globalException = consumer;
    }

    public static Consumer<Throwable> getGlobalException() {
        return globalException;
    }

    public static void setLocation(String location) {
        BeeEnvironment.location = location;
    }

    public static String getLocation() {
        return location;
    }
}
