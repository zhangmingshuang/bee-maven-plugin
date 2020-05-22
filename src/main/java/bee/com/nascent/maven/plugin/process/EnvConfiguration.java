package bee.com.nascent.maven.plugin.process;

import java.util.function.Consumer;

/**
 * .
 *
 * @author zhangmsh
 * @version 1.0.0
 * @date 2020/5/21
 */
public final class EnvConfiguration {

    protected static final EnvConfiguration ENV_CONFIGURATION = new EnvConfiguration();

    public EnvConfiguration jsonParser(JsonParser jsonParser) {
        BeeEnvironment.setJsonParser(jsonParser);
        return this;
    }

    public JsonParser getJsonParser() {
        return BeeEnvironment.getJsonParser();
    }

    public EnvConfiguration globalException(Consumer<Throwable> consumer) {
        BeeEnvironment.setGlobalException(consumer);
        return this;
    }
}
