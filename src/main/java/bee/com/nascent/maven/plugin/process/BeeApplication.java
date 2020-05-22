package bee.com.nascent.maven.plugin.process;

import java.util.function.Consumer;

/**
 * .
 *
 * @author zhangmsh
 * @version 1.0.0
 * @date 2020/5/17
 */
public interface BeeApplication {

    static StepRegister stepPrepositionRegister() {
        return new DefaultStepRegister();
    }

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
     * @param consumer
     * @return
     * @since 1.1.0
     * @deprecated 建议使用 {@link #configuration()}进行设置
     */
    @Deprecated
    static EnvConfiguration globalException(Consumer<Throwable> consumer) {
        return EnvConfiguration.ENV_CONFIGURATION.globalException(consumer);
    }
}
