package eu.utilo.authorization.jackson;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.Map;


@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
@JsonDeserialize(using = SingletonMapDeserializer.class)
public abstract class SingletonMapMixin {

    @JsonCreator
    SingletonMapMixin(Map<?, ?> map) {
    }

}
