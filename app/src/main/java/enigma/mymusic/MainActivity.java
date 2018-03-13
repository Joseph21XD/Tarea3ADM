package enigma.mymusic;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import static java.util.Arrays.asList;

public class MainActivity extends AppCompatActivity {
    static ArrayList<Lyric> lyrics= new ArrayList<>();
    static int index=0;
    Timer timer = new Timer();
    MediaPlayer mediaPlayer;
    AudioManager audioManager;
    TextView v1,v2,v3,v4;
    static boolean usuarioCambia= false;
    static boolean termina= false;

    public void playPauseButton(View view){
        Button button= (Button)view;
        if(mediaPlayer.isPlaying()){
            mediaPlayer.pause();
            button.setBackgroundResource(R.drawable.playbutton1);
            v1.setTextColor(v1.getResources().getColor(R.color.colorAccent));
            v2.setTextColor(v1.getResources().getColor(R.color.colorAccent));
            v3.setTextColor(v1.getResources().getColor(R.color.colorAccent));
        }
        else{
            mediaPlayer.start();
            button.setBackgroundResource(R.drawable.pausebutton1);
            v1.setTextColor(v1.getResources().getColor(R.color.aqua));
            v2.setTextColor(v1.getResources().getColor(R.color.aqua));
            v3.setTextColor(v1.getResources().getColor(R.color.aqua));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mediaPlayer = MediaPlayer.create(this, R.raw.believer);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        v1= findViewById(R.id.textView);
        v2= findViewById(R.id.textView2);
        v3= findViewById(R.id.textView3);
        v4= findViewById(R.id.textView4);
        v4.setText("Believer, Imagine Dragons");
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        procesaString(R.raw.believer_lrc);
        SeekBar volumeSeekBar = (SeekBar) findViewById(R.id.volumeSeekBar);
        volumeSeekBar.setProgress(currentVolume);
        volumeSeekBar.setMax(maxVolume);


        //Manejo de Volumen
        volumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        final SeekBar advanceSeekBar = (SeekBar) findViewById(R.id.advanceSeekBar);
        int duration = mediaPlayer.getDuration();
        int progress = mediaPlayer.getCurrentPosition();
        advanceSeekBar.setMax(duration);
        advanceSeekBar.setProgress(progress);

        advanceSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    int i= progress/1000;
                    cambiaLyricsPos(i);
                    mediaPlayer.seekTo(progress);}

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        v3.setText(lyrics.get(0).getLetra());

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable(){
                    @Override
                    public void run(){
                        advanceSeekBar.setProgress(mediaPlayer.getCurrentPosition());
                        if(mediaPlayer.getCurrentPosition()+1000>= mediaPlayer.getDuration()) {
                            Button button= findViewById(R.id.button2);
                            button.setBackgroundResource(R.drawable.playbutton1);
                            mediaPlayer.seekTo(0);
                            mediaPlayer.pause();
                            index=0;
                            termina=false;
                            v1.setText("");
                            v2.setText("");
                            v3.setText(lyrics.get(0).getLetra());
                        }
                        int i=mediaPlayer.getCurrentPosition()/1000;
                        cambiaLyrics(i);
                    }
                });

            }
        }, 0, 1000);

        ListView listView= findViewById(R.id.listView);

        final ArrayList<String> elementos= new ArrayList<String>(asList("So Far Away, Avenged Sevenfold","I Dont Want to Miss a Thing, Aerosmith","Believer, Imagine Dragons", "Dream On, Aerosmith", "Bohemian Rhapsody, Queen"));
        listView.setAdapter(new ArrayAdapter<String>(this, R.layout.my_list, elementos) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView textView = (TextView) super.getView(position, convertView, parent);
                if(position%2==0)
                    textView.setTextColor(MainActivity.this.getResources().getColor(R.color.colorAccent));
                else
                    textView.setTextColor(MainActivity.this.getResources().getColor(R.color.aqua));
                return textView;
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ver(elementos.get(i));
            }
        });
    }

    private void cambiaLyricsPos(int progress) {
        Log.d("PROGRESOOOOOOOOO", progress + "");
        boolean estado = false;
        int cont=0;
        if(!(progress<lyrics.get(0).getTiempo())) {
            for (int i = 0; i < lyrics.size() && estado == false; i++) {
                if (lyrics.get(i).getTiempo() > progress) {
                    cont++;
                    Log.d("TIEMPOOOOOOOOO", lyrics.get(i).getTiempo() + "");
                    if (i != 0) {
                        index = i - 1;
                    }
                    v1.setText("");
                    v2.setText("");
                    v3.setText("");
                    estado = true;
                    usuarioCambia = true;
                    termina= false;
                }
            }
            if(cont==0){
                v1.setText("");
                v2.setText("");
                v3.setText("");
                termina=true;
            }
        }
        else{
            index=0;
            v1.setText("");
            v2.setText("");
            v3.setText(lyrics.get(index).getLetra());
            estado = true;
            usuarioCambia = true;
        }
    }


    private void cambiaLyrics(int i) {
        if(termina==false){
        if(index<lyrics.size() &&  lyrics.get(index).getTiempo()<=i){
            v1.setText(v2.getText());
            v2.setText(v3.getText());
            if(usuarioCambia==true) {
                v2.setText(lyrics.get(index).getLetra());
                if(index!=0){
                    v1.setText(lyrics.get(index-1).getLetra());
                }
                usuarioCambia=false;
            }
            index++;
            if(index<lyrics.size()){
                v3.setText(lyrics.get(index).getLetra());
            }
            else{
                v3.setText("");
            }
        }}
    }

    private void procesaString(int resid) {
        String linea = "";

        try
        {
            InputStream fraw =
                    getResources().openRawResource(resid);

            BufferedReader brin =
                    new BufferedReader(new InputStreamReader(fraw));

            while((linea= brin.readLine())!=null) {
                lyrics.add(new Lyric(linea));
            }
            fraw.close();
        }
        catch (Exception ex)
        {
            Log.e("Ficheros", "Error al leer fichero desde recurso raw");;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        AudioManager audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);

        if ((keyCode == KeyEvent.KEYCODE_VOLUME_UP)){
            SeekBar volumeSeekBar = (SeekBar) findViewById(R.id.volumeSeekBar);
            volumeSeekBar.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)+1);

        }else if ((keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)){
            SeekBar volumeSeekBar = (SeekBar) findViewById(R.id.volumeSeekBar);
            volumeSeekBar.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)-1);

        }
        return true; // super.dispatchKeyEvent(event);
    }

    public void ver(String s){
        mediaPlayer.stop();
        lyrics= new ArrayList<>();
        switch (s) {
            case "So Far Away, Avenged Sevenfold":
                procesaString(R.raw.so_far_away_lrc);
                mediaPlayer = MediaPlayer.create(this.getApplicationContext(), R.raw.so_far_away);
                break;
            case "Bohemian Rhapsody, Queen":
                procesaString(R.raw.bohemian_rhapsody_lrc);
                mediaPlayer = MediaPlayer.create(this.getApplicationContext(), R.raw.bohemian_rhapsody);
                break;
            case "Believer, Imagine Dragons":
                procesaString(R.raw.believer_lrc);
                mediaPlayer = MediaPlayer.create(this.getApplicationContext(), R.raw.believer);
                break;
            case "I Dont Want to Miss a Thing, Aerosmith":
                procesaString(R.raw.i_dont_want_to_miss_a_thing_lrc);
                mediaPlayer = MediaPlayer.create(this.getApplicationContext(), R.raw.i_dont_want_to_miss_a_thing);
                break;
            default:
                procesaString(R.raw.dream_on_lrc);
                mediaPlayer = MediaPlayer.create(this.getApplicationContext(), R.raw.dream_on);
                break;
        }
        v1.setText("");
        v2.setText("");
        v4.setText(s);
        v3.setText(lyrics.get(0).getLetra());
        final SeekBar advanceSeekBar = (SeekBar) findViewById(R.id.advanceSeekBar);
        int duration = mediaPlayer.getDuration();
        int progress = mediaPlayer.getCurrentPosition();
        advanceSeekBar.setMax(duration);
        advanceSeekBar.setProgress(progress);
        cambiaLyricsPos(0);
        mediaPlayer.start();
    }
}
