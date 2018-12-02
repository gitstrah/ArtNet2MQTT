package net.ionjet.artnet2mqtt.rules;

import net.ionjet.artnet2mqtt.DmxChange;
import net.ionjet.artnet2mqtt.config.RuleConfig;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class TasmotaRule extends Rule {
    private final static Logger LOGGER = Logger.getLogger(TasmotaRule.class.getName());

    private static final String VALUE_OFF = "0";
    private static final String VALUE_ON = "1";
private static final String PROPERTY_TOPIC = "topic";
private static final String PROPERTY_TRESHOLD = "treshold";

    private String lastValue = null;

    public TasmotaRule(RuleConfig config, IMqttClient client) {
        super(config, client);
        LOGGER.info(String.format("[Tasmota Rule] DMX channel: %s, MQTT topic: '%s'",
                String.join(",", config.getChannels().stream()
                        .map(Object::toString)
                        .collect(Collectors.toList())),
                config.getString(PROPERTY_TOPIC)));
    }

    @Override
    public boolean process(List<DmxChange> dmxChanges) {
        if (dmxChanges.stream().anyMatch(dmxChange -> config.getChannels().contains(dmxChange.getChannel()))) {
            dmxChanges.forEach(dmxChange -> {
                if (config.getChannels().contains(dmxChange.getChannel())) {
                    try {
                        String topic = config.getString(PROPERTY_TOPIC);
                        String value = dmxChange.getValue() >= config.getInteger(PROPERTY_TRESHOLD) ? VALUE_ON : VALUE_OFF;
                        if(!value.equals(lastValue)) {
                            MqttMessage msg = new MqttMessage(value.getBytes());
                            msg.setQos(0);
                            msg.setRetained(true);
                            client.publish(topic, msg);
                            lastValue = value;
                            LOGGER.info(String.format("%s - %s", topic, value));
                        }
                    } catch (MqttException e) {
                        e.printStackTrace();
                        LOGGER.warning(e.getMessage());
                    }
                }
            });
            return true;
        }
        return false;
    }
}
