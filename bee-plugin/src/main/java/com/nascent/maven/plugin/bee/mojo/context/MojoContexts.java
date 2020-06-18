package com.nascent.maven.plugin.bee.mojo.context;

import com.nascent.maven.plugin.bee.mojo.AbstractBeeMojo;
import java.util.HashMap;
import java.util.Map;
import org.apache.maven.model.Build;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.project.MavenProject;

/**
 * .
 *
 * @author zhangmsh
 * @version 1.0.0
 * @date 2020/5/15
 */
public class MojoContexts {

    private MojoContexts() {

    }

    /**
     * 初始化
     */
    public static void init(AbstractBeeMojo mojo) {
        MojoLog mojoLog = MojoLog.newInstance(mojo.getLog());
        MojoContexts.addMojoContext(mojoLog);

        PluginDescriptor pluginDescriptor
            = (PluginDescriptor) mojo.getPluginContext().get("pluginDescriptor");
        MojoPlugin mojoPlugin = MojoPlugin.newInstance(pluginDescriptor);
        MojoContexts.addMojoContext(mojoPlugin);

        MavenProject mavenProject = mojo.getMavenProject();
        MojoProject mojoProject = MojoProject.newInstance(mavenProject);
        MojoContexts.addMojoContext(mojoProject);

        Build build = mavenProject.getBuild();
        MojoBuild mojoBuild = MojoBuild.newInstance(build);
        MojoContexts.addMojoContext(mojoBuild);
    }

    private static final ThreadLocal<Map<Class, Context>> MOJO_CONTEXTS = new ThreadLocal<>();

    private static void addMojoContext(Context mojoContext) {
        Map<Class, Context> map = MOJO_CONTEXTS.get();
        if (map == null) {
            map = new HashMap<>(16);
            MOJO_CONTEXTS.set(map);
        }
        map.put(mojoContext.getClass(), mojoContext);
    }

    public static <T extends Context> T getMojoContext(Class<T> clazz) {
        Context mojoContext = MOJO_CONTEXTS.get().get(clazz);
        return (T) mojoContext;
    }

    public static MojoLog getLogger() {
        return getMojoContext(MojoLog.class);
    }

    public static MojoPlugin getPlugin() {
        return getMojoContext(MojoPlugin.class);
    }

    public static MojoBuild getBuild() {
        return getMojoContext(MojoBuild.class);
    }

    public static MojoProject getProject() {
        return getMojoContext(MojoProject.class);
    }
}
