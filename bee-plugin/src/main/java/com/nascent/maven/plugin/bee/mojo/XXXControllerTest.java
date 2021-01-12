package com.nascent.maven.plugin.bee.mojo;

import bee.com.nascent.maven.plugin.BeeApplication;
import bee.com.nascent.maven.plugin.BeeAsserts;
import bee.com.nascent.maven.plugin.process.AbstractBeeMethodArgs;
import bee.com.nascent.maven.plugin.process.AbstractBeeMethodDelegate;
import bee.com.nascent.maven.plugin.process.DataAddition;
import bee.com.nascent.maven.plugin.process.Param;
import bee.com.nascent.maven.plugin.process.junit.BeeJunitAsserts;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * The Demo.
 *
 * @author zhangmsh
 * @version 1.0.0
 * @date 2020/5/15
 */
@SuppressWarnings({"java:S3752", "java:S119", "java:S117"})
public class XXXControllerTest {

  public static XMethodDelegate xMethod() {
    return new XMethodDelegate("requestMappingUrl");
  }

  public static void main(String[] args) throws Throwable {
    BeeAsserts<XXP> xx =
        XXXControllerTest.xMethod()
            .args()
            .param("xx")
            .toRequestBody()
            .mock(new XXP())
            .asserts()
            .isTrue(xxp -> xxp.a == 0);

    BeeApplication.stepPrepositionRegister()
        .register(xx)
        .onData(x -> new Param("key", x.a))
        .andThen(new BeeJunitAsserts<XXP2>(new XXP2()))
        .onData(x -> new Param("key2", x.bb));

    BeeApplication.globalRequestParamRegister()
        //            .register("name")
        .onParam("test", () -> "hai")
        .sign(
            "sign",
            values
            // Do Sign
            -> "sign");
    //        BeeEnv.setLogger(new Logger(new TestLogger()));
    //        String s = BeeTestFileBuilder
    //            .builder(BeeEnv.getClassPool().get(XXXController.class.getName()))
    //            .javaCode();
    //        System.out.println(s);
  }

  public static class XXP {

    private int a;
  }

  public static class XXP2 {

    private int bb;
  }

  @RequestMapping("/")
  public static class XXXController {

    @RequestMapping("/x")
    public XXP isJoinedActivity(String groupId, String nick, String activityGuid) {
      return new XXP();
    }
  }

  public static class XMethodArgs<XXP> extends AbstractBeeMethodArgs<XXP> {

    public XMethodArgs(DataAddition d) {
      super(d);
    }

    public XMethodArgs<XXP> param(String param) {
      String key = "param";
      super._bee_add_Reqest_Params(key, param);
      return this;
    }
  }

  public static class XMethodDelegate extends AbstractBeeMethodDelegate {

    public XMethodDelegate(String _bee_Mapping_url) {
      super(_bee_Mapping_url);
    }

    @Override
    public XMethodArgs<XXP> args() {
      return new XMethodArgs(this);
    }

    @Override
    public Class _bee_Return_Type_Class() {
      return XXP.class;
    }
  }
}
