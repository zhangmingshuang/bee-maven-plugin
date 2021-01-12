package bee.com.nascent.maven.plugin;

import bee.com.nascent.maven.plugin.process.AbstractBeeMethodArgs;
import bee.com.nascent.maven.plugin.process.AbstractBeeMethodDelegate;
import bee.com.nascent.maven.plugin.process.DataAddition;
import bee.com.nascent.maven.plugin.process.__BeeEnvironment;
import com.nascent.maven.plugin.bee.mojo.XXXControllerTest;
import java.util.Optional;

/**
 * bee auto build. at 2020-06-11T18:54:45.044
 *
 * @see com.nascent.maven.plugin.bee.mojo.XXXControllerTest$XXXController
 */
public class BeeXXXControllerTest$XXXController {
  /**
   * @see com.nascent.maven.plugin.bee.mojo.XXXControllerTest$XXXController#isJoinedActivity arthas:
   *     trace com.nascent.maven.plugin.bee.mojo.XXXControllerTest$XXXController isJoinedActivity
   */
  public static IsJoinedActivityDelegate isJoinedActivity() {
    String uri =
        Optional.ofNullable(__BeeEnvironment.getLocation()).orElse("http://192.168.10.163:9091");
    return new IsJoinedActivityDelegate(uri + "//x");
  }

  public static class IsJoinedActivityDelegate extends AbstractBeeMethodDelegate {
    public IsJoinedActivityDelegate(String _bee_Mapping_url) {
      super(_bee_Mapping_url);
    }

    public IsJoinedActivityArgs<XXXControllerTest.XXP> args() {
      return new IsJoinedActivityArgs<XXXControllerTest.XXP>(this);
    }

    public Class _bee_Return_Type_Class() {
      return XXXControllerTest.XXP.class;
    }
  }

  public static class IsJoinedActivityArgs<T> extends AbstractBeeMethodArgs<T> {
    public IsJoinedActivityArgs(DataAddition _bee_Data_Addition) {
      super(_bee_Data_Addition);
    }

    public IsJoinedActivityArgs<T> groupId(String groupId) {
      super._bee_add_Reqest_Params("groupId", groupId);
      return this;
    }

    public IsJoinedActivityArgs<T> nick(String nick) {
      super._bee_add_Reqest_Params("nick", nick);
      return this;
    }

    public IsJoinedActivityArgs<T> activityGuid(String activityGuid) {
      super._bee_add_Reqest_Params("activityGuid", activityGuid);
      return this;
    }
  }
}
