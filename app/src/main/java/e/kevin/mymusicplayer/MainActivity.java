package e.kevin.mymusicplayer;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

import static android.view.MotionEvent.*;
import static e.kevin.mymusicplayer.R.color.blackish;
import static e.kevin.mymusicplayer.R.color.coolGreen;
import static e.kevin.mymusicplayer.R.drawable.*;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    int refreshFlag = 1;
    int changeFlag = 0;
    Button b = null;


    ImageView songPicJava;

    ImageButton playlistJava;
    ImageButton playJava;
    ImageButton stopJava;
    ImageButton increaseVol;
    ImageButton decreaseVol;
    ImageButton muteVol;
    ImageButton nextButton;


    ImageButton forwardJava;
    ImageButton backwardJava;

    MediaPlayer mediaPlayer;
    SeekBar seekbar;
    TextView tv, currentTime, endTime;
    Button btn1, btn2, btn3, btn4, btn5, btn6;
    ScrollView sc;

    LinearLayout lin;

    AudioManager mAudioManager;

    int maxSong = 6;
    int muteFlag = 0;
    String songsList[];
    int currSong;
    String curSong;

    private double startTime = 0;
    private double finalTime = 0;

    private int msStartTime = 0;
    private int msFinalTime = 0;

    int maxVolume = 20;
    int currentVolLevel = 10;
    private Handler myHandler = new Handler();
    ;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        playJava = findViewById(R.id.playBtn);
        playlistJava = findViewById(R.id.playlistBtn);
        stopJava = findViewById(R.id.stopBtn);

        forwardJava = findViewById(R.id.forwardBtn);
        backwardJava = findViewById(R.id.backwardBtn);

        mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolLevel, 0);

        nextButton = findViewById(R.id.nextBtn);
        //   mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        mediaPlayer = MediaPlayer.create(this, R.raw.one);

        finalTime = mediaPlayer.getDuration();


        int songID = getResources().getIdentifier("one", "raw", getPackageName());
        currSong = songID;

        songsList = new String[]{"one", "two", "three", "four", "five", "six"};

        tv = findViewById(R.id.songName);
        tv.setText(R.string.one);
        sc = findViewById(R.id.scroller);

        btn1 = findViewById(R.id.one);
        btn2 = findViewById(R.id.two);
        btn3 = findViewById(R.id.three);
        btn4 = findViewById(R.id.four);
        btn5 = findViewById(R.id.five);
        btn6 = findViewById(R.id.six);

        currentTime = findViewById(R.id.current);
        endTime = findViewById(R.id.remaining);


        songPicJava = findViewById(R.id.songPic);
        ;
        increaseVol = findViewById(R.id.increaseBtn);
        decreaseVol = findViewById(R.id.decreaseBtn);
        muteVol = findViewById(R.id.muteBtn);
        seekbar = findViewById(R.id.seekBar);

        //seekbar.setEnabled(false);

        //seekbar.getProgressDrawable().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
        //seekbar.getThumb().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);


        ScrollView sc = findViewById(R.id.scroller);
        sc.setVisibility(View.GONE);


        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        btn3.setOnClickListener(this);
        btn4.setOnClickListener(this);
        btn5.setOnClickListener(this);
        btn6.setOnClickListener(this);


        backwardJava.setOnClickListener(this);
        forwardJava.setOnClickListener(this);

        playJava.setOnClickListener(this);
        playlistJava.setOnClickListener(this);
        stopJava.setOnClickListener(this);
        increaseVol.setOnClickListener(this);
        decreaseVol.setOnClickListener(this);
        muteVol.setOnClickListener(this);


        lin = findViewById(R.id.songPicLayout);
        lin.setOnClickListener(this);

        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            float progressChangedValue = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChangedValue = progress / 100;

                Log.d("progressChanged", "fk");
                currentTime.setText(String.format("%02d : %02d ",
                        TimeUnit.MILLISECONDS.toMinutes((long) progressChangedValue),
                        TimeUnit.MILLISECONDS.toSeconds((long) progressChangedValue) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                        toMinutes((long) progressChangedValue)))
                );

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.d("starteddd", "fk");

                refreshFlag = 0;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.d("stopped", "finally finished");
                mediaPlayer.seekTo((int) progressChangedValue);
                refreshFlag = 1;
                myHandler.postDelayed(UpdateSongTime, 1000);


            }
        });


        registerForContextMenu(tv);


        endTimeSetter();
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Select The Action");
        menu.add(0, v.getId(), 0, "Play");//groupId, itemId, order, title
        menu.add(0, v.getId(), 0, "Pause");
        menu.add(0, v.getId(), 0, "Stop");
        menu.add(0, v.getId(), 0, "Next");
        menu.add(0, v.getId(), 0, "Previous");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle() == "Play") {
            pausePlay(0);
        } else if (item.getTitle() == "Pause") {
            pausePlay(0);
        } else if (item.getTitle() == "Stop") {
            mediaPlayer.reset();
            pausePlay(2);
            mediaPlayer = MediaPlayer.create(this, currSong);
        } else if (item.getTitle() == "Next") {
            findCurrentSong(1);
        } else if (item.getTitle() == "Previous") {
            findCurrentSong(2);
        } else {
            return false;
        }
        return true;
    }


    @Override
    public void onClick(View v) {

        ImageButton ib;

        final float[] x1 = new float[1];
        final float[] x2 = new float[1];


        switch (v.getId()) {

            case R.id.songPicLayout:
                lin.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {

                        switch (event.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                x1[0] = event.getY();

                            case MotionEvent.ACTION_UP:
                                x2[0] = event.getY();
                                if (Math.abs(x1[0]) < Math.abs(x2[0])) {
                                    findCurrentSong(0);
                                } else if (Math.abs(x1[0]) > Math.abs(x2[0])) {
                                    findCurrentSong(1);
                                }
                                break;
                        }
                        return true;
                    }
                });
                break;
            //View Playlist
            case R.id.playlistBtn:
