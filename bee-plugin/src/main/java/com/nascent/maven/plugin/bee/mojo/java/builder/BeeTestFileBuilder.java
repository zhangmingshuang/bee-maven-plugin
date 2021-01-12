package com.nascent.maven.plugin.bee.mojo.java.builder;

import bee.com.nascent.maven.plugin.BeeApplication;
import bee.com.nascent.maven.plugin.process.AbstractBeeMethodArgs;
import bee.com.nascent.maven.plugin.process.AbstractBeeMethodDelegate;
import bee.com.nascent.maven.plugin.process.DataAddition;
import bee.com.nascent.maven.plugin.process.__BeeEnvironment;
import com.nascent.maven.plugin.bee.constant.Config;
import com.nascent.maven.plugin.bee.mojo.context.MojoContexts;
import com.nascent.maven.plugin.bee.mojo.support.RequestSupport;
import com.nascent.maven.plugin.bee.mojo.support.SpringRequestSupport;
import com.nascent.maven.plugin.bee.utils.ArrayUtils;
import com.nascent.maven.plugin.bee.utils.StringUtils;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeSpec.Builder;
import com.squareup.javapoet.TypeVariableName;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javax.lang.model.element.Modifier;

/**
 * .
 *
 * @author zhangmsh
 * @version 1.0.0
 * @date 2020/5/15
 */
public class BeeTestFileBuilder {

  private static final BeeTestFileBuilder EMPTY =
      new BeeTestFileBuilder(null, null) {
        @Override
        public void writeTo(Path folder) throws IOException {
          // Ignore
        }

        @Override
        public BeeTestFileBuilder javaFile(String packageName) {
          return this;
        }
      };

  private CtClass clazz;
  private TypeSpec clazzType;
  private JavaFile javaFile;

  public BeeTestFileBuilder(CtClass clazz, TypeSpec clazzType) {
    this.clazz = clazz;
    this.clazzType = clazzType;
  }

  @SuppressWarnings("java:S112")
  public static BeeTestFileBuilder builder(CtClass aClass) throws BuildException {
    MojoContexts.getLogger().debug("build class:" + aClass.getName());

    CtMethod[] declaredMethods = aClass.getDeclaredMethods();

    TypeSpec.Builder classBuilder =
        TypeSpec.classBuilder("Bee" + aClass.getSimpleName()).addModifiers(Modifier.PUBLIC);

    RequestSupport requestSupport = new SpringRequestSupport();
    if (!requestSupport.hasMapping(aClass)) {
      return BeeTestFileBuilder.EMPTY;
    }
    boolean hasMethodGenerate = false;
    Set<String> methodClassDelegateGenerated = new HashSet<>();
    Set<String> methodArgsClassGenerated = new HashSet<>();
    Set<String> methodGenerated = new HashSet<>();
    for (int i = 0, s = declaredMethods.length; i < s; i++) {
      CtMethod method = declaredMethods[i];

      String uri = requestSupport.getMapping(aClass, method);
      if (StringUtils.isEmpty(uri)) {
        MojoContexts.getLogger().debug("empty uri. clazz:" + method.getLongName());
        continue;
      }
      hasMethodGenerate = true;
      CtClass returnType = null;
      try {
        returnType = method.getReturnType();
      } catch (NotFoundException e) {
        throw new BuildException(aClass.getName(), method.getName(), e);
      }
      String returnTypeName = getReturnTypeName(returnType);
      // 生成方法代理类
      String methodDelegateClassName = StringUtils.capitalize(method.getName()) + "Delegate";
      // 静态方法入参代理类
      String methodArgsClassName = StringUtils.capitalize(method.getName()) + "Args";
      if (!methodClassDelegateGenerated.contains(methodDelegateClassName)) {
        methodClassDelegateGenerated.add(methodDelegateClassName);
        // 静态方法代理内部类
        TypeSpec methodDelegateClass =
            buildMethodDelegateClass(returnTypeName, methodDelegateClassName, methodArgsClassName);
        classBuilder.addType(methodDelegateClass);
        // end MethodDelegate Class Build.
      }
      if (!methodGenerated.contains(method.getName())) {
        methodGenerated.add(method.getName());
        // 生成代理方法
        classBuilder.addMethod(buildMethodDelegateMethod(method, uri, methodDelegateClassName));
      }
      if (!methodArgsClassGenerated.contains(methodArgsClassName)) {
        methodArgsClassGenerated.add(methodArgsClassName);
        TypeSpec methodArgsClass = null;
        try {
          methodArgsClass = buildMethodArgsClass(method, methodArgsClassName);
        } catch (NotFoundException e) {
          throw new BuildException(aClass.getName(), method.getName(), e);
        }
        classBuilder.addType(methodArgsClass);
      }
    }

    if (!hasMethodGenerate) {
      return BeeTestFileBuilder.EMPTY;
    }

    classBuilder.addJavadoc(
        CodeBlock.builder()
            .add("bee auto build. at $L", LocalDateTime.now())
            .add(Config.LINE_SEPARATOR)
            .add("@see $L", aClass.getName())
            .build());
    return new BeeTestFileBuilder(aClass, classBuilder.build());
  }

