package bee.com.nascent.maven.plugin.process;

import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * .
 *
 * @author zhangmsh
 * @version 1.0.0
 * @date 2020/5/18
 */
public interface ParamRegister {

    default ParamRegister location(String location) {
        BeeEnvironment.setLocation(location);
        return this;
    }

//    ParamRegister register(String... paramNames);

    ParamRegister onParam(String keyName, Supplier<String> valueSupplier);

    ParamRegister onParam(String keyName, String value);

    ParamRegister sign(String signKeyName, Function<Map<String, String>, String> fun);

}
