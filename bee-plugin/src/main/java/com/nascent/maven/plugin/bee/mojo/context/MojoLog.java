package com.nascent.maven.plugin.bee.mojo.context;

import lombok.experimental.Delegate;
import org.apache.maven.plugin.logging.Log;

/**
 * .
 *
 * @author zhangmsh
 * @version 1.0.0
 * @date 2020/6/15
 */
public class MojoLog implements Context {

  @Delegate private Log log;

  private MojoLog(Log log) {
    this.log = log;
  }

  protected static MojoLog newInstance(Log log) {
    return new MojoLog(log);
  }

  public void errorAndExit(Throwable e) {
    this.error(e);
    System.exit(0);
  }
}
