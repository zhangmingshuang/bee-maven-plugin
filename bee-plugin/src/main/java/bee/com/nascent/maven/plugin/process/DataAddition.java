package bee.com.nascent.maven.plugin.process;

/**
 * .
 *
 * @author zhangmsh
 * @version 1.0.0
 * @date 2020/5/15
 */
@SuppressWarnings("java:S100")
public interface DataAddition {

  /**
   * 获得请求地址
   *
   * @return
   */
  String _bee_Mapping_url();

  /**
   * 获取响应类型
   *
   * @return
   */
  Class _bee_Return_Type_Class();
}
