package com.nascent.maven.plugin.bee.mojo;

import lombok.experimental.Delegate;
import org.apache.maven.plugin.logging.Log;

/**
 * .
 *
 * @author zhangmsh
 * @version 1.0.0
 * @date 2020/5/15
 */
public class Logger {

    @Delegate
    private Log log;

    public Logger(Log log) {
        this.log = log;
    }

    public void errorAndExit(Throwable e) {
        this.error(e);
        System.exit(0);
    }

}
