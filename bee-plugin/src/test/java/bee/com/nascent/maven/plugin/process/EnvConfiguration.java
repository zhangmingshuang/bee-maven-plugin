package bee.com.nascent.maven.plugin.process;

import bee.com.nascent.maven.plugin.process.json.JsonParser;
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

  public EnvConfiguration location(String location) {
    __BeeEnvironment.setLocation(location);
    return this;
  }

  public HttpConfiguration http() {
    return EnvConfiguration.HTTP;
  }

  public static interface JsonConfiguration {

    default JsonConfiguration dateFormat(String pattern) {
      return this.dateFormat(new SimpleDateFormat(pattern));
    }

    JsonConfiguration dateFormat(DateFormat dateFormat);

    JsonConfiguration ignoreOnUnknownProperties();

    default EnvConfiguration andThen() {
      return EnvConfiguration.ENV_CONFIGURATION;
    }
  }

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
}
