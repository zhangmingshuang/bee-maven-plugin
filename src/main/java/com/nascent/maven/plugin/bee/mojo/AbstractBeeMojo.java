package com.nascent.maven.plugin.bee.mojo;

import bee.com.nascent.maven.plugin.process.Param;
import com.nascent.maven.plugin.bee.mojo.xml.BeeXml;
import com.nascent.maven.plugin.bee.utils.CollectionUtils;
import com.nascent.maven.plugin.bee.utils.StringUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import org.apache.maven.artifact.ArtifactUtils;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.DefaultProjectBuildingRequest;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectDependenciesResolver;
import org.apache.maven.shared.dependency.graph.DependencyGraphBuilder;
import org.springframework.util.AntPathMatcher;
import org.xml.sax.SAXException;

/**
 * .
 *
 * @author zhangmsh
 * @version 1.0.0
 * @date 2020/5/15
 */
public abstract class AbstractBeeMojo extends AbstractMojo {

    /**
     * models.
     *
     * <p>在多模块项目依赖时，需要依赖于其他模块中的类。</p>
     *
     * <p>通过配置{@code models}来处理多个模块的类依赖问题。</p>
     */
    @Parameter(name = "models")
    private List<String> models = new ArrayList();
    /**
     * 扫描Class文件目录
     */
    @Parameter(name = "target", defaultValue = "/target/classes")
    private String target;
    /**
     * 配置找不到类时是否输出异常
     */
    @Parameter(name = "showNotFound", defaultValue = "false")
    private boolean showNotFound;
    /**
     * 配置扫描类的包目录
     */
    @Parameter(name = "basePackage")
    private String basePackage;

    /**
     * 配置生成的测试代码包目录
     */
    @Parameter(name = "testDir", defaultValue = "/src/test/java")
    private String testDir;
    /**
     * 仅生成能被Http请求的接口
     */
    @Parameter(name = "httpRequestOnly", defaultValue = "true")
    private boolean httpRequestOnly;
    @Parameter(name = "location", defaultValue = "http://localhost:8080")
    private String location;

    private Scanner fileScanner = new ClassFileScanner();

    private ClassPool classPool = MojoEnv.CLASS_POOL;

    private AntPathMatcher antPathMatcher = new AntPathMatcher();
    private Set<String> appendedClassPaths = new HashSet<>();

    @Parameter(defaultValue = "${session}", readonly = true)
    private MavenSession mavenSession;
    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject mavenProject;

    @Component
    private DependencyGraphBuilder dependencyGraphBuilder;

