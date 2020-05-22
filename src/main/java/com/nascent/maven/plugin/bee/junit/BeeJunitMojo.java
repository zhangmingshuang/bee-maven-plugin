package com.nascent.maven.plugin.bee.junit;

import com.nascent.maven.plugin.bee.mojo.AbstractBeeMojo;
import com.nascent.maven.plugin.bee.mojo.BeeTestFileBuilder;
import com.nascent.maven.plugin.bee.mojo.MojoEnv;
import com.nascent.maven.plugin.bee.utils.FileExtUtil;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.Optional;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.plugins.annotations.Mojo;

/**
 * .
 *
 * @author zhangmsh
 * @version 1.0.0
 * @date 2020/5/15
 */
@Mojo(name = "junit")
public class BeeJunitMojo extends AbstractBeeMojo {

    private Path folder;

    @Override
    protected void initEnv() {
        super.initEnv();
        //清空旧目录
        folder = Paths.get(MojoEnv.getUserDir(), MojoEnv.getTestDir());
        try {
            FileExtUtil.ensureExistEmptyDir(folder.resolve("bee/com/nascent/maven/plugin"));
        } catch (IOException e) {
            MojoEnv.getLogger().error(e);
        }
        this.processJavaCopy();
    }

    @SuppressWarnings("java:S2674")
    private void processJavaCopy() {
        //进行依赖复制
        PluginDescriptor pluginDescriptor = MojoEnv.getPluginDescriptor();
        try (JarFile jarFile = new JarFile(pluginDescriptor.getSource())) {
            Set<JarEntry> javaFiles = jarFile.stream()
                .filter(e -> !e.isDirectory() && e.getName().endsWith("java"))
                .collect(Collectors.toSet());
            Path path = Paths.get(MojoEnv.getUserDir(), MojoEnv.getTestDir(),
                MojoEnv.getProcessPackage());
            //清空目录
            FileExtUtil.ensureExistEmptyDir(path);
            for (JarEntry javaFile : javaFiles) {
                InputStream inputStream = jarFile.getInputStream(javaFile);
                byte[] bytes = new byte[inputStream.available()];
                inputStream.read(bytes);

                String name = javaFile.getName();
                Path file = path.resolve(name);
                try (FileChannel fc = new RandomAccessFile(file.toFile(), "rw").getChannel()) {
                    fc.write(ByteBuffer.wrap(bytes));
                }
            }
        } catch (IOException e) {
            MojoEnv.getLogger().errorAndExit(e);
        }
    }

    @Override
    @SuppressWarnings("java:S1612")
    protected void parseClass(CtClass aClass) throws NotFoundException {
        try {
            BeeTestFileBuilder.builder(aClass)
                .javaFile("bee.com.nascent.maven.plugin")
                .writeTo(folder);
        } catch (Throwable e) {
            Optional<CtMethod> threadMethod = Optional.ofNullable(MojoEnv.getThreadMethod());
            String methodName = threadMethod.map(c -> c.getName()).orElse("");

            MojoEnv.getLogger().error("error on " + aClass.getName()
                + "#" + methodName);
            MojoEnv.getLogger().errorAndExit(e);
        }
    }

    @Override
    protected void afterParsed() {
        //依赖文件复制
        ClassLoader classLoader = BeeJunitMojo.class.getClassLoader();
        try {
            Enumeration<URL> resources = classLoader.getResources("/support");
            while (resources.hasMoreElements()) {
                MojoEnv.getLogger().info(resources.nextElement().toString());
            }
        } catch (IOException e) {
            MojoEnv.getLogger().errorAndExit(e);
        }
    }
}