  private static TypeSpec buildMethodArgsClass(CtMethod method, String methodArgsClassName)
      throws NotFoundException {
    CtClass[] parameterTypes = method.getParameterTypes();
    /**
     * 生成方法Args
     *
     * <pre>{@code
     *  public static class XMethodArgs<XXP> extends AbstractBeeMethodArgs<XXP> {
     *
     *     public XMethodArgs(DataAddition d) {
     *         super(d);
     *     }
     *
     *     //方法中的参数区
     * }
     * }</pre>
     */
    Builder methodArgsClassBuilder =
        TypeSpec.classBuilder(methodArgsClassName)
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .addTypeVariable(TypeVariableName.get("T"))
            .superclass(
                ParameterizedTypeName.get(
                    ClassName.get(AbstractBeeMethodArgs.class), ClassName.bestGuess("T")))
            .addMethod(
                MethodSpec.constructorBuilder()
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(DataAddition.class, "_bee_Data_Addition")
                    .addStatement("super($N)", "_bee_Data_Addition")
                    .build());
    if (!ArrayUtils.isEmpty(parameterTypes)) {
      ParameterizedTypeName parameterizedTypeName =
          ParameterizedTypeName.get(
              ClassName.bestGuess(methodArgsClassName), ClassName.bestGuess("T"));

      int pos = javassist.Modifier.isStatic(method.getModifiers()) ? 0 : 1;
      CodeAttribute codeAttribute = method.getMethodInfo().getCodeAttribute();
      LocalVariableAttribute localVariableAttribute =
          (LocalVariableAttribute) codeAttribute.getAttribute(LocalVariableAttribute.tag);

      for (int k = 0, ks = parameterTypes.length; k < ks; k++) {
        CtClass param = parameterTypes[k];
        String paramName = localVariableAttribute.variableName(k + pos);
        while (Config.THIS.equals(paramName)) {
          paramName = localVariableAttribute.variableName(k + ++pos);
        }
        String paramType = param.getName().replace("$", ".");
        /**
         * 生成参数
         *
         * <pre>{@code
         * public XMethodArgs<XXP> param(String param) {
         *        String key = "param";
         *        super._bee_add_Reqest_Params(key, param);
         *        return this;
         *    }
         * }</pre>
         */
        try {
          methodArgsClassBuilder.addMethod(
              MethodSpec.methodBuilder(paramName)
                  .addModifiers(Modifier.PUBLIC)
                  .returns(parameterizedTypeName)
                  .addParameter(TypeVariableName.get(paramType), paramName)
                  .addStatement("super._bee_add_Reqest_Params($S,$N)", paramName, paramName)
                  .addStatement("return this")
                  .build());
        } catch (IllegalArgumentException e) {
          StringBuilder builder = new StringBuilder();
          builder
              .append("create methodArgsClassBuilder error. ")
              .append(method.getLongName())
              .append(" index = " + k + " [" + paramType + " " + paramName + "]")
              .append(" variables: [");
          int len = localVariableAttribute.tableLength();
          for (int i = 0; i < len; i++) {
            builder
                .append(i)
                .append(" = ")
                .append(localVariableAttribute.variableName(i))
                .append(Config.LINE_SEPARATOR);
          }
          builder.append("]");
          MojoContexts.getLogger().error(builder.toString());
          throw new IllegalArgumentException(e);
        }
      }
    }
    return methodArgsClassBuilder.build();
    // end MethodArgs Build.
  }

