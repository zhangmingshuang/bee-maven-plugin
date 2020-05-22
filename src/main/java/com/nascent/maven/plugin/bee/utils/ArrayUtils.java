package com.nascent.maven.plugin.bee.utils;

/**
 * .
 *
 * @author zhangmsh
 * @version 1.0.0
 * @date 2020/5/11
 */
public class ArrayUtils {

    private ArrayUtils() {

    }

    public static boolean isEmpty(Object[] array) {
        return array == null || array.length < 1;
    }

    public static <T> T[] emptyOrElse(T[] value, T[] path) {
        if (isEmpty(value)) {
            return path;
        }
        return value;
    }
}
