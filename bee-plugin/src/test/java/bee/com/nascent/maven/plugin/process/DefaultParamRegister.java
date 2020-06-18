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

    @Override
    public ParamRegister onParam(String keyName, Supplier<String> valueSupplier, boolean required) {
        __BeeEnvironment.getDataProvider().addData(keyName, valueSupplier);
        if (required) {
            __BeeEnvironment.getDataProvider().addRequiredParam(keyName);
        }
        return this;
    }

    @Override
    public ParamRegister onParam(String keyName, String value, boolean required) {
        __BeeEnvironment.getDataProvider().addData(keyName, value);
        if (required) {
            __BeeEnvironment.getDataProvider().addRequiredParam(keyName);
        }
        return this;
    }

    @Override
    public ParamRegister sign(String signKeyName, Function<Map<String, String>, String> fun) {
        __BeeEnvironment.getSignature().config(signKeyName, fun);
        return this;
    }
}
