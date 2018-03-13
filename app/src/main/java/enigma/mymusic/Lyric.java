package enigma.mymusic;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.Context;
import android.util.Log;

/**
 * Created by ramir on 3/11/2018.
 */

public class Lyric {
    private String letra;
    private int tiempo;

    public Lyric(String s, int n){
        letra= s;
        tiempo= n;
    }
    public Lyric( String s)
    {
        int minuto= Integer.parseInt(s.substring(s.indexOf("[")+1,s.indexOf(":")));
        int segundo= Integer.parseInt(s.substring(s.indexOf(":")+1,s.indexOf(".")));
        tiempo= minuto*60+segundo;
        letra= s.substring(s.indexOf("]")+1);
    }

    public String getLetra() {
        return letra;
    }

    public void setLetra(String letra) {
        this.letra = letra;
    }

    public int getTiempo() {
        return tiempo;
    }

    public void setTiempo(int tiempo) {
        this.tiempo = tiempo;
    }
}
