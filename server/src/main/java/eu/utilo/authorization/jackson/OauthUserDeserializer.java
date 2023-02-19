package eu.utilo.authorization.jackson;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.MissingNode;
import eu.utilo.authorization.entity.OauthUser;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.io.IOException;
import java.util.Set;

public class OauthUserDeserializer extends JsonDeserializer<OauthUser> {

    private static final TypeReference<Set<SimpleGrantedAuthority>> SIMPLE_GRANTED_AUTHORITY_SET =
        new TypeReference<Set<SimpleGrantedAuthority>>() {};

    @Override
    public OauthUser deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JacksonException {

        ObjectMapper mapper = (ObjectMapper) jp.getCodec();
        JsonNode jsonNode = mapper.readTree(jp);
        Set<? extends GrantedAuthority> authorities = mapper.convertValue(jsonNode.get("authorities"),
                SIMPLE_GRANTED_AUTHORITY_SET);
        JsonNode passwordNode = readJsonNode(jsonNode, "password");
        String username = readJsonNode(jsonNode, "username").asText();
        String password = passwordNode.asText("");
        boolean enabled = readJsonNode(jsonNode, "enabled").asBoolean();
        boolean accountNonExpired = readJsonNode(jsonNode, "accountNonExpired").asBoolean();
        boolean credentialsNonExpired = readJsonNode(jsonNode, "credentialsNonExpired").asBoolean();
        boolean accountNonLocked = readJsonNode(jsonNode, "accountNonLocked").asBoolean();
        OauthUser result =
                new OauthUser(
                        username,
                        password,
                        accountNonExpired,
                        accountNonLocked,
                        credentialsNonExpired,
                        enabled);
        // FIXME: set Roles
        if (passwordNode.asText(null) == null) {
            result.setPassword("******");
        }
        return result;

    }

    private JsonNode readJsonNode(JsonNode jsonNode, String field) {

        return jsonNode.has(field) ? jsonNode.get(field) : MissingNode.getInstance();

    }

}
