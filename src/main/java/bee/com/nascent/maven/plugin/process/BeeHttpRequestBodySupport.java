package bee.com.nascent.maven.plugin.process;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.function.LongConsumer;

/**
 * .
 *
 * @author zhangmsh
 * @version 1.0.0
 * @date 2020/5/15
 */
public interface BeeHttpRequestBodySupport<T> {

    BeeAsserts<T> asserts();

    T body();

    long timeUsed();

    default BeeHttpRequestBodySupport<T> timeUsed(OutputStream os) {
        try {
            long t = timeUsed();
            os.write(("time used " + t + "ms").getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
        return this;
    }

    default BeeHttpRequestBodySupport<T> timeUsed(LongConsumer consumer) {
        consumer.accept(timeUsed());
        return this;
    }
}
