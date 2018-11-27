package net.ionjet.artnet2mqtt.config;

import java.net.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

public class IpHelper {
    private final static Logger LOGGER = Logger.getLogger(IpHelper.class.getName());
    /**
     * Gets an available IPv4 non-virtual address or provided one
     * Works fine if just a single interface is connected (i.e either WiFi or Ethernet)
     * */
    public static InetAddress getAddress(String defaultIp) throws UnknownHostException {
        List<InetAddress> inetAddresses = new ArrayList<>();
        try {

            Collections.list(NetworkInterface.getNetworkInterfaces()).stream()
                    .filter(i -> {
                        try {
                            return i.isUp() && !i.isVirtual();
                        } catch (SocketException e) {
                            LOGGER.warning(e.getMessage());
                            e.printStackTrace();
                            return false;
                        }
                    })
                    .forEach(i -> {
                        // IPv4 only
                        Optional<InterfaceAddress> address = i.getInterfaceAddresses().stream()
                                .filter(ia -> ia.getBroadcast() != null)
                                .findFirst();
                        if (address.isPresent()) {
                            LOGGER.info(String.format("%s - %s", i.getDisplayName(), address.get().toString()));
                            inetAddresses.add(address.get().getAddress());
                        }

                    });
        } catch (SocketException e) {
            LOGGER.warning(e.getMessage());
            e.printStackTrace();
        }
        if(inetAddresses.size() != 1) {
            LOGGER.info(String.format("Ambiguous IP address or no IP addresses found. Using default: %s", defaultIp));
            return InetAddress.getByName(defaultIp);
        }
        LOGGER.info(String.format("Using IP: %s", inetAddresses.get(0).toString()));
        return inetAddresses.get(0);
    }
}
