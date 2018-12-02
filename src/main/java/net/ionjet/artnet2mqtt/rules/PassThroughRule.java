package net.ionjet.artnet2mqtt.rules;

import net.ionjet.artnet2mqtt.config.RuleConfig;
import net.ionjet.artnet2mqtt.DmxChange;
import org.eclipse.paho.client.mqttv3.IMqttClient;

import java.util.List;

public class PassThroughRule extends Rule {

    public PassThroughRule(RuleConfig config, IMqttClient client) {
        super(config, client);
    }

    @Override
    public boolean process(List<DmxChange> dmxChanges) {
        return false;
    }
}
