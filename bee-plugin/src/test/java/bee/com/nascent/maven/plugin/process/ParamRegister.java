package bee.com.nascent.maven.plugin.process;

import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * .
 *
 * @author zhangmsh
 * @version 1.0.0
 * @date 2020/5/18
 */
public interface ParamRegister {

  default ParamRegister location(String location) {
    BeeApplication.configuration().location(location);
    return this;
  }

  default ParamRegister onRequiredParam(String keyName, Supplier<String> valueSupplier) {
    return this.onParam(keyName, valueSupplier, true);
  }

  default ParamRegister onRequiredParam(String keyName, String value) {
    return this.onParam(keyName, value, true);
  }

  default ParamRegister onParam(String keyName, Supplier<String> valueSupplier) {
    return this.onParam(keyName, valueSupplier, false);
  }

  default ParamRegister onParam(String keyName, String value) {
    return this.onParam(keyName, value, false);
  }

  ParamRegister onParam(String keyName, Supplier<String> valueSupplier, boolean required);

  ParamRegister onParam(String keyName, String value, boolean required);

  ParamRegister sign(String signKeyName, Function<Map<String, String>, String> fun);
}
