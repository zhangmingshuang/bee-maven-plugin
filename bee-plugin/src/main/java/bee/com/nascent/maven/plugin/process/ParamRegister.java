package bee.com.nascent.maven.plugin.process;

import bee.com.nascent.maven.plugin.BeeApplication;
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

  /**
   * 配置请求Location
   *
   * @param location
   * @return
   */
  default ParamRegister location(String location) {
    BeeApplication.configuration().location().location(location);
    return this;
  }

  /**
   * 配置必须参数， 必须参数表示如果不存在则也自动填充
   *
   * @param keyName 参数名称
   * @param valueSupplier 参数提供器
   * @return
   */
  default ParamRegister onRequiredParam(String keyName, Supplier<String> valueSupplier) {
    return this.onParam(keyName, valueSupplier, true);
  }

  /**
   * 配置必须参数， 必须参数表示如果不存在则也自动填充
   *
   * @param keyName 参数名称
   * @param value 参数值
   * @return
   */
  default ParamRegister onRequiredParam(String keyName, String value) {
    return this.onParam(keyName, value, true);
  }

  /**
   * 配置参数
   *
   * @param keyName 参数名称
   * @param valueSupplier 参数提供器
   * @return
   */
  default ParamRegister onParam(String keyName, Supplier<String> valueSupplier) {
    return this.onParam(keyName, valueSupplier, false);
  }

  /**
   * 配置参数
   *
   * @param keyName 参数名称
   * @param value 参数值
   * @return
   */
  default ParamRegister onParam(String keyName, String value) {
    return this.onParam(keyName, value, false);
  }

  /**
   * 配置参数
   *
   * @param keyName 参数名称
   * @param valueSupplier 参数提供器
   * @param required 是否必须， 如果是必须的，则不管请求数据是否存在都会进行填充
   * @return
   */
  ParamRegister onParam(String keyName, Supplier<String> valueSupplier, boolean required);

  /**
   * 配置参数
   *
   * @param keyName 参数名称
   * @param value 参数值
   * @param required 是否必须， 如果是必须的，则不管请求数据是否存在都会进行填充
   * @return
   */
  ParamRegister onParam(String keyName, String value, boolean required);

  /**
   * 签名
   *
   * @param signKeyName 签名参数名
   * @param fun 签名函数
   * @return
   */
  ParamRegister sign(String signKeyName, Function<Map<String, String>, String> fun);
}
