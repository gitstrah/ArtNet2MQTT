package net.ionjet.artnet2mqtt.rules;

import net.ionjet.artnet2mqtt.config.RuleConfig;
import org.eclipse.paho.client.mqttv3.IMqttClient;

public class RuleFactory {

    public static Rule create(RuleConfig ruleConfig, IMqttClient client) {
        switch (ruleConfig.getType()) {
            case NONE:
                break;
            case TASMOTA:
                return new TasmotaRule(ruleConfig, client);
            case RENAME:
                break;
        }
        return null;
    }
}
