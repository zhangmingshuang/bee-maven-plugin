package bee.com.nascent.maven.plugin.process;

import bee.com.nascent.maven.plugin.BeeAsserts;

/**
 * .
 *
 * @author zhangmsh
 * @version 1.0.0
 * @date 2020/5/18
 */
public class DefaultStepRegister implements StepRegister {

  @Override
  public <T> StepBodySupport<T> register(bee.com.nascent.maven.plugin.BeeAsserts<T> asserts) {
    return new DefaultStepBody(asserts.body(), this);
  }

  @Override
  public <T> StepBodySupport<T> register(BeeHttpRequestBodySupport<T> support) {
    return this.register(support.asserts());
  }

  //    @Override
  //    public String getValue(String key) {
  //        return BeeEnvironment.getDataProvider().getData(key, null);
  //    }

  @Override
  public <T> StepBodySupport<T> andThen(BeeAsserts<T> beeAsserts) {
    return new DefaultStepBody<>(beeAsserts.body(), this);
  }
}
