package ch.heig;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.*;
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

    public void setInstrument(String instrument) {
        this.instrument = instrument;
    }

    public static Musicien jsonEnMusicien(String json){
        Gson gson = new Gson();
        Musicien musicien = gson.fromJson(json, Musicien.class);
        AuditorApp app = new AuditorApp();
        musicien.setInstrument(app.getInstrument(musicien.son));
        return musicien;
    }

    public String musicienEnJson(){
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public String getUuid() {
        return uuid;
    }

    public long getTempsModif() {
        return tempsModif;
    }

    public void setTempsModif(long tempsModif) {
        this.tempsModif = tempsModif;
    }
}

