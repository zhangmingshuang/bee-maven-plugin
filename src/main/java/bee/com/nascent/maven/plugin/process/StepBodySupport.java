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
public interface StepBodySupport<T> {

    /**
     * 注册一个键值对
     *
     * @param fun
     * @return
     */
    StepRegister onData(Function<T, Param> fun);

    StepRegister onDatas(Function<T, List<Param>> fun);
}
