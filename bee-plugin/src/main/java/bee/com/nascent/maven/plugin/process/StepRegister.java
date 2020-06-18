package bee.com.nascent.maven.plugin.process;

import bee.com.nascent.maven.plugin.BeeAsserts;

/**
 * .
 *
 * @author zhangmsh
 * @version 1.0.0
 * @date 2020/5/18
 */
public interface StepRegister {

    /**
     * 注册流程步骤
     *
     * @param asserts 断言器
     * @param <T>
     * @return
     */
    <T> StepBodySupport<T> register(BeeAsserts<T> asserts);

    /**
     * 注册流程步骤
     *
     * @param support 提供器
     * @param <T>
     * @return
     */
    <T> StepBodySupport<T> register(BeeHttpRequestBodySupport<T> support);

    /**
     * 继续
     *
     * @param asserts
     * @param <T>
     * @return
     */
    <T> StepBodySupport<T> andThen(BeeAsserts<T> asserts);
}
