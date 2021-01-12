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

  default StepRegister onRequriedData(Function<T, Param> fun) {
    return this.onData(fun, true);
  }

  StepRegister onData(Function<T, Param> fun, boolean required);

  default StepRegister onDatas(Function<T, List<Param>> fun) {
    return this.onDatas(fun, false);
  }

  default StepRegister onRequiredDatas(Function<T, List<Param>> fun) {
    return this.onDatas(fun, true);
  }

  StepRegister onDatas(Function<T, List<Param>> fun, boolean required);
}
