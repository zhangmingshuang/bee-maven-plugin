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

  <T> StepBodySupport<T> register(bee.com.nascent.maven.plugin.BeeAsserts<T> asserts);

  <T> StepBodySupport<T> register(BeeHttpRequestBodySupport<T> support);

  //    String getValue(String key);

  <T> StepBodySupport<T> andThen(BeeAsserts<T> asserts);
}
