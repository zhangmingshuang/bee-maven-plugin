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
public class DefaultParamRegister implements ParamRegister {

//    @Override
//    public ParamRegister register(String... paramNames) {
//        BeeEnvironment.addGlobalRequestParam(paramNames);
//        return this;
//    }

    @Override
    public ParamRegister onParam(String keyName, Supplier<String> valueSupplier) {
        BeeEnvironment.getDataProvider().addData(keyName, valueSupplier);
        return this;
    }

    @Override
    public ParamRegister onParam(String keyName, String value) {
        BeeEnvironment.getDataProvider().addData(keyName, value);
        return this;
    }

    @Override
    public ParamRegister sign(String signKeyName, Function<Map<String, String>, String> fun) {
        BeeEnvironment.getSignature().config(signKeyName, fun);
        return this;
    }
}
