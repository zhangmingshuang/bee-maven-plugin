package com.nascent.maven.plugin.bee.mojo.xml;

import org.apache.maven.plugin.logging.Log;

/**
 * .
 *
 * @author zhangmsh
 * @version 1.0.0
 * @since 2021/1/25
 */
public class TestMojoLog implements Log {

  @Override
  public boolean isDebugEnabled() {
    return false;
  }

  @Override
  public void debug(CharSequence content) {}

  @Override
  public void debug(CharSequence content, Throwable error) {}

  @Override
  public void debug(Throwable error) {}

  @Override
  public boolean isInfoEnabled() {
    return false;
  }

  @Override
  public void info(CharSequence content) {}

  @Override
  public void info(CharSequence content, Throwable error) {}

  @Override
  public void info(Throwable error) {}

  @Override
  public boolean isWarnEnabled() {
    return false;
  }

  @Override
  public void warn(CharSequence content) {}

  @Override
  public void warn(CharSequence content, Throwable error) {}

  @Override
  public void warn(Throwable error) {}

  @Override
  public boolean isErrorEnabled() {
    return false;
  }

  @Override
  public void error(CharSequence content) {}

  @Override
  public void error(CharSequence content, Throwable error) {}

  @Override
  public void error(Throwable error) {
    error.printStackTrace();
  }
}
