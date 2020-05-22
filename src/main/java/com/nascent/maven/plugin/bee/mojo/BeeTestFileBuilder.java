package com.nascent.maven.plugin.bee.mojo;

import bee.com.nascent.maven.plugin.process.AbstractBeeMethodArgs;
import bee.com.nascent.maven.plugin.process.AbstractBeeMethodDelegate;
import bee.com.nascent.maven.plugin.process.BeeEnvironment;
import bee.com.nascent.maven.plugin.process.DataAddition;
import com.nascent.maven.plugin.bee.utils.ArrayUtils;
import com.nascent.maven.plugin.bee.utils.StringUtil;
import com.nascent.maven.plugin.bee.mojo.support.SpringSupport;
import com.nascent.maven.plugin.bee.mojo.support.Support;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeSpec.Builder;
import com.squareup.javapoet.TypeVariableName;
import java.io.IOException;
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

    private static final BeeTestFileBuilder EMPTY = new BeeTestFileBuilder(null) {
        @Override
        public void writeTo(Path folder) throws IOException {
            //Ignore
        }

        @Override
        public BeeTestFileBuilder javaFile(String packageName) {
            return this;
        }
    };
    private TypeSpec clazz;
    private JavaFile javaFile;

    public BeeTestFileBuilder(TypeSpec clazz) {
        this.clazz = clazz;
    }

    public BeeTestFileBuilder javaFile(String packageName) {
        javaFile = JavaFile.builder(packageName, clazz).build();
        return this;
    }

    public void writeTo(Path folder) throws IOException {
        javaFile.writeTo(folder);
    }

    public String javaCode() {
        return clazz.toString();
    }

    @SuppressWarnings("java:S112")
    public static BeeTestFileBuilder builder(CtClass aClass)
        throws Throwable {
        MojoEnv.getLogger().debug("build class:" + aClass.getName());

        CtMethod[] declaredMethods = aClass.getDeclaredMethods();

        TypeSpec.Builder classBuilder = TypeSpec.classBuilder("Bee" + aClass.getSimpleName())
            .addModifiers(Modifier.PUBLIC);

        Support support = new SpringSupport();
        if (!support.hasMapping(aClass)) {
            return BeeTestFileBuilder.EMPTY;
        }
        boolean hasMethodGenerate = false;
        Set<String> methodClassDelegateGenerated = new HashSet<>();
        Set<String> methodArgsClassGenerated = new HashSet<>();
        Set<String> methodGenerated = new HashSet<>();
        for (int i = 0, s = declaredMethods.length; i < s; i++) {
            CtMethod method = declaredMethods[i];
            MojoEnv.setThreadMethod(method);

            String uri = support.getMapping(aClass, method);
            if (StringUtil.isEmpty(uri)) {
                MojoEnv.getLogger().debug("empty uri. clazz:"
                    + method.getLongName());
                continue;
            }
            hasMethodGenerate = true;
            CtClass returnType = method.getReturnType();
            String returnTypeName = getReturnTypeName(returnType);
            //生成方法代理类
            String methodDelegateClassName = StringUtil.capitalize(method.getName()) + "Delegate";
            //静态方法入参代理类
            String methodArgsClassName = StringUtil.capitalize(method.getName()) + "Args";
            if (!methodClassDelegateGenerated.contains(methodDelegateClassName)) {
                methodClassDelegateGenerated.add(methodDelegateClassName);
                //静态方法代理内部类
                TypeSpec methodDelegateClass = buildMethodDelegateClass(returnTypeName,
                    methodDelegateClassName, methodArgsClassName);
                classBuilder.addType(methodDelegateClass);
                //end MethodDelegate Class Build.
            }
            if (!methodGenerated.contains(method.getName())) {
                methodGenerated.add(method.getName());
                //生成代理方法
                classBuilder.addMethod(
                    buildMethodDelegateMethod(method, uri, methodDelegateClassName));
            }
            if (!methodArgsClassGenerated.contains(methodArgsClassName)) {
                methodArgsClassGenerated.add(methodArgsClassName);
                TypeSpec methodArgsClass = buildMethodArgsClass(method, methodArgsClassName);
                classBuilder.addType(methodArgsClass);
            }
        }

        if (!hasMethodGenerate && MojoEnv.isHttpRequestOnly()) {
            return BeeTestFileBuilder.EMPTY;
        }

        classBuilder.addJavadoc(CodeBlock.builder()
            .add("bee auto build. at $L", LocalDateTime.now())
            .add(MojoEnv.SEPARATOR)
            .add("@see $L", aClass.getName())
            .build());
        return new BeeTestFileBuilder(classBuilder.build());
    }

    private static TypeSpec buildMethodArgsClass(CtMethod method, String methodArgsClassName)
        throws NotFoundException {
        CtClass[] parameterTypes = method.getParameterTypes();
        /**
         * 生成方法Args
         * <pre>
         * {@code
         *      public static class XMethodArgs<XXP> extends AbstractBeeMethodArgs<XXP> {
         *
         *         public XMethodArgs(DataAddition d) {
         *             super(d);
         *         }
         *
         *         //方法中的参数区
         *     }
         * }
         * </pre>
         */
        Builder methodArgsClassBuilder = TypeSpec.classBuilder(methodArgsClassName)
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .addTypeVariable(TypeVariableName.get("T"))
            .superclass(ParameterizedTypeName.get(
                ClassName.get(AbstractBeeMethodArgs.class),
                ClassName.bestGuess("T")))
            .addMethod(MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(DataAddition.class, "_bee_Data_Addition")
                .addStatement("super($N)", "_bee_Data_Addition")
                .build());
        if (!ArrayUtils.isEmpty(parameterTypes)) {
            ParameterizedTypeName parameterizedTypeName = ParameterizedTypeName.get(
                ClassName.bestGuess(methodArgsClassName),
                ClassName.bestGuess("T"));

            int pos = javassist.Modifier.isStatic(method.getModifiers()) ? 0 : 1;
            CodeAttribute codeAttribute = method.getMethodInfo().getCodeAttribute();
            LocalVariableAttribute localVariableAttribute
                = (LocalVariableAttribute) codeAttribute.getAttribute(LocalVariableAttribute.tag);

            for (int k = 0, ks = parameterTypes.length; k < ks; k++) {
                CtClass param = parameterTypes[k];
                String paramName = localVariableAttribute.variableName(k + pos);
                while ("this".equals(paramName)) {
                    paramName = localVariableAttribute.variableName(k + ++pos);
                }
                String paramType = param.getName().replace("$", ".");
                /**
                 * 生成参数
                 * <pre>
                 * {@code
                 *      public XMethodArgs<XXP> param(String param) {
                 *             String key = "param";
                 *             super._bee_add_Reqest_Params(key, param);
                 *             return this;
                 *         }
                 * }
                 * </pre>
                 */
                try {
                    methodArgsClassBuilder.addMethod(MethodSpec.methodBuilder(paramName)
                        .addModifiers(Modifier.PUBLIC)
                        .returns(parameterizedTypeName)
                        .addParameter(TypeVariableName.get(paramType), paramName)
                        .addStatement("super._bee_add_Reqest_Params($S,$N)", paramName, paramName)
                        .addStatement("return this")
                        .build());
                } catch (IllegalArgumentException e) {
                    StringBuilder builder = new StringBuilder();
                    builder.append("create methodArgsClassBuilder error. ")
                        .append(method.getLongName())
                        .append(" index = " + k + " [" + paramType + " " + paramName + "]")
                        .append(" variables: [");
                    int len = localVariableAttribute.tableLength();
                    for (int i = 0; i < len; i++) {
                        builder.append(i).append(" = ")
                            .append(localVariableAttribute.variableName(i))
                            .append(MojoEnv.SEPARATOR);
                    }
                    builder.append("]");
                    MojoEnv.getLogger().error(builder.toString());
                    throw new IllegalArgumentException(e);
                }

            }
        }
        return methodArgsClassBuilder.build();
        //end MethodArgs Build.
    }

    private static MethodSpec buildMethodDelegateMethod(CtMethod method, String uri,
        String methodDelegateClassName) {
        /**
         * 生成代理方法
         * {@code
         *      public static XMethodDelegate xMethod() {
         *          return new XMethodDelegate("http://localhost"+"/xMethod");
         *      }
         * }
         */
        return MethodSpec.methodBuilder(method.getName())
            .addJavadoc("@see $N#$N",
                method.getDeclaringClass().getName(),
                method.getName())
            .addJavadoc("\narthas: trace $N $N",
                method.getDeclaringClass().getName(),
                method.getName())
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .returns(ClassName.bestGuess(methodDelegateClassName))
            .addStatement("String uri = $T.ofNullable($T.getLocation()).orElse($S)",
                Optional.class, BeeEnvironment.class, MojoEnv.getLocation())
            .addStatement("return new $N(uri+$S)",
                methodDelegateClassName, uri)
            .build();
    }

    private static TypeSpec buildMethodDelegateClass(String returnTypeName,
        String methodDelegateClassName, String methodArgsClassName) {
        /**
         * 静态方法代理内部类
         *  <pre>
         * {@code
         *    public static class XMethodDelegate
         *      extends com.nascent.maven.plugin.bee.core.AbstractBeeMethodDelegate {
         * }
         * </pre>
         */
        Builder methodDelegateClass = TypeSpec.classBuilder(methodDelegateClassName)
            .superclass(AbstractBeeMethodDelegate.class)
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            /**
             * 构造方法
             * <pre>
             *     {@code
             *     public XMethodDelegate(java.lang.String _bee_Mapping_url) {
             *       super(_bee_Mapping_url);
             *     }
             *     }
             * </pre>
             */
            .addMethod(MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(String.class, "_bee_Mapping_url")
                .addStatement("super($N)", "_bee_Mapping_url").build())
            /**
             *  args方法
             * <pre>
             * {@code
             *     public XMethodArgs<com.nascent.maven.plugin.bee.BeeTestMojo$XXP> args() {
             *       return new XMethodArgs(this);
             *     }
             * }
             * </pre>
             */
            .addMethod(MethodSpec.methodBuilder("args")
                .addModifiers(Modifier.PUBLIC)
                .returns(ParameterizedTypeName.get(
                    ClassName.bestGuess(methodArgsClassName),
                    ClassName.bestGuess(returnTypeName)))
                .addStatement("return new $N<$N>(this)", methodArgsClassName, returnTypeName)
                .build())
            /**响应类方法**/
            .addMethod(MethodSpec.methodBuilder("_bee_Return_Type_Class")
                .addModifiers(Modifier.PUBLIC)
                .returns(Class.class)
                .addStatement("return $N.class", returnTypeName)
                .build());
        return methodDelegateClass.build();
    }

    private static String getReturnTypeName(CtClass returnType) {
        String returnTypeName = returnType.getName();
        if ("void".equals(returnTypeName)) {
            returnTypeName = Void.class.getName();
        } else {
            returnTypeName = returnTypeName.replace("$", ".");
        }
        return returnTypeName;
    }
}