//               sc.setVisibility(View.VISIBLE);
//                playlistJava.setVisibility(View.GONE);
//                songPicJava.setVisibility(View.GONE);

findCurrentSong(1);

            //play button icon change when pressed along with pause n play
            case R.id.playBtn:
                pausePlay(0);

                break;

            case R.id.increaseBtn:
                if (currentVolLevel < 20) {
                    currentVolLevel++;
                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolLevel, AudioManager.FLAG_SHOW_UI);
                } else
                    Toast.makeText(this, "Maximum Volume", Toast.LENGTH_SHORT).show();
                //findCurrentSong(1);

                break;
            case R.id.decreaseBtn:

                if (currentVolLevel > 0) {
                    currentVolLevel--;
                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolLevel, AudioManager.FLAG_SHOW_UI);
                } else
                    Toast.makeText(this, "Minimum Volume", Toast.LENGTH_SHORT).show();
                //findCurrentSong(2);
                break;

            case R.id.nextBtn:


                break;
            case R.id.muteBtn:
                if (muteFlag == 0) {
                    muteFlag = 1;
                    muteVol.setBackgroundResource(rounded_light);
                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, AudioManager.FLAG_SHOW_UI);
                } else {
                    muteFlag = 0;
                    muteVol.setBackgroundResource(player_btn);
                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolLevel, AudioManager.FLAG_SHOW_UI);
                }
                break;

            case R.id.stopBtn:
                mediaPlayer.reset();
                pausePlay(2);
                mediaPlayer = MediaPlayer.create(this, currSong);
                break;

            case R.id.forwardBtn:
                jumpTime(1);
                break;

            case R.id.backwardBtn:
                jumpTime(0);
                break;

            default:
                b = (Button) v;
                int resID = getResources().getIdentifier("" + b.getId(), "raw", getPackageName());
                String curSong1 = getResources().getResourceEntryName(currSong);


                tv.setText(b.getText());

                Button bb = findViewById(v.getId());

                sc.setVisibility(View.GONE);
                playlistJava.setVisibility(View.VISIBLE);
                songPicJava.setVisibility(View.VISIBLE);

                String songIndex = getResources().getResourceEntryName(resID);
                int songID = getResources().getIdentifier(songIndex, "raw", getPackageName());
                int picID = getResources().getIdentifier(songIndex, "drawable", getPackageName());

                songPicJava.setImageResource(picID);

                currSong = songID;
                mediaPlayer.stop();
                mediaPlayer.reset();
                mediaPlayer = MediaPlayer.create(this, songID);
                mediaPlayer.start();
                pausePlay(1);
                endTimeSetter();
        }

    }

    public void pausePlay(int i) {
        if (i == 0) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                playJava.setImageResource(play);
                playJava.setBackgroundResource(player_btn);
            } else {
                mediaPlayer.start();
                playJava.setImageResource(pause);
                playJava.setBackgroundResource(rounded_light);
            }
        } else if (i == 1) {
            playJava.setImageResource(pause);
            playJava.setBackgroundResource(rounded_light);
        } else if (i == 2) {
            playJava.setImageResource(play);
            playJava.setBackgroundResource(player_btn);
        }


        myHandler.postDelayed(UpdateSongTime, 100);

    }

    public void findCurrentSong(int d) {

        curSong = getResources().getResourceEntryName(currSong);

        for (int k = 0; k < maxSong; k++) {
            if (songsList[k].equals(curSong)) {
                if (d == 1) {
                    curSong = songsList[(k + 1) % maxSong];
                } else {

                    if (k == 0) {
                        curSong = songsList[maxSong - 1];
                    } else {
                        curSong = songsList[k - 1];
                    }
                }
                break;
            }

        }

        playingSongString();
        endTimeSetter();

    }


    public void playingSongString() {
        switch (curSong) {
            case "one":
                tv.setText(R.string.one);
                break;
            case "two":
                tv.setText(R.string.two);
                break;
            case "three":
                tv.setText(R.string.three);
                break;
            case "four":
                tv.setText(R.string.four);
                break;
            case "five":
                tv.setText(R.string.five);
                break;
            case "six":
                tv.setText(R.string.six);
                break;
        }

        int songID1 = getResources().getIdentifier(curSong, "raw", getPackageName());
        int picID1 = getResources().getIdentifier(curSong, "drawable", getPackageName());

        songPicJava.setImageResource(picID1);

        currSong = songID1;

        int flag;
        if (mediaPlayer.isPlaying()) {
            flag = 1;
        } else {
            flag = 0;
        }
        mediaPlayer.stop();
        mediaPlayer.reset();
        mediaPlayer = MediaPlayer.create(this, songID1);

        if (flag == 1) {

            pausePlay(1);
            mediaPlayer.start();

        }
    }

    void picChangge(int d) {
        String curSong1 = getResources().getResourceEntryName(currSong);

        for (int k = 0; k < maxSong; k++) {
            if (songsList[k].equals(curSong1)) {
                if (d == 1) {
                    curSong1 = songsList[(k + 1) % maxSong];
                } else {
                    if (k == 0) {
                        curSong1 = songsList[maxSong - 1];
                    } else {
                        curSong1 = songsList[k - 1];
                    }
                }
                break;
            }

        }

        switch (curSong1) {
            case "one":
                tv.setText(R.string.one);
                break;
            case "two":
                tv.setText(R.string.two);
                break;
            case "three":
                tv.setText(R.string.three);
                break;
            case "four":
                tv.setText(R.string.four);
                break;
            case "five":
                tv.setText(R.string.five);
                break;
            case "six":
                tv.setText(R.string.six);
                break;
        }
        int picID1 = getResources().getIdentifier(curSong1, "drawable", getPackageName());

        songPicJava.setImageResource(picID1);


    }

    void jumpTime(int d) {
        int temp = (int) startTime;
        int TimeGap = 5000;

        if (d == 1) {
            if ((temp + TimeGap) <= finalTime) {
                startTime = startTime + TimeGap;
                mediaPlayer.seekTo((int) startTime);
                //Toast.makeText(getApplicationContext(), "You have Jumped forward 5 seconds", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Cannot jump forward 5 seconds", Toast.LENGTH_SHORT).show();
            }
        } else {
            if ((temp - TimeGap) >= 0) {
                startTime = startTime - TimeGap;
                mediaPlayer.seekTo((int) startTime);
                //Toast.makeText(getApplicationContext(), "You have Jumped backward 5 seconds", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Cannot jump backward 5 seconds", Toast.LENGTH_SHORT).show();
            }
        }

    }

    public void endTimeSetter() {

        finalTime = mediaPlayer.getDuration();

        msFinalTime = (int) (finalTime * 100);
        seekbar.setMax(msFinalTime);

        endTime.setText(String.format("%02d : %02d",
                TimeUnit.MILLISECONDS.toMinutes((long) finalTime),
                TimeUnit.MILLISECONDS.toSeconds((long) finalTime) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long)
                                finalTime)))
        );
    }


    private Runnable UpdateSongTime = new Runnable() {
        public void run() {
            startTime = mediaPlayer.getCurrentPosition();
            currentTime.setText(String.format("%02d : %02d ",
                    TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                    TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                    toMinutes((long) startTime)))
            );

            msStartTime = (int) (startTime * 100);
            seekbar.setProgress(msStartTime);


            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                public void onCompletion(MediaPlayer mp) {

                    findCurrentSong(1);
                    mediaPlayer.start();
                }
            });

            if (refreshFlag == 1) {
                myHandler.postDelayed(this, 1000);
            }
        }
    };


}

