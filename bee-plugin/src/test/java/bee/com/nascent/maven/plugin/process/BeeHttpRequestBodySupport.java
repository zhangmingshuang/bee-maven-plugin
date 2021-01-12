package bee.com.nascent.maven.plugin.process;

import bee.com.nascent.maven.plugin.BeeAsserts;
import bee.com.nascent.maven.plugin.process.__BeeEnvironment.Elapsed;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.function.Consumer;

/**
 * .
 *
 * @author zhangmsh
 * @version 1.0.0
 * @date 2020/5/15
 */
public interface BeeHttpRequestBodySupport<T> {

  String method();

  BeeAsserts<T> asserts();

  T body();

  default BeeHttpRequestBodySupport<T> printlnBody(String prefix, OutputStream os) {
    T body = body();
    try {
      String bodyStr = prefix;
      if (body == null) {
        bodyStr = "null";
      } else if (body instanceof CharSequence) {
        bodyStr += body.toString();
      } else {
        bodyStr += __BeeEnvironment.getJsonParser().beanToString(body);
      }
      bodyStr += "\n";
      os.write(bodyStr.getBytes(StandardCharsets.UTF_8));
    } catch (IOException e) {
      throw new IoRuntimeException(e);
    }
    return this;
  }

  default BeeHttpRequestBodySupport<T> printlnBody(OutputStream os) {
    return this.printlnBody("body:", os);
  }

  default BeeHttpRequestBodySupport<T> printlnBody() {
    return this.printlnBody("body:" + System.out);
  }

  default BeeHttpRequestBodySupport<T> printlnBody(String prefix) {
    return this.printlnBody(prefix, System.out);
  }

  default BeeHttpRequestBodySupport<T> printlnTimeUsed() {
    return this.printlnTimeUsed("timeUsed:", System.out);
  }

  default BeeHttpRequestBodySupport<T> printlnTimeUsed(String prefix) {
    return this.printlnTimeUsed(prefix, System.out);
  }

  default BeeHttpRequestBodySupport<T> printlnTimeUsed(OutputStream os) {
    return this.printlnTimeUsed("timeUsed:" + os);
  }

  default BeeHttpRequestBodySupport<T> printlnTimeUsed(String prefix, OutputStream os) {
    this.timeUsed(
        message -> {
          try {
            String info = this.requestUrl() + "\n";
            info += (prefix + message);
            os.write(info.getBytes(StandardCharsets.UTF_8));
          } catch (IOException e) {
            throw new IoRuntimeException(e);
          }
        });
    return this;
  }

  default BeeHttpRequestBodySupport<T> timeUsed(Consumer<String> consumer) {
    Elapsed elapsed = Elapsed.get();
    consumer.accept(elapsed.toString());
    return this;
  }

  default BeeHttpRequestBodySupport<T> printlnRequestParams(OutputStream os) {
    return this.printlnRequestParams("requestParams:", os);
  }

  default BeeHttpRequestBodySupport<T> printlnRequestParams() {
    return this.printlnRequestParams("requestParams:", System.out);
  }

  default BeeHttpRequestBodySupport<T> printlnRequestParams(String prefix) {
    return this.printlnRequestParams(prefix, System.out);
  }

  default BeeHttpRequestBodySupport<T> printlnRequestParams(String prefix, OutputStream os) {
    Map params = this.requestParams();
    try {
      String paramsStr = prefix;
      if (params == null) {
        paramsStr += "null";
      } else {
        paramsStr += __BeeEnvironment.getJsonParser().beanToString(params);
      }
      paramsStr += "\n";
      os.write(paramsStr.getBytes(StandardCharsets.UTF_8));
    } catch (IOException e) {
      throw new IoRuntimeException(e);
    }
    return this;
  }

  default BeeHttpRequestBodySupport<T> requestParams(Consumer<Map> consumer) {
    consumer.accept(requestParams());
    return this;
  }

  Map requestParams();

  String requestUrl();
}
