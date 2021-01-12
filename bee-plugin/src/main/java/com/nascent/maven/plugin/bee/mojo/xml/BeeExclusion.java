package com.nascent.maven.plugin.bee.mojo.xml;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

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
public class BeeExclusion {

  private String groupId;
  private String artifactId;
}
