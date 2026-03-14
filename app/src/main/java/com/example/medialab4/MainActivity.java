package com.example.medialab4;

import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;

public class MainActivity extends AppCompatActivity {

    private PlayerView playerView;
    private ExoPlayer player;

    private Button btnSelectFile, btnLoadUrl, btnPlay, btnPause, btnStop;
    private EditText urlInput;
    private RadioButton radioAudio, radioVideo;

    private Uri currentUri;

    private final ActivityResultLauncher<String[]> filePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.OpenDocument(), uri -> {
                if (uri != null) {
                    currentUri = uri;
                    Toast.makeText(this, "File selected: " + uri.toString(), Toast.LENGTH_SHORT).show();
                    playMedia(uri);
                } else {
                    Toast.makeText(this, "File not selected", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        playerView = findViewById(R.id.playerView);
        btnSelectFile = findViewById(R.id.btnSelectFile);
        btnLoadUrl = findViewById(R.id.btnLoadUrl);
        btnPlay = findViewById(R.id.btnPlay);
        btnPause = findViewById(R.id.btnPause);
        btnStop = findViewById(R.id.btnStop);
        urlInput = findViewById(R.id.urlInput);
        radioAudio = findViewById(R.id.radioAudio);
        radioVideo = findViewById(R.id.radioVideo);

        initPlayer();

        btnSelectFile.setOnClickListener(v -> openFilePicker());

        btnLoadUrl.setOnClickListener(v -> {
            String url = urlInput.getText().toString().trim();

            if (url.isEmpty()) {
                Toast.makeText(this, "Enter URL", Toast.LENGTH_SHORT).show();
                return;
            }

            currentUri = Uri.parse(url);
            Toast.makeText(this, "Loading URL...", Toast.LENGTH_SHORT).show();
            playMedia(currentUri);
        });

        btnPlay.setOnClickListener(v -> {
            if (player != null) {
                player.play();
            }
        });

        btnPause.setOnClickListener(v -> {
            if (player != null) {
                player.pause();
            }
        });

        btnStop.setOnClickListener(v -> {
            if (player != null) {
                player.pause();
                player.seekTo(0);
            }
        });
    }

    private void initPlayer() {
        if (player == null) {
            player = new ExoPlayer.Builder(this).build();
            playerView.setPlayer(player);
        }
    }

    private void openFilePicker() {
        if (radioAudio.isChecked()) {
            filePickerLauncher.launch(new String[]{"audio/*"});
        } else {
            filePickerLauncher.launch(new String[]{"video/*"});
        }
    }

    private void playMedia(Uri uri) {
        if (player == null) {
            initPlayer();
        }

        try {
            MediaItem mediaItem = MediaItem.fromUri(uri);
            player.setMediaItem(mediaItem);
            player.prepare();
            player.play();

            Toast.makeText(this, "Playback started", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Playback error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.release();
            player = null;
        }
    }
}