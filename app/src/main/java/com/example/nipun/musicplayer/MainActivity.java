package com.example.nipun.musicplayer;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private RelativeLayout parentRelativeLayout;
    private SpeechRecognizer speechRecognizer;
    private Intent speechRecognizerIntent;
    private String keeper = "";

    private ImageView pausePlayBtn, nextBtn, previousBtn;
    private TextView songNameTxt;

    private ImageView imageView;
    private RelativeLayout lowerRelativeLayout;
    private Button voiceEnabledBtn;
    private String mode = "ON";

    private MediaPlayer mediaPlayer;
    private int position;
    private ArrayList<File> mysongs;
    private String mySongName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkVoiceCommandPermission();

        pausePlayBtn = findViewById(R.id.play_pause_btn);
        nextBtn = findViewById(R.id.next_btn);
        previousBtn = findViewById(R.id.previous_btn);
        imageView = findViewById(R.id.logo);


        lowerRelativeLayout= findViewById(R.id.lower);
        voiceEnabledBtn = findViewById(R.id.voice_enabled_btn);
        songNameTxt = findViewById(R.id.songName);

        parentRelativeLayout = findViewById(R.id.parentRelativeLayout);
        speechRecognizer = speechRecognizer.createSpeechRecognizer(MainActivity.this);
        speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, Locale.getDefault());

        validateReceiveValuesAndStartPlaying();
        imageView.setBackgroundResource(R.drawable.logo);

        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {

            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float rmsdB) {

            }

            @Override
            public void onBufferReceived(byte[] buffer) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int error) {

            }

            @Override
            public void onResults(Bundle results) {

                ArrayList<String> matchesFound = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (matchesFound != null)
                {
                    if (mode.equals("ON"))
                    {
                        keeper = matchesFound.get(0);

                        if (keeper.equals("pause the song"))
                        {
                            playPauseSong();
                            Toast.makeText(MainActivity.this, "Command = "+ keeper,Toast.LENGTH_LONG).show();
                        }
                        else if (keeper.equals("play the song"))
                        {
                            playPauseSong();
                            Toast.makeText(MainActivity.this, "Command = "+ keeper,Toast.LENGTH_LONG).show();

                        }
                        else if (keeper.equals("play the next song"))
                        {
                            playNextSong();
                            Toast.makeText(MainActivity.this, "Command = "+ keeper,Toast.LENGTH_LONG).show();

                        }
                        else if (keeper.equals("play the previous song"))
                        {
                            playPreviousSong();
                            Toast.makeText(MainActivity.this, "Command = "+ keeper,Toast.LENGTH_LONG).show();

                        }
                        else
                        {
                            Toast.makeText(MainActivity.this, "Invalid Command = "+ keeper,Toast.LENGTH_LONG).show();

                        }

                    }
//                    Toast.makeText(MainActivity.this, "Result = "+ keeper,Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onPartialResults(Bundle partialResults) {

            }

            @Override
            public void onEvent(int eventType, Bundle params) {

            }
        });

        parentRelativeLayout.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                switch (event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        speechRecognizer.startListening(speechRecognizerIntent);
                        keeper = "";
                        break;

                    case MotionEvent.ACTION_UP:
                        speechRecognizer.stopListening();
                        break;
                }

                return false;
            }
        });


        voiceEnabledBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (mode.equals("ON"))
                {
                    mode = "OFF";
                    voiceEnabledBtn.setText("Voide Mode Enabled - OFF");
                    lowerRelativeLayout.setVisibility(View.VISIBLE);
                }
                else
                {
                    mode = "ON";
                    voiceEnabledBtn.setText("Voide Mode Enabled - ON");
                    lowerRelativeLayout.setVisibility(View.GONE);

                }

            }
        });


        pausePlayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                playPauseSong();

            }
        });

        previousBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (mediaPlayer.getCurrentPosition()>0)
                {
                    playPreviousSong();
                }
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (mediaPlayer.getCurrentPosition()>0)
                {
                    playNextSong();
                }
            }
        });



    }


    private void validateReceiveValuesAndStartPlaying()
    {
        if (mediaPlayer != null)
        {
            mediaPlayer.stop();
            mediaPlayer.release();
        }

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        mysongs = (ArrayList) bundle.getParcelableArrayList("song");
        mySongName = mysongs.get(position).getName();
        String songName = intent.getStringExtra("name");

        songNameTxt.setText(songName);
        songNameTxt.setSelected(true);

        position = bundle.getInt("position", 0);
        Uri uri = Uri.parse(mysongs.get(position).toString());

        mediaPlayer = MediaPlayer.create(MainActivity.this, uri);
        mediaPlayer.start();

    }

    private void checkVoiceCommandPermission()
    {

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if (!(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED))
            {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getPackageName()));
                startActivity(intent);
                finish();

            }

        }
    }


    private void playPauseSong()
    {
        imageView.setBackgroundResource(R.drawable.four);

        if (mediaPlayer.isPlaying())
        {
            pausePlayBtn.setImageResource(R.drawable.play);
            mediaPlayer.pause();
        }
        else
        {
            pausePlayBtn.setImageResource(R.drawable.pause);
            mediaPlayer.start();

            imageView.setBackgroundResource(R.drawable.five);
        }
    }


    private void playNextSong()
    {
        mediaPlayer.pause();
        mediaPlayer.stop();
        mediaPlayer.release();

        position = ((position+1)%mysongs.size());

        Uri uri = Uri.parse(mysongs.get(position).toString());

        mediaPlayer = MediaPlayer.create(MainActivity.this, uri);

        mySongName = mysongs.get(position).toString();
        songNameTxt.setText(mySongName);
        mediaPlayer.start();

        imageView.setBackgroundResource(R.drawable.three);

        if (mediaPlayer.isPlaying())
        {
            pausePlayBtn.setImageResource(R.drawable.pause);

        }
        else
        {
            pausePlayBtn.setImageResource(R.drawable.play);


            imageView.setBackgroundResource(R.drawable.five);
        }


    }


    private void playPreviousSong()
    {
        mediaPlayer.pause();
        mediaPlayer.stop();
        mediaPlayer.release();

        position = ((position-1)<0 ? (mysongs.size()-1) : (position-1));

        Uri uri = Uri.parse(mysongs.get(position).toString());

        mediaPlayer = MediaPlayer.create(MainActivity.this, uri);

        mySongName = mysongs.get(position).toString();
        songNameTxt.setText(mySongName);
        mediaPlayer.start();

        imageView.setBackgroundResource(R.drawable.two);

        if (mediaPlayer.isPlaying())
        {
            pausePlayBtn.setImageResource(R.drawable.pause);

        }
        else
        {
            pausePlayBtn.setImageResource(R.drawable.play);


            imageView.setBackgroundResource(R.drawable.five);
        }


    }
}
