package bee.com.nascent.maven.plugin.process;

/**
 * .
 *
 * @author zhangmsh
 * @version 1.0.0
 * @date 2020/5/15
 */
@SuppressWarnings({"java:S116", "java:S117"})
public abstract class AbstractBeeMethodDelegate implements BeeMethodDelegate, DataAddition {

  static {
    WebUtils.init();
  }

  private String _bee_Mapping_url;

  public AbstractBeeMethodDelegate(String _bee_Mapping_url) {
    this._bee_Mapping_url = _bee_Mapping_url;
  }

  @Override
  public String _bee_Mapping_url() {
    return _bee_Mapping_url;
  }
}