  private static MethodSpec buildMethodDelegateMethod(
      CtMethod method, String uri, String methodDelegateClassName) {
    /**
     * 生成代理方法 {@code public static XMethodDelegate xMethod() { return new
     * XMethodDelegate("http://localhost"+"/xMethod"); } }
     */
    String methodArgs = null;
    try {
      CtClass[] parameterTypes = method.getParameterTypes();
      if (!ArrayUtils.isEmpty(parameterTypes)) {
        StringBuilder builder = new StringBuilder();
        for (CtClass parameterType : parameterTypes) {
          builder.append(parameterType.getName()).append(".class,");
        }
        methodArgs = builder.substring(0, builder.length() - 1);
      }
    } catch (NotFoundException e) {
      // Ignore
    }
    return MethodSpec.methodBuilder(method.getName())
        .addJavadoc("@see $N#$N", method.getDeclaringClass().getName(), method.getName())
        .addJavadoc("\narthas: trace $N $N", method.getDeclaringClass().getName(), method.getName())
        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
        .returns(ClassName.bestGuess(methodDelegateClassName))
        .addStatement(
            "String uri = $T.ofNullable($T.getLocation()).orElse($S) + $S",
            Optional.class,
            __BeeEnvironment.class,
            Config.LOCATION,
            uri)
        .beginControlFlow("if($T.configuration().location().hasBuilder())", BeeApplication.class)
        .addStatement("Class clazz = $N.class", method.getDeclaringClass().getName())
        .addStatement(
            "Class[] methodArgs = $N",
            methodArgs == null ? "null" : "new Class[]{" + methodArgs + "}")
        .addStatement("$T method = null", Method.class)
        .beginControlFlow("try")
        // start if
        .beginControlFlow("if(methodArgs != null && methodArgs.length > 0)")
        .addStatement("method = clazz.getDeclaredMethod($S, methodArgs) ", method.getName())
        .nextControlFlow("else")
        .addStatement("method = clazz.getDeclaredMethod($S)", method.getName())
        // end if
        .endControlFlow()
        .addStatement(
            "uri = $T.configuration().location()" + ".buildLocation(uri, clazz, method)",
            BeeApplication.class)
        // try cache
        .nextControlFlow("catch($T e)", NoSuchMethodException.class)
        .addStatement("throw new $T(e)", RuntimeException.class)
        // end try
        .endControlFlow()
        .endControlFlow()
        .addStatement("return new $N(uri)", methodDelegateClassName)
        .build();
  }

  private static TypeSpec buildMethodDelegateClass(
      String returnTypeName, String methodDelegateClassName, String methodArgsClassName) {
    /**
     * 静态方法代理内部类
     *  <pre>
     * {@code
     *    public static class XMethodDelegate
     *      extends com.nascent.maven.plugin.bee.core.AbstractBeeMethodDelegate {
     * }
     * </pre>
     */
    Builder methodDelegateClass =
        TypeSpec.classBuilder(methodDelegateClassName)
            .superclass(AbstractBeeMethodDelegate.class)
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            /**
             * 构造方法
             *
             * <pre>{@code
             * public XMethodDelegate(java.lang.String _bee_Mapping_url) {
             *   super(_bee_Mapping_url);
             * }
             *
             * }</pre>
             */
            .addMethod(
                MethodSpec.constructorBuilder()
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(String.class, "_bee_Mapping_url")
                    .addStatement("super($N)", "_bee_Mapping_url")
                    .build())
            /**
             * args方法
             *
             * <pre>{@code
             * public XMethodArgs<com.nascent.maven.plugin.bee.BeeTestMojo$XXP> args() {
             *   return new XMethodArgs(this);
             * }
             * }</pre>
             */
            .addMethod(
                MethodSpec.methodBuilder("args")
                    .addModifiers(Modifier.PUBLIC)
                    .returns(
                        ParameterizedTypeName.get(
                            ClassName.bestGuess(methodArgsClassName),
                            ClassName.bestGuess(returnTypeName)))
                    .addStatement("return new $N<$N>(this)", methodArgsClassName, returnTypeName)
                    .build())
            /** 响应类方法* */
            .addMethod(
                MethodSpec.methodBuilder("_bee_Return_Type_Class")
                    .addModifiers(Modifier.PUBLIC)
                    .returns(Class.class)
                    .addStatement("return $N.class", returnTypeName)
                    .build());
    return methodDelegateClass.build();
  }

  private static String getReturnTypeName(CtClass returnType) {
    String returnTypeName = returnType.getName();
    if (Config.VOID.equals(returnTypeName)) {
      returnTypeName = Void.class.getName();
    } else {
      returnTypeName = returnTypeName.replace(Config.DOLLAR, Config.POINT);
    }
    return returnTypeName;
  }

  public BeeTestFileBuilder javaFile(String packageName) {
    String clazzPackageName = clazz.getPackageName();
    if (clazzPackageName.indexOf(Config.POINT) != -1) {
      clazzPackageName = clazzPackageName.substring(clazzPackageName.lastIndexOf(Config.POINT) + 1);
    }
    javaFile = JavaFile.builder(packageName + ".bean." + clazzPackageName, clazzType).build();
    return this;
  }

  public void writeTo(Path folder) throws IOException {
    javaFile.writeTo(folder);
  }

  public String javaCode() {
    return clazzType.toString();
  }
}
