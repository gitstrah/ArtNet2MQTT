package net.ionjet.artnet2mqtt;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DmxChange {
    private int channel;
    private int value;
}
