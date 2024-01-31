package ch.heig;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.UUID;

import static java.nio.charset.StandardCharsets.UTF_8;

public class Musicien {
    private String uuid;
    private transient String instrument;
    private String son;
    private long tempsModif;

    public Musicien(String instrument, String son ){
        this.instrument = instrument;
        this.son = son;
        this.uuid = UUID.randomUUID().toString();
        this.tempsModif = System.currentTimeMillis();
    }


    void envoiSon(){
        try (DatagramSocket socket = new DatagramSocket()) {

            tempsModif = System.currentTimeMillis();

            Gson gson = new Gson();
            String message = gson.toJson(this);
            byte[] payload = message.getBytes(UTF_8);
            var dest_address = new InetSocketAddress(MusicianApp.IPADDRESS, MusicianApp.PORT);
            var packet = new DatagramPacket(payload,
                    payload.length,
                    dest_address);
            socket.send(packet);

        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
}
