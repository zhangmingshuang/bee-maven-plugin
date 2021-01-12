package com.nascent.maven.plugin.bee.mojo.support;

/**
 * .
 *
 * @author zhangmsh
 * @version 1.0.0
 * @date 2020/5/11
 */
public class SpringPackage {

  public static final String CONTROLLER = "org.springframework.stereotype.Controller";
  public static final String REST_CONTROLLER =
      "org.springframework.web.bind.annotation.RestController";
  public static final String MAPPING = "org.springframework.web.bind.annotation.Mapping";
  public static final String POST_MAPPING = "org.springframework.web.bind.annotation.PostMapping";
  public static final String GET_MAPPING = "org.springframework.web.bind.annotation.GetMapping";
  public static final String REQUEST_MAPPING =
      "org.springframework.web.bind.annotation.RequestMapping";
  public static final String REQUEST_METHOD_POST =
      "org.springframework.web.bind.annotation.RequestMethod.POST";
  public static final String REQUEST_METHOD_GET =
      "org.springframework.web.bind.annotation.RequestMethod.GET";
  public static final String PATH_VARIABLE = "org.springframework.web.bind.annotation.PathVariable";
  public static final String REQUEST_BODY = "org.springframework.web.bind.annotation.RequestBody";

  private SpringPackage() {}
}
