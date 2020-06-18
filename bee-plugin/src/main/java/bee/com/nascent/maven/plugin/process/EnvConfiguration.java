package bee.com.nascent.maven.plugin.process;

import bee.com.nascent.maven.plugin.process.json.JsonParser;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.function.Consumer;

/**
 * .
 *
 * @author zhangmsh
 * @version 1.0.0
 * @date 2020/5/21
 */
public final class EnvConfiguration {

    public static final HttpConfiguration HTTP = new HttpConfiguration();
    public static final EnvConfiguration ENV_CONFIGURATION = new EnvConfiguration();
    private static final LocationConfiguration LOCATION = new DefaultLocationConfiguration();

    public static class HttpConfiguration {

        public HttpConfiguration connectionTimeout(int connectTimeout) {
            __BeeEnvironment.Http.connectionTimeOut(connectTimeout);
            return this;
        }

        public HttpConfiguration readTimeout(int readTimeout) {
            __BeeEnvironment.Http.readTimeout(readTimeout);
            return this;
        }

        public EnvConfiguration andThen() {
            return EnvConfiguration.ENV_CONFIGURATION;
        }
    }

    public static interface JsonConfiguration {

        /**
         * 时间格式化配置
         *
         * @param pattern 时间格式
         * @return
         */
        default JsonConfiguration dateFormat(String pattern) {
            return this.dateFormat(new SimpleDateFormat(pattern));
        }

        /**
         * 时间格式化配置
         *
         * @param dateFormat 格式化器
         * @return
         */
        JsonConfiguration dateFormat(DateFormat dateFormat);

        /**
         * 忽略不匹配的字段
         *
         * @return
         */
        JsonConfiguration ignoreOnUnknownProperties();

        /**
         * 继续器
         *
         * @return
         */
        default EnvConfiguration andThen() {
            return EnvConfiguration.ENV_CONFIGURATION;
        }
    }


    public EnvConfiguration jsonParser(JsonParser jsonParser) {
        __BeeEnvironment.setJsonParser(jsonParser);
        return this;
    }

    public JsonParser getJsonParser() {
        return __BeeEnvironment.getJsonParser();
    }

    public EnvConfiguration globalException(Consumer<Throwable> consumer) {
        __BeeEnvironment.setGlobalException(consumer);
        return this;
    }

    public static interface LocationConfiguration {

        /**
         * 配置请求location
         *
         * @param location Location
         * @return
         */
        LocationConfiguration location(String location);

        /**
         * 请求location构建器
         *
         * @param builder 构建器
         * @return
         */
        LocationConfiguration locationBuilder(Builder builder);

        /**
         * 获取请求location
         *
         * @return
         */
        String getLocation();

        /**
         * 构建请求location
         *
         * @param location
         * @param clazz
         * @param method
         * @return
         */
        String buildLocation(String location, Class clazz, Method method);

        /**
         * 是否有配置请求builder
         *
         * @return
         */
        boolean hasBuilder();

        /**
         * 继续
         *
         * @return
         */
        default EnvConfiguration andThen() {
            return EnvConfiguration.ENV_CONFIGURATION;
        }

        /**
         * Location构建器
         */
        interface Builder {

            /**
             * 构建请求Location
             *
             * @param location Location
             * @param clazz    类
             * @param method   方法
             * @return
             */
            String build(String location, Class clazz, Method method);
        }
    }

    public static class DefaultLocationConfiguration implements LocationConfiguration {

        /**
         * 请求location
         */
        private String location;
        /**
         * 请求location构建器
         */
        private Builder builder;

        @Override
        public LocationConfiguration location(String location) {
            this.location = location;
            return this;
        }

        @Override
        public LocationConfiguration locationBuilder(Builder builder) {
            this.builder = builder;
            return this;
        }

        @Override
        public boolean hasBuilder() {
            return builder != null;
        }

        @Override
        public String buildLocation(String url, Class clazz, Method method) {
            return hasBuilder()
                ? builder.build(url, clazz, method)
                : this.location;
        }

        @Override
        public String getLocation() {
            return location;
        }
    }

    public LocationConfiguration location() {
        return EnvConfiguration.LOCATION;
    }

    public EnvConfiguration location(String location) {
        this.location().location(location);
        return this;
    }

    public HttpConfiguration http() {
        return EnvConfiguration.HTTP;
    }
}
