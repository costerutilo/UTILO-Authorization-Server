package eu.utilo.authorization.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;


public final class SingletonMapDeserializer extends JsonDeserializer<Map<?, ?>> {

    @Override
    public Map<?, ?> deserialize(JsonParser parser, DeserializationContext context) throws IOException {

        ObjectMapper mapper = (ObjectMapper) parser.getCodec();
        JsonNode mapNode = mapper.readTree(parser);
        Map<String, Object> result = new LinkedHashMap<>();
        if (mapNode != null && mapNode.isObject()) {
            Iterable<Map.Entry<String, JsonNode>> fields = mapNode::fields;
            for (Map.Entry<String, JsonNode> field : fields) {
                result.put(field.getKey(), mapper.readValue(field.getValue().traverse(mapper), Object.class));
            }
        }
        if (result.size() == 1) {
            for (Map.Entry<String, Object> entry : result.entrySet()) {
                return Collections.singletonMap(entry.getKey(), entry.getValue());
            }
        }
        return result;

    }

}
