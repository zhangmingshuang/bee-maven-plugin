package com.nascent.maven.plugin.bee.mojo.support;

import com.nascent.maven.plugin.bee.constant.Config;
import com.nascent.maven.plugin.bee.mojo.context.MojoBuild;
import com.nascent.maven.plugin.bee.mojo.context.MojoContexts;
import com.nascent.maven.plugin.bee.mojo.context.MojoPlugin;
import com.nascent.maven.plugin.bee.utils.FileExtUtil;
import com.nascent.maven.plugin.bee.utils.StringUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import lombok.NonNull;

/**
 * .
 *
 * @author zhangmsh
 * @version 1.0.0
 * @date 2020/6/15
 */
public class MojoResourceProcessor {

    /**
     * 生成依赖资源文件
     */
    public static void generateTo(String folder) {
        MojoPlugin plugin = MojoContexts.getPlugin();
        String jarFileSource = plugin.getJarFileResource();
        String packageName = plugin.getResourcetPackageName();
        MojoBuild build = MojoContexts.getBuild();
        String packageFolder = build.packageToPath(packageName);
        Path path = Paths.get(folder).resolve(packageFolder);
        String pluginResourceFolder = plugin.getPluginProperties().get(Config.RESOURCES_TARGET);
        try (JarFile jarFile = new JarFile(jarFileSource)) {
            Set<JarEntry> javaFiles = jarFile.stream()
                .filter(e -> !e.isDirectory() && e.getName().endsWith(Config.JAVA_SUFFIX))
                .collect(Collectors.toSet());

            //清空目录
            FileExtUtil.ensureExistEmptyDir(path);
            for (JarEntry javaFile : javaFiles) {
                InputStream inputStream = jarFile.getInputStream(javaFile);
                byte[] bytes = new byte[inputStream.available()];
                inputStream.read(bytes);

                String name = javaFile.getName();
                if (StringUtils.isEmpty(name)) {
                    continue;
                }
                if (name.indexOf(pluginResourceFolder) != -1) {
                    name = name.replace(pluginResourceFolder, "");
                }
                if (name.charAt(0) == '/') {
                    name = name.substring(1);
                }
                Path file = path.resolve(name);
                if (Files.notExists(file.getParent())) {
                    Files.createDirectories(file.getParent());
                }
                try (FileChannel fc = new RandomAccessFile(file.toFile(), "rw").getChannel()) {
                    fc.write(ByteBuffer.wrap(bytes));
                }
            }
        } catch (IOException e) {
            MojoContexts.getLogger().errorAndExit(e);
        }
    }
}
