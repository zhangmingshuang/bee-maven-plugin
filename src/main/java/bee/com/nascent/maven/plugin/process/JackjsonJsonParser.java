package bee.com.nascent.maven.plugin.process;

import bee.com.nascent.maven.plugin.process.JsonParser.Configuration;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * .
 *
 * @author zhangmsh
 * @version 1.0.0
 * @date 2020/5/21
 */
public class JackjsonJsonParser implements JsonParser, Configuration {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Configuration configuration() {
        return this;
    }

    @Override
    public Configuration dateFormat(DateFormat dateFormat) {
        objectMapper.setDateFormat(dateFormat);
        return this;
    }

    @Override
    public <T> T writeValueAsString(T body, Class<T> clazz) {
        try {
            if (body.getClass().equals(clazz)) {
                return body;
            }
            String json = objectMapper.writeValueAsString(body);
            return objectMapper.readValue(json, clazz);
        } catch (IOException e) {
            throw new JsonException(e);
        }
    }

    @Override
    public <T> T readValue(String data, Type type) {
        try {
            JavaType javaType = TypeFactory.defaultInstance().constructType(type);
            return objectMapper.readValue(data, javaType);
        } catch (IOException e) {
            throw new JsonException(e);
        }
    }

    @Override
    public <T> String writeValueAsString(T body) {
        try {
            return objectMapper.writeValueAsString(body);
        } catch (IOException e) {
            throw new JsonException(e);
        }
    }

    @Override
    public <T> byte[] writeValueAsByte(T body) {
        try {
            return objectMapper.writeValueAsBytes(body);
        } catch (IOException e) {
            throw new JsonException(e);
        }
    }

    @Override
    public <T> Map<String, Object> writeValueAsMap(T data) {
        try {
            JsonNode jsonNode = objectMapper.readTree(this.writeValueAsString(data));
            Map<String, Object> params = new HashMap<>();
            jsonNode.fields().forEachRemaining(entry -> {
                JsonNode value = entry.getValue();
                Object strValue;
                if (value.isNull()) {
                    strValue = null;
                } else if (value.isTextual()) {
                    strValue = value.textValue();
                } else {
                    strValue = value;
                }
                params.put(entry.getKey(), strValue);
            });
            return params;
        } catch (IOException e) {
            throw new JsonException(e);
        }
    }

}
