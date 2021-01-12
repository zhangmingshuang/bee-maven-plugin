package bee.com.nascent.maven.plugin.process;

import java.io.IOException;

/**
 * .
 *
 * @author zhangmsh
 * @version 1.0.0
 * @date 2020/5/19
 */
public class IORuntimeException extends RuntimeException {

  public IORuntimeException(String message) {
    super(message);
  }

  public IORuntimeException(IOException e) {
    super(e);
  }
}
