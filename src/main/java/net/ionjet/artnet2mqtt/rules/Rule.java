package net.ionjet.artnet2mqtt.rules;

import net.ionjet.artnet2mqtt.config.RuleConfig;
import net.ionjet.artnet2mqtt.DmxChange;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.eclipse.paho.client.mqttv3.IMqttClient;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public abstract class Rule {
    protected RuleConfig config;
    protected IMqttClient client;

    public Rule(RuleConfig config) {
        this.config = config;
    }
    public abstract boolean process(List<DmxChange> dmxChanges);
}
