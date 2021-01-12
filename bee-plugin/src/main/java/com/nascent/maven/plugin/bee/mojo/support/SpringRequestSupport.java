package com.nascent.maven.plugin.bee.mojo.support;

import com.nascent.maven.plugin.bee.mojo.context.MojoContexts;
import com.nascent.maven.plugin.bee.utils.ArrayUtils;
import java.lang.annotation.Annotation;
import javassist.CtClass;
import javassist.CtMethod;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * .
 *
 * @author zhangmsh
 * @version 1.0.0
 * @date 2020/5/15
 */
public class SpringRequestSupport implements RequestSupport {

  @Override
  public String getMapping(CtClass aClass, CtMethod method) {
    if (!hasMapping(aClass) || !hasMapping(method)) {
      return null;
    }
    try {
      String[] mappingPrefix = getClassMapping(aClass);
      if (mappingPrefix == null) {
        // 没有注解
        return null;
      }

      String[] mappingSuffix = getMethodMapping(method);
      if (mappingSuffix == null) {
        return null;
      }

      if (ArrayUtils.isEmpty(mappingPrefix)) {
        mappingPrefix = new String[] {""};
      }

      if (ArrayUtils.isEmpty(mappingSuffix)) {
        mappingSuffix = new String[] {""};
      }

      return mappingPrefix[0] + mappingSuffix[0];
    } catch (ClassNotFoundException e) {
      MojoContexts.getLogger().error(e);
    }
    return null;
  }

  @Override
  public boolean hasRequestBody(Object[] annotations) {
    if (ArrayUtils.isEmpty(annotations)) {
      return false;
    }
    for (Object annotation : annotations) {
      String name = ((Annotation) annotation).annotationType().getName();
      if (SpringPackage.REQUEST_BODY.equals(name)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean hasRequestBody(Object[][] annotations) {
    if (ArrayUtils.isEmpty(annotations)) {
      return false;
    }
    for (Object[] annotation : annotations) {
      if (this.hasRequestBody(annotation)) {
        return true;
      }
    }
    return false;
  }

  private boolean hasMapping(CtMethod method) {
    if (method == null) {
      return false;
    }
    return method.hasAnnotation(SpringPackage.POST_MAPPING)
        || method.hasAnnotation(SpringPackage.GET_MAPPING)
        || method.hasAnnotation(SpringPackage.REQUEST_MAPPING);
  }

  @Override
  public boolean hasMapping(CtClass clazz) {
    if (clazz == null) {
      return false;
    }
    return clazz.hasAnnotation(SpringPackage.POST_MAPPING)
        || clazz.hasAnnotation(SpringPackage.GET_MAPPING)
        || clazz.hasAnnotation(SpringPackage.REQUEST_MAPPING);
  }

  private String[] getMethodMapping(CtMethod aMethod) throws ClassNotFoundException {
    if (aMethod.hasAnnotation(SpringPackage.POST_MAPPING)) {
      PostMapping postMapping = (PostMapping) aMethod.getAnnotation(PostMapping.class);
      return ArrayUtils.emptyOrElse(postMapping.value(), postMapping.path());
    }
    if (aMethod.hasAnnotation(SpringPackage.GET_MAPPING)) {
      GetMapping getMapping = (GetMapping) aMethod.getAnnotation(GetMapping.class);
      return ArrayUtils.emptyOrElse(getMapping.value(), getMapping.path());
    }
    if (aMethod.hasAnnotation(SpringPackage.REQUEST_MAPPING)) {
      RequestMapping getMapping = (RequestMapping) aMethod.getAnnotation(RequestMapping.class);
      return ArrayUtils.emptyOrElse(getMapping.value(), getMapping.path());
    }
    return null;
  }

  private String[] getClassMapping(CtClass aClass) throws ClassNotFoundException {
    if (aClass.hasAnnotation(SpringPackage.POST_MAPPING)) {
      PostMapping postMapping = (PostMapping) aClass.getAnnotation(PostMapping.class);
      return ArrayUtils.emptyOrElse(postMapping.value(), postMapping.path());
    }
    if (aClass.hasAnnotation(SpringPackage.GET_MAPPING)) {
      GetMapping getMapping = (GetMapping) aClass.getAnnotation(GetMapping.class);
      return ArrayUtils.emptyOrElse(getMapping.value(), getMapping.path());
    }
    if (aClass.hasAnnotation(SpringPackage.REQUEST_MAPPING)) {
      RequestMapping getMapping = (RequestMapping) aClass.getAnnotation(RequestMapping.class);
      return ArrayUtils.emptyOrElse(getMapping.value(), getMapping.path());
    }
    return new String[0];
  }

  //    private String[] getRequestPaths(Object[] annotations) {
  //        for (Object annotation : annotations) {
  //            String name = ((Annotation) annotation).annotationType().getName();
  //            switch (name) {
  //                case SpringPackage.POST_MAPPING:
  //                case SpringPackage.GET_MAPPING:
  //                case SpringPackage.REQUEST_MAPPING:
  //                    try {
  //                        Field h = Proxy.class.getDeclaredField("h");
  //                        h.setAccessible(true);
  //                        AnnotationImpl impl = (AnnotationImpl) h.get(annotation);
  //                        javassist.bytecode.annotation.Annotation ssistAnnotation
  //                            = impl.getAnnotation();
  //                        ArrayMemberValue value = (ArrayMemberValue) ssistAnnotation
  //                            .getMemberValue("value");
  //                        if (value == null) {
  //                            value = (ArrayMemberValue) ssistAnnotation.getMemberValue("path");
  //                        }
  //                        if (value == null) {
  //                            return new String[]{""};
  //                        }
  //                        MemberValue[] values = value.getValue();
  //                        if (values != null && values.length > 0) {
  //                            return Arrays.stream(values)
  //                                .map(v -> ((StringMemberValue) v).getValue())
  //                                .toArray(String[]::new);
  //                        }
  //                    } catch (NoSuchFieldException | IllegalAccessException e) {
  //                        BeeEnv.getLogger().error(e);
  //                    }
  //                    return new String[0];
  //                default:
  //                    break;
  //            }
  //
  //        }
  //        return new String[0];
  //    }
}
