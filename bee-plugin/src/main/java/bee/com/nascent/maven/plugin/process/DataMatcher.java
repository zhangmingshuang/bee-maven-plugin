package bee.com.nascent.maven.plugin.process;

/**
 * .
 *
 * @author zhangmsh
 * @version 1.0.0
 * @date 2020/5/22
 */
public interface DataMatcher<T> {

    /**
     * 匹配器
     *
     * @param a 值A
     * @param b 值B
     * @return 值A与值B是否相等
     */
    boolean match(T a, T b);
}
