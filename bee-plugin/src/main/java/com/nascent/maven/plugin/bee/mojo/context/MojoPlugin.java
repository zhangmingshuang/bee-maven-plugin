package com.nascent.maven.plugin.bee.mojo.context;

import bee.com.nascent.maven.plugin.BeeApplication;
import bee.com.nascent.maven.plugin.process.__BeeEnvironment;
import com.nascent.maven.plugin.bee.constant.Config;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.apache.maven.plugin.descriptor.PluginDescriptor;

/**
 * .
 *
 * @author zhangmsh
 * @version 1.0.0
 * @date 2020/6/15
 */
public class MojoPlugin implements Context {

    private PluginDescriptor pluginDescriptor;
    private Map<String, String> pluginProperties
        = new HashMap<>(2, 1);

    protected static MojoPlugin newInstance(PluginDescriptor pluginDescriptor) {
        return new MojoPlugin(pluginDescriptor);
    }

    private MojoPlugin(PluginDescriptor pluginDescriptor) {
        this.pluginDescriptor = pluginDescriptor;
        this.init();
    }

    public Map<String, String> getPluginProperties() {
        return new HashMap<>(pluginProperties);
    }

    public void addPluginProperties(String key, String value) {
        this.pluginProperties.put(key, value);
    }

    public String getJarFileResource() {
        return pluginDescriptor.getSource();
    }

    public String getResourcetPackageName() {
        return BeeApplication.class.getPackage().getName();
    }

    private void init() {
        //初始化
        this.doPropertiesLoad();
    }

    private void doPropertiesLoad() {
        //扫描本身属性配置
        try (InputStream is = MojoContexts.class.getResourceAsStream(Config.PROPERTIES)) {
            Properties properties = new Properties();
            properties.load(is);
            if (!properties.isEmpty()) {
                properties.stringPropertyNames().forEach(key
                    -> pluginProperties.put(key, properties.getProperty(key)));
            }
        } catch (Throwable e) {
            MojoContexts.getLogger().errorAndExit(e);
        }
    }
}
