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
    public StepRegister onData(Function<T, Param> fun) {
        Param param = fun.apply(body);
        this.addParam(param);
        return register;
    }

    private void addParam(Param param) {
        String key = param.getKey();
        Object data = param.getValue();
        String strData = BeeEnvironment.getJsonParser()
            .beanToString(data);
        BeeEnvironment.getDataProvider()
            .addData(key, strData);
    }

    @Override
    public StepRegister onDatas(Function<T, List<Param>> fun) {
        List<Param> params = fun.apply(body);
        params.forEach(this::addParam);
        return register;
    }

//    private StepRegister toJsonNode(Object apply) {
//        //解析成KeyValue
//        ObjectMapper objectMapper = new ObjectMapper();
//        try {
//            String json = objectMapper.writeValueAsString(apply);
//            JsonNode jsonNode = objectMapper.readTree(json);
//            if (jsonNode.isArray()) {
//                for (JsonNode node : ((ArrayNode) jsonNode)) {
//                    register.registerBean((ObjectNode) node);
//                }
//            } else {
//                register.registerBean((ObjectNode) jsonNode);
//            }
//        } catch (IOException e) {
//            throw new JsonException(e);
//        }
//        return register;
//    }
}
