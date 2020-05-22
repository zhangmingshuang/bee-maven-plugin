package bee.com.nascent.maven.plugin.process;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Map;
import javafx.util.Pair;

/**
 * .
 *
 * @author zhangmsh
 * @version 1.0.0
 * @date 2020/5/21
 */
public interface JsonParser {

    public static interface Configuration {

        default Configuration dateFormat(String pattern) {
            return this.dateFormat(new SimpleDateFormat(pattern));
        }

        Configuration dateFormat(DateFormat dateFormat);
    }

    @FunctionalInterface
    public static interface PairSupplier<T, R> {

        Pair<T, R> pair();
    }

    Configuration configuration();

    <T> T writeValueAsString(T body, Class<T> clazz);

    <T> String writeValueAsString(T body);

    <T> byte[] writeValueAsByte(T body);

    <T> T readValue(String data, Type type);

    <T> Map<String, Object> writeValueAsMap(T body);

    default <T> String beanToString(T body) {
        if (body.getClass().isPrimitive()) {
            return body.toString();
        }
        return this.writeValueAsString(body);
    }
}
