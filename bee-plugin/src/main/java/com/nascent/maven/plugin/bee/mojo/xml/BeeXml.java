package com.nascent.maven.plugin.bee.mojo.xml;

import com.nascent.maven.plugin.bee.mojo.support.EmptyLog;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.digester3.Digester;
import org.apache.commons.digester3.RegexMatcher;
import org.apache.commons.digester3.RegexRules;
import org.apache.commons.digester3.SetNextRule;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.xml.sax.SAXException;

/**
 * .
 *
 * @author zhangmsh
 * @version 1.0.0
 * @date 2020/5/20
 */
@Setter
@Getter
@ToString
public class BeeXml {

    public static final String LIST_ADD = "add";
    public static final String SET_EXCLUSIONS = "setExclusions";
    public static final String ADD_DEPENDENCY = "addDependency";
    public static final String ADD_DEPENDENCY_MANAGEMENT = "addDependencyManagement";
    public static final String SET_PARENT = "setParent";
    public static final String SET_PROPERTIES = "setProperties";
    /**
     * 父依赖
     */
    private BeeParentDependency parent;

    private String groupId;
    private String artifactId;
    private String version;
    private String packaging;

    private List<BeeDependency> dependencyManagement = new ArrayList<>();
    private List<BeeDependency> dependencies = new ArrayList<>();
    private List<String> modules;
    private Map<String, String> properties = new HashMap<>();

    public void addDependency(BeeDependency beeDependency) {
        this.dependencies.add(beeDependency);
    }

    public void addDependencyManagement(BeeDependency beeDependency) {
        this.dependencyManagement.add(beeDependency);
    }

    private static final PathMatcher PATH_MATCHER = new AntPathMatcher();


    public static BeeXml from(InputStream inputStream) throws IOException, SAXException {
        Digester digester = new Digester();
        digester.setValidating(false);
        digester.setLogger(EmptyLog.empty(digester.getLogger()));
        digester.setSAXLogger(EmptyLog.empty(digester.getSAXLogger()));
        digester.setRules(new RegexRules(new RegexMatcher() {

            @Override
            public boolean match(String pathPattern, String rulePattern) {
                return Objects.equals(pathPattern, rulePattern)
                    || PATH_MATCHER.match(rulePattern, pathPattern);
            }
        }));
        digester.addObjectCreate("project", BeeXml.class);
        digester.addBeanPropertySetter("project/artifactId");
        digester.addBeanPropertySetter("project/groupId");
        digester.addBeanPropertySetter("project/version");
        digester.addBeanPropertySetter("project/packaging");

        digester.addObjectCreate("project/properties", HashMap.class);
        digester.addSetNext("project/properties", SET_PROPERTIES);
        SetNextRule setNextRule = new SetNextRule("testOne", Object.class.getName()) {
            private String body;

            @Override
            public void body(String namespace, String name, String text) throws Exception {
                super.body(namespace, name, text);
                this.body = text;
            }

            @Override
            public void end(String namespace, String name) throws Exception {
                HashMap child = (HashMap) super.getChild();
                child.put(name, body);
            }
        };
        digester.addRule("project/properties/*", setNextRule);

        digester.addObjectCreate("project/parent", BeeParentDependency.class);
        digester.addSetNext("project/parent", SET_PARENT);
        digester.addBeanPropertySetter("project/parent/artifactId");
        digester.addBeanPropertySetter("project/parent/groupId");
        digester.addBeanPropertySetter("project/parent/groupId");
        digester.addBeanPropertySetter("project/parent/relativePath");

        digester.addObjectCreate("project/modules", ArrayList.class);
        digester.addSetNext("project/modules", "setModules");
        digester.addCallMethod("project/modules/module", LIST_ADD, 1);
        digester.addCallParam("project/modules/module", 0);

        digester.addObjectCreate(
            "project/dependencyManagement/dependencies/dependency",
            BeeDependency.class);
        digester.addSetNext("project/dependencyManagement/dependencies/dependency",
            ADD_DEPENDENCY_MANAGEMENT);
        digester.addBeanPropertySetter(
            "project/dependencyManagement/dependencies/dependency/artifactId");
        digester.addBeanPropertySetter(
            "project/dependencyManagement/dependencies/dependency/groupId");
        digester.addBeanPropertySetter(
            "project/dependencyManagement/dependencies/dependency/version");
        digester.addBeanPropertySetter(
            "project/dependencyManagement/dependencies/dependency/type");
        digester.addBeanPropertySetter(
            "project/dependencyManagement/dependencies/dependency/scope");

        digester.addObjectCreate("project/dependencies/dependency", BeeDependency.class);
        digester.addSetNext("project/dependencies/dependency", ADD_DEPENDENCY);
        digester.addBeanPropertySetter("project/dependencies/dependency/artifactId");
        digester.addBeanPropertySetter("project/dependencies/dependency/groupId");
        digester.addBeanPropertySetter("project/dependencies/dependency/version");
        digester.addBeanPropertySetter("project/dependencies/dependency/type");
        digester.addBeanPropertySetter("project/dependencies/dependency/scope");
        digester.addBeanPropertySetter("project/dependencies/dependency/optional");

        digester.addObjectCreate(
            "project/dependencies/dependency/exclusions", ArrayList.class);
        digester.addSetNext(
            "project/dependencies/dependency/exclusions", SET_EXCLUSIONS);
        digester.addObjectCreate(
            "project/dependencies/dependency/exclusions/exclusion",
            BeeExclusion.class);
        digester.addSetNext(
            "project/dependencies/dependency/exclusions/exclusion", LIST_ADD);
        digester.addBeanPropertySetter(
            "project/dependencies/dependency/exclusions/exclusion/artifactId");
        digester.addBeanPropertySetter(
            "project/dependencies/dependency/exclusions/exclusion/groupId");
        return digester.parse(inputStream);
    }
}
