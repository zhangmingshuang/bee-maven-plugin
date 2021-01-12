package bee.com.nascent.maven.plugin.process;

/**
 * .
 *
 * @author zhangmsh
 * @version 1.0.0
 * @date 2020/5/22
 */
public interface DataMatcher<T> {

  boolean match(T a, T b);
}
