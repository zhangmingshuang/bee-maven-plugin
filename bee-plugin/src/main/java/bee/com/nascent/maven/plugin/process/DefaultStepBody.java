package bee.com.nascent.maven.plugin.process;

import java.util.List;
import java.util.function.Function;

/**
 * .
 *
 * @author zhangmsh
 * @version 1.0.0
 * @date 2020/5/17
 */
public class DefaultStepBody<T> implements StepBodySupport<T> {

    private T body;
    private StepRegister register;

    public DefaultStepBody(T body, StepRegister register) {
        this.body = body;
        this.register = register;
    }

    @Override
    public StepRegister onData(Function<T, Param> fun, boolean required) {
        Param param = fun.apply(body);
        this.addParam(param, required);
        return register;
    }

    private void addParam(Param param, boolean required) {
        String key = param.getKey();
        Object data = param.getValue();
        String strData = data instanceof CharSequence
            ? data.toString()
            : __BeeEnvironment.getJsonParser().beanToString(data);
        __BeeEnvironment.getDataProvider()
            .addData(key, strData);
        if (required) {
            __BeeEnvironment.getDataProvider().addRequiredParam(param.getKey());
        }
    }

    @Override
    public StepRegister onDatas(Function<T, List<Param>> fun, boolean required) {
        List<Param> params = fun.apply(body);
        params.forEach(param -> this.addParam(param, required));
        return register;
    }

}
