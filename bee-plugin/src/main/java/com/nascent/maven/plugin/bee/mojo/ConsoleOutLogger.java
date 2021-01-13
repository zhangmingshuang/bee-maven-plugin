package com.nascent.maven.plugin.bee.mojo;

import org.apache.maven.plugin.logging.Log;

/**
 * .
 *
 * @author zhangmsh
 * @version 1.0.0
 * @date 2020/5/16
 */
@SuppressWarnings({
  "*",
  "java:S1186",
  "java:S1128",
  "UseOfSystemOutOrSystemErr",
  "CallToPrintStackTrace"
})
public class ConsoleOutLogger implements Log {

  @Override
  public boolean isDebugEnabled() {
    return true;
  }

  @Override
  public void debug(CharSequence content) {
    System.out.println(content);
  }

  @Override
  public void debug(CharSequence content, Throwable error) {
    System.out.println(content);
    error.printStackTrace();
  }

  @Override
  public void debug(Throwable error) {
    error.printStackTrace();
  }

  @Override
  public boolean isInfoEnabled() {
    return true;
  }

  @Override
  public void info(CharSequence content) {
    System.out.println(content);
  }

  @Override
  public void info(CharSequence content, Throwable error) {
    System.out.println(content);
    error.printStackTrace();
  }

  @Override
  public void info(Throwable error) {
    error.printStackTrace();
  }

  @Override
  public boolean isWarnEnabled() {
    return true;
  }

  @Override
  public void warn(CharSequence content) {
    System.out.println(content);
  }

  @Override
  public void warn(CharSequence content, Throwable error) {
    System.out.println(content);
    error.printStackTrace();
  }

  @Override
  public void warn(Throwable error) {
    error.printStackTrace();
  }

  @Override
  public boolean isErrorEnabled() {
    return true;
  }

  @Override
  public void error(CharSequence content) {
    System.out.println(content);
  }

  @Override
  public void error(CharSequence content, Throwable error) {
    System.out.println(content);
    error.printStackTrace();
  }

  @Override
  public void error(Throwable error) {
    error.printStackTrace();
  }

  public void errorAndExit(Throwable e) {
    e.printStackTrace();
  }
}
