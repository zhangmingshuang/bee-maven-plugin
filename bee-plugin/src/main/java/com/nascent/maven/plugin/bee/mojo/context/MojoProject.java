package com.nascent.maven.plugin.bee.mojo.context;

import java.io.File;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.List;
import java.util.Properties;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.DependencyManagement;
import org.apache.maven.project.DefaultProjectBuildingRequest;
import org.apache.maven.project.MavenProject;

/**
 * .
 *
 * @author zhangmsh
 * @version 1.0.0
 * @date 2020/6/16
 */
public class MojoProject implements Context {

    private MavenProject mavenProject;
    private ArtifactRepository artifactRepository;

    protected static MojoProject newInstance(MavenProject mavenProject) {
        return new MojoProject(mavenProject);
    }

    private MojoProject(MavenProject mavenProject) {
        this.mavenProject = mavenProject;
    }

    /**
     * 依赖管理
     *
     * @return
     */
    public DependencyManagement getDependencyManagement() {
        return mavenProject.getDependencyManagement();
    }

    public List<Dependency> getDependencies() {
        return mavenProject.getDependencies();
    }

    /**
     * 取得本地仓库的基础目录，如：c:\\.m2
     *
     * @return 本地仓库的基础目录
     */
    public String getLocalRepositoryBaseDir() {
        return this.getLocalRepository().getBasedir();
    }

    /**
     * 取得本地仓库
     *
     * @return 本地仓库
     */
    public ArtifactRepository getLocalRepository() {
        if (artifactRepository != null) {
            return artifactRepository;
        }
        try {
            Field field = MavenProject.class.getDeclaredField("projectBuilderConfiguration");
            field.setAccessible(true);
            DefaultProjectBuildingRequest request
                = (DefaultProjectBuildingRequest) field.get(mavenProject);
            artifactRepository = request.getLocalRepository();
        } catch (NoSuchFieldException | IllegalAccessException e) {
            //ignore
        }
        return artifactRepository;
    }

    public Properties getProperties() {
        return mavenProject.getProperties();
    }

    /**
     * 取得使用插件的本身项目的pom.xml
     *
     * @return pom file.
     */
    public File getPomFile() {
        return mavenProject.getFile();
    }

    /**
     * 取得使用插件的本身项目的工作目录
     *
     * @return
     */
    public Path getBaseDir() {
        return mavenProject.getBasedir().toPath();
    }
}
