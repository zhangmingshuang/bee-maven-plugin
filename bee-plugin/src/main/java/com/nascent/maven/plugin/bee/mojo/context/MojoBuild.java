package com.nascent.maven.plugin.bee.mojo.context;

import com.nascent.maven.plugin.bee.utils.StringUtils;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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

  private static final Pattern DOT = Pattern.compile(".", Pattern.LITERAL);

  @Delegate private final Build build;

  private MojoBuild(Build build) {
    this.build = build;
  }

  protected static MojoBuild newInstance(Build build) {
    return new MojoBuild(build);
  }

  /**
   * replace the package to path url.
   *
   * @param packageName the package name.
   * @return the path url. eg: {@code com.xxx} return {@code com/xxx}
   */
  public String packageToPath(String packageName) {
    if (StringUtils.isEmpty(packageName)) {
      return "";
    }
    return DOT.matcher(packageName).replaceAll(Matcher.quoteReplacement("/"));
  }
}
