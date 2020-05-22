package com.nascent.maven.plugin.bee.mojo;

import bee.com.nascent.maven.plugin.process.BeeAsserts;
import java.io.File;
import javassist.ClassPool;
import javassist.CtMethod;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.project.MavenProject;

/**
 * .
 *
 * @author zhangmsh
 * @version 1.0.0
 * @date 2020/5/15
 */
@SuppressWarnings("java:S5164")
public class MojoEnv {

    private MojoEnv() {

    }

    public static final String USER_DIR = System.getProperty("user.dir");
    public static final ClassPool CLASS_POOL = ClassPool.getDefault();
    public static final String SEPARATOR = System.getProperty("line.separator");
    private static Logger logger;
    private static String testDir;
    private static boolean httpRequestOnly;
    private static MavenProject mavenProject;
    private static PluginDescriptor pluginDescriptor;
    private static final ThreadLocal METHOD_THREAD_LOCAL = new ThreadLocal();
    private static final String PROCESS_PACKAGE =
        BeeAsserts.class.getPackage().getName().replace(".", File.separator);
    private static String location;

    public static String getProcessPackage() {
        return PROCESS_PACKAGE;
    }

    public static boolean isHttpRequestOnly() {
        return httpRequestOnly;
    }

    public static String getUserDir() {
        return USER_DIR;
    }

    public static ClassPool getClassPool() {
        return CLASS_POOL;
    }

    public static void setLogger(Logger log) {
        logger = log;
    }

    public static Logger getLogger() {
        return logger;
    }

    public static String getLocation() {
        return location;
    }

    public static void setLocation(String l) {
        location = l;
    }

    public static void setTestDir(String dir) {
        testDir = dir;
    }

    public static String getTestDir() {
        return testDir;
    }

    public static void setHttpRequestOnly(boolean b) {
        httpRequestOnly = b;
    }

    public static void setThreadMethod(CtMethod object) {
        METHOD_THREAD_LOCAL.set(object);
    }

    public static CtMethod getThreadMethod() {
        Object o = METHOD_THREAD_LOCAL.get();
        if (o == null) {
            return null;
        }
        return (CtMethod) o;
    }

    public static void setMavenProject(MavenProject mavenProject) {
        MojoEnv.mavenProject = mavenProject;
    }

    public static MavenProject getMavenProject() {
        return mavenProject;
    }

    public static void setPluginDescriptor(
        PluginDescriptor pluginDescriptor) {
        MojoEnv.pluginDescriptor = pluginDescriptor;
    }

    public static PluginDescriptor getPluginDescriptor() {
        return pluginDescriptor;
    }


}
