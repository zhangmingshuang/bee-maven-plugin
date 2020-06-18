package com.nascent.maven.plugin.bee.mojo.support;

import org.apache.commons.logging.Log;

/**
 * .
 *
 * @author zhangmsh
 * @version 1.0.0
 * @date 2020/6/15
 */
public class EmptyLog implements Log {

    private Log log;

    private EmptyLog(Log log) {
        this.log = log;
    }

    public static Log empty(Log old) {
        return new EmptyLog(old);
    }

    @Override
    public void debug(Object o) {

    }

    @Override
    public void debug(Object o, Throwable throwable) {

    }

    @Override
    public void error(Object o) {
        log.error(o);
    }

    @Override
    public void error(Object o, Throwable throwable) {
        log.error(o, throwable);
    }

    @Override
    public void fatal(Object o) {
        log.fatal(o);
    }

    @Override
    public void fatal(Object o, Throwable throwable) {
        log.fatal(o, throwable);
    }

    @Override
    public void info(Object o) {
        log.info(o);
    }

    @Override
    public void info(Object o, Throwable throwable) {
        log.info(o, throwable);
    }

    @Override
    public boolean isDebugEnabled() {
        return false;
    }

    @Override
    public boolean isErrorEnabled() {
        return true;
    }

    @Override
    public boolean isFatalEnabled() {
        return true;
    }

    @Override
    public boolean isInfoEnabled() {
        return true;
    }

    @Override
    public boolean isTraceEnabled() {
        return true;
    }

    @Override
    public boolean isWarnEnabled() {
        return true;
    }

    @Override
    public void trace(Object o) {
        log.trace(o);
    }

    @Override
    public void trace(Object o, Throwable throwable) {
        log.trace(o, throwable);
    }

    @Override
    public void warn(Object o) {
        log.warn(o);
    }

    @Override
    public void warn(Object o, Throwable throwable) {
        log.warn(o, throwable);
    }
}
