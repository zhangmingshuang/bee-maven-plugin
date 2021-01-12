package com.nascent.maven.plugin.bee.mojo.context;

import com.nascent.maven.plugin.bee.utils.StringUtils;
import lombok.experimental.Delegate;
import org.apache.maven.model.Build;

/**
 * .
 *
 * @author zhangmsh
 * @version 1.0.0
 * @date 2020/6/15
 */
public class MojoBuild implements Context {

  @Delegate private Build build;

  private MojoBuild(Build build) {
    this.build = build;
  }

  protected static MojoBuild newInstance(Build build) {
    return new MojoBuild(build);
  }

  public String packageToPath(String packageName) {
    if (StringUtils.isEmpty(packageName)) {
      return "";
    }
    return packageName.replace(".", "/");
  }
}
