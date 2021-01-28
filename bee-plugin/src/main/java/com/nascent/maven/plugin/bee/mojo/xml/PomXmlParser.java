package com.nascent.maven.plugin.bee.mojo.xml;

import com.nascent.maven.plugin.bee.mojo.context.MojoContexts;
import com.nascent.maven.plugin.bee.mojo.support.EmptyLog;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import org.apache.commons.digester3.Digester;
import org.apache.commons.digester3.RegexMatcher;
import org.apache.commons.digester3.RegexRules;
import org.apache.commons.digester3.SetNextRule;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.xml.sax.SAXException;

/**
 * the {@code pom.xml} file parser.
 *
 * @author zhangmsh
 * @version 1.0.0
 * @date 2020/5/20
 */
public class PomXmlParser implements XmlParser {

  private static final PathMatcher PATH_MATCHER = new AntPathMatcher();

  @Override
  public Pom parse(FileInputStream inputStream) {
    try {
      return this.parseXml(inputStream);
    } catch (IOException | SAXException e) {
      MojoContexts.getLogger().errorAndExit(e);
    }
    // will never trigger here.
    return new Pom();
  }

  protected Pom parseXml(InputStream inputStream) throws IOException, SAXException {

    Digester digester = new Digester();

    this.preDigester(digester);

    this.onProperties(digester);

    this.onProjectParent(digester);

    this.onProjectModules(digester);

    this.onProjectDependencyManagement(digester);

    this.onProjectDependencies(digester);

    return digester.parse(inputStream);
  }

  protected void onProjectDependencies(Digester digester) {
    digester.addObjectCreate("project/dependencies/dependency", PomDependency.class);
    digester.addSetNext("project/dependencies/dependency", "addDependency");
    digester.addBeanPropertySetter("project/dependencies/dependency/artifactId");
    digester.addBeanPropertySetter("project/dependencies/dependency/groupId");
    digester.addBeanPropertySetter("project/dependencies/dependency/version");
    digester.addBeanPropertySetter("project/dependencies/dependency/type");
    digester.addBeanPropertySetter("project/dependencies/dependency/scope");
    digester.addBeanPropertySetter("project/dependencies/dependency/optional");

    digester.addObjectCreate("project/dependencies/dependency/exclusions", ArrayList.class);
    digester.addSetNext("project/dependencies/dependency/exclusions", "setExclusions");
    digester.addObjectCreate(
        "project/dependencies/dependency/exclusions/exclusion", PomDependencyExclusion.class);
    digester.addSetNext("project/dependencies/dependency/exclusions/exclusion", "add");
    digester.addBeanPropertySetter(
        "project/dependencies/dependency/exclusions/exclusion/artifactId");
    digester.addBeanPropertySetter("project/dependencies/dependency/exclusions/exclusion/groupId");
  }

  protected void onProjectDependencyManagement(Digester digester) {
    digester.addObjectCreate(
        "project/dependencyManagement/dependencies/dependency", PomDependency.class);
    digester.addSetNext(
        "project/dependencyManagement/dependencies/dependency", "addDependencyManagement");
    digester.addBeanPropertySetter(
        "project/dependencyManagement/dependencies/dependency/artifactId");
    digester.addBeanPropertySetter("project/dependencyManagement/dependencies/dependency/groupId");
    digester.addBeanPropertySetter("project/dependencyManagement/dependencies/dependency/version");
    digester.addBeanPropertySetter("project/dependencyManagement/dependencies/dependency/type");
    digester.addBeanPropertySetter("project/dependencyManagement/dependencies/dependency/scope");
  }

  protected void onProjectModules(Digester digester) {
    digester.addObjectCreate("project/modules", ArrayList.class);
    digester.addSetNext("project/modules", "setModules");
    digester.addCallMethod("project/modules/module", "add", 1);
    digester.addCallParam("project/modules/module", 0);
  }

  protected void onProjectParent(Digester digester) {
    digester.addObjectCreate("project/parent", PomParentDependency.class);
    // setParent = set to this's parent field.
    digester.addSetNext("project/parent", "setParent");
    digester.addBeanPropertySetter("project/parent/artifactId");
    digester.addBeanPropertySetter("project/parent/groupId");
    digester.addBeanPropertySetter("project/parent/groupId");
    digester.addBeanPropertySetter("project/parent/relativePath");
  }

  protected void onProperties(Digester digester) {
    digester.addObjectCreate("project/properties", HashMap.class);
    // setProperties = set to this's properties field.
    digester.addSetNext("project/properties", "setProperties");
    // put the project/properties/ path's key and value to properties map.
    digester.addRule("project/properties/*", this.putToHashMapRule());
  }

  protected void preDigester(Digester digester) {
    digester.setValidating(false);
    digester.setLogger(EmptyLog.empty(digester.getLogger()));
    digester.setSAXLogger(EmptyLog.empty(digester.getSAXLogger()));
    digester.setRules(this.getRegexRules());
    digester.addObjectCreate("project", Pom.class);
    digester.addBeanPropertySetter("project/artifactId");
    digester.addBeanPropertySetter("project/groupId");
    digester.addBeanPropertySetter("project/version");
    digester.addBeanPropertySetter("project/packaging");
  }

  protected SetNextRule putToHashMapRule() {
    return new SetNextRule("testOne", Object.class.getName()) {
      private String body;

      @Override
      public void body(String namespace, String name, String text) throws Exception {
        super.body(namespace, name, text);
        this.body = text;
      }

      @Override
      public void end(String namespace, String name) {
        HashMap child = (HashMap) super.getChild();
        child.put(name, this.body);
      }
    };
  }

  protected RegexRules getRegexRules() {
    return new RegexRules(
        new RegexMatcher() {

          @Override
          public boolean match(String pathPattern, String rulePattern) {
            return Objects.equals(pathPattern, rulePattern)
                || PATH_MATCHER.match(rulePattern, pathPattern);
          }
        });
  }
}
