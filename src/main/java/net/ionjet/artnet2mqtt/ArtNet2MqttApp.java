package net.ionjet.artnet2mqtt;

import net.ionjet.artnet2mqtt.config.Config;
import net.ionjet.artnet2mqtt.config.IpHelper;
import net.ionjet.artnet2mqtt.rules.Rule;
import net.ionjet.artnet2mqtt.rules.RuleFactory;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class ArtNet2MqttApp {
    // TODO: handle disconnects
    // TODO: config file name into args
    public static void main(String[] args) throws IOException, MqttException {
        Config config = getConfig();
        // ArtNet
        DatagramSocket socket = new DatagramSocket(config.getArtNetPort(), IpHelper.getAddress(config.getArtNetIp()));
        byte[] buf = new byte[1024];
        DatagramPacket packet = new DatagramPacket(buf, buf.length);

        // MQTT
        String publisherId = UUID.randomUUID().toString();
        IMqttClient publisher = new MqttClient(String.format("tcp://%s:%d", config.getMqttHost(), config.getMqttPort()),
                publisherId);
        MqttConnectOptions options = new MqttConnectOptions();
        options.setAutomaticReconnect(true);
        options.setCleanSession(true);
        options.setConnectionTimeout(10);
        publisher.connect(options);

        // Vars
        byte[] oldData = new byte[512];

        // Rules
        List<Rule> rules = config.getRules().stream()
                .map(ruleConfig -> RuleFactory.create(ruleConfig, publisher))
                .collect(Collectors.toList());


        while (true) {
            try {
                socket.receive(packet);
                ArtNetPacket obj = ArtNetPacket.parse(packet.getData(), packet.getLength());
                List<DmxChange> dmxChanges = new ArrayList<>();
                for (int i = 0; i < Math.min(512, obj.getLength()); i++) {
                    byte value = obj.getData()[i];
                    if(obj.getData()[i] != oldData[i]) {
                        dmxChanges.add(DmxChange.builder()
                                .channel(i)
                                .value(value < 0 ? 256 + value : value)
                                .build());
                    }
                    oldData[i] = obj.getData()[i];
                }
                if (!dmxChanges.isEmpty()) {
                    rules.forEach(rule -> rule.process(dmxChanges));
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

        }
    }

    private static Config getConfig() {
        return Config.fromFile("src/config/config.json");
    }

}
