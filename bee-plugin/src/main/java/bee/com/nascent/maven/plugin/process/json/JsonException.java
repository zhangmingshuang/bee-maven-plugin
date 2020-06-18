package bee.com.nascent.maven.plugin.process.json;

/**
 * .
 *
 * @author zhangmsh
 * @version 1.0.0
 * @date 2020/5/18
 */
public class JsonException extends RuntimeException {

    public JsonException(Throwable e) {
        super(e);
    }

    public JsonException(String message) {
        super(message);
    }
}
