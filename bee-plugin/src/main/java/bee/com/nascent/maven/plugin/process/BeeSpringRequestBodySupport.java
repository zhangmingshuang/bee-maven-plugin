package bee.com.nascent.maven.plugin.process;

/**
 * .
 *
 * @author zhangmsh
 * @version 1.0.0
 * @date 2020/5/15
 */
public interface BeeSpringRequestBodySupport {

  /**
   * 标注为RequestBody
   *
   * @return
   */
  BeeHttpRequestSupport toRequestBody();
}