    @Component
    private ProjectDependenciesResolver projectDependenciesResolver;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        models.add("");
        this.initEnv();
        this.initJarDepend();
        this.initXmlDepend();
        this.loadCLasses();
    }

    protected void initEnv() {
        MojoEnv.setLogger(new Logger(getLog()));
        MojoEnv.setTestDir(testDir);
        MojoEnv.setHttpRequestOnly(httpRequestOnly);
        MojoEnv.setLocation(location);
        MojoEnv.setMavenProject(mavenProject);
        MojoEnv.setPluginDescriptor(
            (PluginDescriptor) this.getPluginContext().get("pluginDescriptor"));
    }

    private void initXmlDepend() {
        //todo 从XML中获取依赖
//        MavenProject project = MojoEnv.getMavenProject();
//        File xml = project.getFile();
//        try {
//            BeeXml beeXml = BeeXml.fromFile(xml);
//
//        } catch (IOException | SAXException e) {
//            MojoEnv.getLogger().errorAndExit(e);
//        }

    }

    private void initJarDepend() {
        MavenProject project = MojoEnv.getMavenProject();
        try {

            List modules = project.getModules();
            if (!CollectionUtils.isEmpty(modules)) {
                modules.forEach(model ->
                    MojoEnv.getLogger().info("load module:" + model));
            }
            Model originalModel = project.getOriginalModel();
            if (originalModel != null) {
                List<String> modules1 = originalModel.getModules();
                modules1.forEach(m ->
                    MojoEnv.getLogger().info("load module1:" + m));
            }

//
//            Map<String, DefaultArtifact> managedVersionMap = mavenProject
//                .getManagedVersionMap();
//            getLog().debug("managedVersionMap Size : " + managedVersionMap.size());
//            managedVersionMap.values().forEach(this::appendClassPathTo);
//
//            List<Dependency> selfDependencies = mavenProject.getDependencies();
//            selfDependencies.forEach(dependency -> {
//                appendClassPathTo(basedir, dependency.getScope(),
//                    dependency.getGroupId(), dependency.getArtifactId(), dependency.getVersion(),
//                    dependency.getType());
//            });
//
            List<Dependency> dependencies = project.getDependencyManagement()
                .getDependencies();
            dependencies.addAll(project.getDependencies());
            if (!CollectionUtils.isEmpty(dependencies)) {
                Field field = MavenProject.class.getDeclaredField("projectBuilderConfiguration");
                field.setAccessible(true);
                DefaultProjectBuildingRequest request
                    = (DefaultProjectBuildingRequest) field.get(project);
                ArtifactRepository localRepository = request.getLocalRepository();
                String basedir = localRepository.getBasedir();
                dependencies.forEach(dependency
                    -> this.appendClassPathTo(dependency, basedir));
            }
            Field resolvedArtifactsField = MavenProject.class
                .getDeclaredField("resolvedArtifacts");
            resolvedArtifactsField.setAccessible(true);
            Set<DefaultArtifact> resolvedArtifacts
                = (Set<DefaultArtifact>) resolvedArtifactsField.get(project);

            resolvedArtifacts.forEach(this::appendClassPathTo);
            getLog().debug("resolvedArtifacts Size : " + resolvedArtifacts.size());

            for (String model : models) {
                this.appendClassPath(model);
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            getLog().debug(e);
        }


    }

    private void appendClassPath(String folder) {
        Path path = Paths.get(MojoEnv.USER_DIR, folder, target);
        try {
            classPool.appendClassPath(path.toString());
        } catch (NotFoundException e) {
            if (showNotFound) {
                getLog().error(e);
            }
        }
    }

    private void appendClassPathTo(Dependency dependency, String basedir) {
        String jarKey = ArtifactUtils.versionlessKey(
            dependency.getGroupId(), dependency.getArtifactId());
        if (appendedClassPaths.contains(jarKey)) {
            return;
        }
        String scope = dependency.getScope();
        String groupId = dependency.getGroupId();
        String artifactId = dependency.getArtifactId();
        String version = dependency.getVersion();
        String type = dependency.getType();
        if (!"compile".equals(scope)
            && !StringUtil.isEmpty(scope)) {
            getLog().debug("notCompileScope:"
                + groupId + ":" + artifactId + ":" + version + ":" + scope);
            return;
        }
        appendedClassPaths.add(jarKey);
        //找到File
        String folder = groupId.replace(".", File.separator);
        Path jarFile = Paths.get(basedir, folder,
            artifactId, version, artifactId + "-" + version + "." + type);
        if (ArtifactUtils.isSnapshot(dependency.getVersion())) {
            Param<Path> param = this.getSnapshotJar(jarKey, jarFile);
            jarKey = param.getKey();
            jarFile = param.getValue();
        }
        if (Files.notExists(jarFile)) {
            getLog().debug("dependencySkip: "
                + groupId + ":" + artifactId + ":" + version + " not exists.");
            return;
        }
        this.appendClassPath(jarKey, jarFile);
    }

    private Param getSnapshotJar(String jarKey, Path jarFile) {
        Path repositories = jarFile.getParent().resolve("_remote.repositories");
        try {
            List<String> lines = Files.readAllLines(repositories);
            for (String line : lines) {
                if (line == null || line.charAt(0) == '#') {
                    continue;
                }
                String pomOrJar = line.substring(0, line.indexOf('>'));
                if (pomOrJar.endsWith("jar")) {
                    jarFile = jarFile.getParent().resolve(pomOrJar);
                }
                jarKey = new StringBuilder(jarKey)
                    .append('>').append(pomOrJar).toString();
            }
        } catch (IOException e) {
            //ignore
        }
        return new Param(jarKey, jarFile);
    }

    protected void appendClassPathTo(DefaultArtifact artifact) {
        String key = ArtifactUtils.versionlessKey(artifact);
        if (appendedClassPaths.contains(key)
            || artifact.getFile() == null) {
            return;
        }
        appendedClassPaths.add(key);
        String jarKey = ArtifactUtils.key(artifact);
        Path jarFile = artifact.getFile().toPath();
        if (artifact.isSnapshot()) {
            Param<Path> param = this.getSnapshotJar(jarKey, jarFile);
            jarKey = param.getKey();
            jarFile = param.getValue();
        }
        this.appendClassPath(jarKey, jarFile);
    }

    private void appendClassPath(String jarKey, Path jarFile) {
        try {
            String jar = jarFile.toString();
            classPool.appendClassPath(jar);
            getLog().debug("appendClassPath:" + jarKey);
        } catch (NotFoundException e) {
            if (showNotFound) {
                getLog().error(e);
            } else {
                getLog().debug("appendClassPathNotFound:" + jarKey);
            }
        }
    }

    private List<CtClass> loadCLasses() {
        List<CtClass> classes = new ArrayList<>();
        try {
            classes.addAll(this.loadClass(Paths.get(MojoEnv.USER_DIR, target)));
            for (CtClass aClass : classes) {
                this.parseClass(aClass);
            }
            this.afterParsed();
        } catch (IOException | NotFoundException e) {
            MojoEnv.getLogger().errorAndExit(e);
        }
        return classes;
    }

    protected abstract void afterParsed();

    protected abstract void parseClass(CtClass aClass) throws NotFoundException;

    private List<CtClass> loadClass(Path userDir) throws IOException {
        getLog().info("loadClassDir:" + userDir);
        List<Path> filePaths = fileScanner.scan(userDir);
        List<CtClass> classes = new ArrayList<>(filePaths.size());
        for (Path file : filePaths) {
            if (!StringUtil.isEmpty(basePackage)
                && !antPathMatcher.match(basePackage, file.toString())) {
                continue;
            }
            CtClass ctClass = classPool.makeClass(new FileInputStream(file.toFile()));
            classes.add(ctClass);
        }
        return classes;
    }
}
