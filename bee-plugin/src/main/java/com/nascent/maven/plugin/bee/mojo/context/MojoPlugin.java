package com.nascent.maven.plugin.bee.mojo.context;

import bee.com.nascent.maven.plugin.BeeApplication;
import com.nascent.maven.plugin.bee.constant.Config;
import java.io.IOException;
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

  private final Map<String, String> pluginProperties = new HashMap<>(2, 1);
  private final PluginDescriptor pluginDescriptor;

  private MojoPlugin(PluginDescriptor pluginDescriptor) {
    this.pluginDescriptor = pluginDescriptor;
    this.init();
  }

  protected static MojoPlugin newInstance(PluginDescriptor pluginDescriptor) {
    return new MojoPlugin(pluginDescriptor);
  }

  private void init() {
    // 初始化
    this.doPropertiesLoad();
  }

  public Map<String, String> getPluginProperties() {
    return new HashMap<>(this.pluginProperties);
  }

  public void addPluginProperties(String key, String value) {
    this.pluginProperties.put(key, value);
  }

  public String getJarFileResource() {
    return this.pluginDescriptor.getSource();
  }

  public String getResourcetPackageName() {
    return BeeApplication.class.getPackage().getName();
  }

  private void doPropertiesLoad() {
    // 扫描本身属性配置
    try (InputStream is = MojoContexts.class.getResourceAsStream(Config.PROPERTIES)) {
      Properties properties = new Properties();
      properties.load(is);
      if (!properties.isEmpty()) {
        properties
            .stringPropertyNames()
            .forEach(key -> this.pluginProperties.put(key, properties.getProperty(key)));
      }
    } catch (IOException e) {
      MojoContexts.getLogger().errorAndExit(e);
    }
  }
}
