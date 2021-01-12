package bee.com.nascent.maven.plugin;

import bee.com.nascent.maven.plugin.process.DefaultParamRegister;
import bee.com.nascent.maven.plugin.process.DefaultStepRegister;
import bee.com.nascent.maven.plugin.process.EnvConfiguration;
import bee.com.nascent.maven.plugin.process.ParamRegister;
import bee.com.nascent.maven.plugin.process.StepRegister;
import java.util.function.Consumer;

/**
 * .
 *
 * @author zhangmsh
 * @version 1.0.0
 * @date 2020/5/17
 */
public interface BeeApplication {

  /**
   * 流程步骤注册器
   *
   * @return 流程注册器
   */
  static StepRegister stepPrepositionRegister() {
    return new DefaultStepRegister();
  }

  /**
   * 全局请求参数注册器
   *
   * @return 参数注册器
   */
  static ParamRegister globalRequestParamRegister() {
    return new DefaultParamRegister();
  }

  /**
   * 配置
   *
   * @return
   * @since 1.1.1
   */
  static EnvConfiguration configuration() {
    return EnvConfiguration.ENV_CONFIGURATION;
  }

  /**
   * 全局异常配置
   *
   * @param consumer 异常消费器
   * @return
   * @since 1.1.0
   * @deprecated 建议使用 {@link #configuration()}进行设置
   */
  @Deprecated
  static EnvConfiguration globalException(Consumer<Throwable> consumer) {
    return EnvConfiguration.ENV_CONFIGURATION.globalException(consumer);
  }
}
