package bee.com.nascent.maven.plugin.process;

import java.util.List;
import java.util.function.Function;

/**
 * .
 *
 * @author zhangmsh
 * @version 1.0.0
 * @date 2020/5/17
 */
public interface StepBodySupport<T> {

  /**
   * 注册一个键值对
   *
   * @param fun
   * @return
   */
  default StepRegister onData(Function<T, Param> fun) {
    return this.onData(fun, false);
  }

  /**
   * 配置必须请求参数
   *
   * @param fun 参数提供者
   * @return
   */
  default StepRegister onRequriedData(Function<T, Param> fun) {
    return this.onData(fun, true);
  }

  /**
   * 配置请求参数
   *
   * @param fun 参数提供者
   * @param required 是否必须
   * @return
   */
  StepRegister onData(Function<T, Param> fun, boolean required);

  /**
   * 配置请求参数
   *
   * @param fun 参数提供者
   * @return
   */
  default StepRegister onDatas(Function<T, List<Param>> fun) {
    return this.onDatas(fun, false);
  }

  /**
   * 配置必须请求参数
   *
   * @param fun 参数提供者
   * @return
   */
  default StepRegister onRequiredDatas(Function<T, List<Param>> fun) {
    return this.onDatas(fun, true);
  }

  /**
   * 配置请求参数
   *
   * @param fun 参数提供者
   * @param required 是否必须
   * @return
   */
  StepRegister onDatas(Function<T, List<Param>> fun, boolean required);
}
