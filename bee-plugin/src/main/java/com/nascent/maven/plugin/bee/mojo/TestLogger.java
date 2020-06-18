package com.nascent.maven.plugin.bee.mojo;

import java.io.IOException;
import lombok.experimental.Delegate;
import org.apache.maven.plugin.logging.Log;

/**
 * .
 *
 * @author zhangmsh
 * @version 1.0.0
 * @date 2020/5/16
 */
@SuppressWarnings({"*", "java:S1186", "java:S1128"})
public class TestLogger implements Log {

    @Override
    public boolean isDebugEnabled() {
        return true;
    }

    @Override
    public void debug(CharSequence charSequence) {
        System.out.println(charSequence);
    }

    @Override
    public void debug(CharSequence charSequence, Throwable throwable) {
        System.out.println(charSequence);
        throwable.printStackTrace();
    }

    @Override
    public void debug(Throwable throwable) {
        throwable.printStackTrace();
    }

    @Override
    public boolean isInfoEnabled() {
        return true;
    }

    @Override
    public void info(CharSequence charSequence) {
        System.out.println(charSequence);
    }

    @Override
    public void info(CharSequence charSequence, Throwable throwable) {
        System.out.println(charSequence);
        throwable.printStackTrace();
    }

    @Override
    public void info(Throwable throwable) {
        throwable.printStackTrace();
    }

    @Override
    public boolean isWarnEnabled() {
        return true;
    }

    @Override
    public void warn(CharSequence charSequence) {
        System.out.println(charSequence);
    }

    @Override
    public void warn(CharSequence charSequence, Throwable throwable) {
        System.out.println(charSequence);
        throwable.printStackTrace();
    }

    @Override
    public void warn(Throwable throwable) {
        throwable.printStackTrace();
    }

    @Override
    public boolean isErrorEnabled() {
        return true;
    }

    @Override
    public void error(CharSequence charSequence) {
        System.out.println(charSequence);
    }

    @Override
    public void error(CharSequence charSequence, Throwable throwable) {
        System.out.println(charSequence);
        throwable.printStackTrace();
    }

    @Override
    public void error(Throwable throwable) {
        throwable.printStackTrace();
    }

    public void errorAndExit(Throwable e) {
        e.printStackTrace();
    }
}
