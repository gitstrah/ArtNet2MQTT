package net.ionjet.artnet2mqtt.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;
import lombok.experimental.Wither;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Wither
public class Config {
    private final static Logger LOGGER = Logger.getLogger(Config.class.getName());

    private static Integer ART_NET_PORT = 6454;
    private static Integer MQTT_PORT = 1883;

    private String artNetIp;
    private Integer artNetPort;
    private String mqttHost;
    private Integer mqttPort;
    private List<RuleConfig> rules;

    public static Config getDefault() {
        return Config.builder()
                .artNetPort(ART_NET_PORT)
                .artNetIp("0.0.0.0")
                .mqttHost("localhost")
                .mqttPort(MQTT_PORT)
                .build();
    }

    public static Config fromFile(String fileName) {
        File file = new File(fileName);
        try {
            return new ObjectMapper().readValue(file, Config.class);
        } catch (IOException e) {
            LOGGER.warning(e.getMessage());
            LOGGER.warning("Loading default configuration");
            e.printStackTrace();
            return getDefault();
        }
    }
}
