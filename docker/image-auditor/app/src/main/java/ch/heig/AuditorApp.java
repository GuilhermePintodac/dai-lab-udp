package ch.heig;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.nio.charset.StandardCharsets;

public class AuditorApp {

    final String IPADDRESS_UDP = "239.255.22.5";
    final int PORT_UDP = 9904;
    final int PORT_TCP = 2205;

    final private Map<String, String> sonInstrument;

    List<Musicien> musiciens;

    public AuditorApp() {
        this.sonInstrument = new HashMap<>();
        this.musiciens = new ArrayList<>();
        initSonsInstrument();
    }

    private void initSonsInstrument() {
        sonInstrument.put("ti-ta-ti", "piano");
        sonInstrument.put("pouet", "trumpet");
        sonInstrument.put("trulu", "flute");
        sonInstrument.put("gzi-gzi", "violin");
        sonInstrument.put("boum-boum", "drum");
    }

    public String getInstrument(String son) {
        return sonInstrument.get(son);
    }

    private void recevoirSon() throws IOException {
        MulticastSocket socket = new MulticastSocket(PORT_UDP);
        var group_address = new InetSocketAddress(IPADDRESS_UDP, PORT_UDP);
        NetworkInterface netif = NetworkInterface.getByName("eth0");


        try (socket) {

            socket.joinGroup(group_address, netif);
            while(true){
                byte[] buffer = new byte[1024];
                var packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                String message = new String(packet.getData(), 0,
                        packet.getLength(), StandardCharsets.UTF_8);
                System.out.println("Received message: " + message +
                        " from " + packet.getAddress() +
                        ", port " + packet.getPort());
                Musicien musicien = Musicien.jsonEnMusicien(message);
                boolean misAJour = false;
                for (Musicien m :musiciens){
                    if (m.getUuid().equals(musicien.getUuid()) ){
                        m.setTempsModif(musicien.getTempsModif());
                        misAJour = true;
                    }
                }

                if (!misAJour){
                    musiciens.add(musicien);
                }
            }

        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }finally {
            try {
                if (socket.isBound()) {
                    socket.leaveGroup(group_address, netif);
                }
            } catch (IOException e) {
                System.out.println("Erreur lors de la sortie du groupe : " + e.getMessage());
            }
        }
    }

    private void envoiInfos(){
        try (ServerSocket serverSocket = new ServerSocket(PORT_TCP)) {
            while (true) {
                try (Socket socket = serverSocket.accept();
                     BufferedReader in = new BufferedReader(
                             new InputStreamReader(
                                     socket.getInputStream(), StandardCharsets.UTF_8));
                     BufferedWriter out = new BufferedWriter(
                             new OutputStreamWriter(
                                     socket.getOutputStream(), StandardCharsets.UTF_8))){

                    StringBuilder line = new StringBuilder();
                    for (Musicien musicien :musiciens){
                        if (System.currentTimeMillis() - musicien.getTempsModif() <= 5000){
                            line.append(musicien.musicienEnJson() + '\n');
                        }
                    }
                    if (line.isEmpty()){
                        line.append("[]");
                    }
                    out.write(line.toString() + '\n');

                    out.flush();
                } catch (IOException e) {
                    System.out.println("Server: socket ex.: " + e);
                }
            }
        } catch (IOException e) {
            System.out.println("Server: server socket ex.: " + e);
        }
    }

    public static void main(String[] args) {
        AuditorApp auditorApp = new AuditorApp();

        Thread recevoirSon = new Thread(() -> {
            try {
                auditorApp.recevoirSon();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        Thread envoiInfos = new Thread(auditorApp::envoiInfos);

        recevoirSon.start();
        envoiInfos.start();

    }
}