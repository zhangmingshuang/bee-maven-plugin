package com.nascent.maven.plugin.bee.utils;

import java.util.Collection;
import java.util.Map;
import javassist.CtClass;
import javassist.bytecode.ClassFile;
import lombok.NonNull;

/**
 * .
 *
 * @author zhangmsh
 * @version 1.0.0
 * @date 2020/5/11
 */
public class CollectionUtils {

    private CollectionUtils() {

    }

    private static final String COLLECTION_FULL_NAME = Collection.class.getTypeName();
    private static final String MAP_FULL_NAME = Map.class.getTypeName();

    /**
     * @param clazz
     * @return
     * @see Collection
     */
    public static boolean isCollection(CtClass clazz) {
        return isInterface(clazz, COLLECTION_FULL_NAME);
    }

    public static boolean isInterface(@NonNull CtClass clazz, @NonNull String interfaceName) {
        ClassFile classFile2 = clazz.getClassFile2();
        if (classFile2 == null) {
            return false;
        }
        String[] interfaces = classFile2.getInterfaces();
        if (ArrayUtils.isEmpty(interfaces)) {
            return false;
        }
        for (String anInterface : interfaces) {
            if (interfaceName.equals(anInterface)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isMap(CtClass clazz) {
        return isInterface(clazz, MAP_FULL_NAME);
    }

    public static boolean isEmpty(Collection collection) {
        return collection == null || collection.isEmpty();
    }

    public static boolean isEmpty(Map values) {
        return values == null || values.isEmpty();
    }


}
