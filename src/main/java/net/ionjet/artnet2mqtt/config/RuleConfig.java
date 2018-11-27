package net.ionjet.artnet2mqtt.config;

import lombok.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class RuleConfig {
    private Set<Integer> channels;
    private RuleType type;
    private Map<String, Object> properties;

    public RuleConfig() {
        properties = new HashMap<>();
    }

    public RuleConfig set(String name, Object value) {
        if(properties == null) {
            properties = new HashMap<>();
        }
        properties.put(name, value);
        return this;
    }

    public Integer getInteger(String name) {
        Object value = properties.get(name);
        if(value == null) return 0;
        if(Integer.class.isAssignableFrom(value.getClass())) {
            return (Integer)value;
        }
        return Integer.getInteger(value.toString());
    }
    public String getString(String name) {
        Object value = properties.get(name);
        if(value == null) return null;
        return value.toString();
    }

}
