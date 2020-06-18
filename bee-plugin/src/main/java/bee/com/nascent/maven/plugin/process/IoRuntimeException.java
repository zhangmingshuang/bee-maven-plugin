package bee.com.nascent.maven.plugin.process;

import java.io.IOException;

/**
 * .
 *
 * @author zhangmsh
 * @version 1.0.0
 * @date 2020/5/19
 */
public class IoRuntimeException extends RuntimeException {

    public IoRuntimeException(String message) {
        super(message);
    }

    public IoRuntimeException(IOException e) {
        super(e);
    }
}
