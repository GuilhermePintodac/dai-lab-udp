package ch.heig;

import java.util.HashMap;
import java.util.Map;

public class MusicianApp {
    final static String IPADDRESS = "239.255.22.5";
    final static int PORT = 9904;

    final private Map<String, String> sonInstrument;

    public MusicianApp() {
        this.sonInstrument = new HashMap<>();
        initSonsInstrument();
    }

    private void initSonsInstrument() {
        sonInstrument.put("piano", "ti-ta-ti");
        sonInstrument.put("trumpet", "pouet");
        sonInstrument.put("flute", "trulu");
        sonInstrument.put("violin", "gzi-gzi");
        sonInstrument.put("drum", "boum-boum");
    }

    public String getSon(String instrument) {
        return sonInstrument.get(instrument);
    }

    public static void main(String[] args) throws InterruptedException {
        if (args.length == 0){
            System.out.println("Erreur: Instrument manquant");
        }

        String instrument = args[0];

        MusicianApp musicianApp = new MusicianApp();
        Musicien musicien = new Musicien(instrument, musicianApp.getSon(instrument));

        while (true){
            musicien.envoiSon();
            Thread.sleep(1000);
        }
    }
}