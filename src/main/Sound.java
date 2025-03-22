package main;

import javax.sound.sampled.*;
import java.net.URL;

public class Sound {
    Clip musicClip;
    URL[] url = new URL[10];

    public Sound(){
        url[0] = getClass().getResource("/background music.wav");
        url[1] = getClass().getResource("/delete line.wav");
        url[2] = getClass().getResource("/gameover.wav");
        url[3] = getClass().getResource("/rotation.wav");
        url[4] = getClass().getResource("/touch floor.wav");

    }
    public void play(int i, boolean isMus){
        try{
            AudioInputStream ais = AudioSystem.getAudioInputStream(url[i]);
            Clip clip = AudioSystem.getClip();
            if (isMus){
                musicClip = clip;
            }
            clip.open(ais);
            clip.addLineListener(new LineListener() {
                @Override
                public void update(LineEvent event) {
                    if (event.getType() == LineEvent.Type.STOP){
                        clip.close();
                    }
                }
            });
            clip.start();
            ais.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    public void loop(){
        FloatControl gainControl = (FloatControl) musicClip.getControl(FloatControl.Type.MASTER_GAIN);
        gainControl.setValue(-10);
        musicClip.loop(Clip.LOOP_CONTINUOUSLY);
    }
    public void stop(){
        musicClip.stop();
        musicClip.close();
    }
}
