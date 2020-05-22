package com.nascent.maven.plugin.bee.mojo.xml;

import java.io.File;
import java.io.IOException;
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

    /**
     * 父依赖
     */
    private BeeDependency parent;

    private String groupId;
    private String artifactId;
    private String version;
    private String packaging;

    private List<BeeDependency> dependencies = new ArrayList<>();
    private List<String> modules;
    private Map<String, String> properties;

    public void addDependency(BeeDependency beeDependency) {
        this.dependencies.add(beeDependency);
    }

    private static final PathMatcher PATH_MATCHER = new AntPathMatcher();

    public static BeeXml fromFile(File file) throws IOException, SAXException {
        Digester digester = new Digester();
        digester.setValidating(false);
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
        digester.addSetNext("project/properties", "setProperties");
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

        digester.addObjectCreate("project/parent", BeeDependency.class);
        digester.addSetNext("project/parent", "setParent");
        digester.addBeanPropertySetter("project/parent/artifactId");
        digester.addBeanPropertySetter("project/parent/groupId");
        digester.addBeanPropertySetter("project/parent/version");

        digester.addObjectCreate("project/modules", ArrayList.class);
        digester.addSetNext("project/modules", "setModules");
        digester.addCallMethod("project/modules/module", "add", 1);
        digester.addCallParam("project/modules/module", 0);

        digester.addObjectCreate("project/dependencies/dependency", BeeDependency.class);
        digester.addSetNext("project/dependencies/dependency", "addDependency");
        digester.addBeanPropertySetter("project/dependencies/dependency/artifactId");
        digester.addBeanPropertySetter("project/dependencies/dependency/groupId");
        digester.addBeanPropertySetter("project/dependencies/dependency/version");

        digester.addObjectCreate("project/dependencies/dependency/exclusions", ArrayList.class);
        digester.addSetNext("project/dependencies/dependency/exclusions", "setExclusions");
        digester.addObjectCreate("project/dependencies/dependency/exclusions/exclusion",
            BeeExclusion.class);
        digester.addSetNext("project/dependencies/dependency/exclusions/exclusion", "add");
        digester.addBeanPropertySetter(
            "project/dependencies/dependency/exclusions/exclusion/artifactId");
        digester.addBeanPropertySetter(
            "project/dependencies/dependency/exclusions/exclusion/groupId");
        return digester.parse(file);
    }
}
