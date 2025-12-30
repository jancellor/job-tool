package uk.jchancellor.jobtool;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

@Component
public class ObjectMerger {

    private final ObjectMapper mergeMapper;

    public ObjectMerger(ObjectMapper objectMapper) {
        this.mergeMapper = objectMapper.copy();
        this.mergeMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

    }

    @SneakyThrows
    public <T> T merge(T object, T overrides) {
        if (object == null) return overrides;
        if (overrides == null) return object;
        @SuppressWarnings("unchecked")
        T copy = mergeMapper.convertValue(object, (Class<T>) object.getClass());
        return mergeMapper.updateValue(copy, overrides);
    }
}
