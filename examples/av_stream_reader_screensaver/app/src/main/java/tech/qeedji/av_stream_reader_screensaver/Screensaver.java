package tech.qeedji.av_stream_reader_screensaver;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.service.dreams.DreamService;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.media3.common.MediaItem;
import androidx.media3.common.PlaybackException;
import androidx.media3.common.Player;
import androidx.media3.common.util.TimestampAdjuster;
import androidx.media3.datasource.DataSource;
import androidx.media3.datasource.UdpDataSource;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory;
import androidx.media3.exoplayer.source.MediaSource;
import androidx.media3.exoplayer.trackselection.AdaptiveTrackSelection;
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector;
import androidx.media3.exoplayer.trackselection.TrackSelector;
import androidx.media3.extractor.ExtractorsFactory;
import androidx.media3.extractor.ts.DefaultTsPayloadReaderFactory;
import androidx.media3.extractor.ts.TsExtractor;
import androidx.media3.ui.PlayerView;

public class Screensaver extends DreamService {

    public static final String TAG = "av_stream_reader_screensaver";

    public static final String PREFERENCES_FILE = "tech.qeedji.av_stream_reader_screensaver.prefs";
    public static final String DEFAULT_URL = "";

    private Context mContext = null;
    String mUrl = DEFAULT_URL;
    private ExoPlayer mExoPlayer = null;
    private boolean mIsAttached = false;
    private boolean mIsStart = false;
    private boolean mIsPlaying = false;
    private Player.Listener mPlayerListener = null;
    private Handler mHandler = new Handler();

    @Override
    public void onCreate() {
        mContext = getApplicationContext();
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        mIsAttached = true;

        Log.d(TAG, "onAttachedToWindow");

        // Exit dream upon user touch
        setInteractive(false);
        // Hide system UI
        setFullscreen(true);
        // Set the dream layout
        setContentView(R.layout.screen_saver);

        // Get url
        mUrl = getUrl();
        if (mUrl.startsWith("udp://")) {
            // Start Udp
            startExoPlayer(mUrl);
        } else {
            error("unsupported url");
        }
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        mIsAttached = false;

        Log.d(TAG, "onDetachedFromWindow");

        // Stop Udp
        stopExoPlayer();
    }

    public void restartExoPlayer() {
        if (mIsAttached == false) {
            Log.d(TAG, "NO restartExoPlayer");
            return;
        }

        Log.d(TAG, "restartExoPlayer");

        // Stop Udp
        stopExoPlayer();
        // Start Udp
        startExoPlayer(mUrl);
    }

    public void error(String s) {
        Log.e(TAG, s + ": " + mUrl);
        TextView textView = findViewById(R.id.textView);
        textView.setText(s + "\n" + mUrl);
        textView.setVisibility(View.VISIBLE);
    }

    public void errorContent() {
        if (mIsAttached == false) {
            Log.d(TAG, "NO errorContent");
            return;
        }

        error("Content temporarily unavailable");
        // Stop Udp
        stopExoPlayer();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Start Udp
                startExoPlayer(mUrl);
            }
        }, 60000);
    }

    private String getUrl() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
        return sharedPreferences.getString("url", DEFAULT_URL);
    }

    @SuppressLint("UnsafeOptInUsageError")
    private void startExoPlayer(String url) {
        if (mIsAttached == false) {
            Log.d(TAG, "NO startExoPlayer");
            return;
        }

        if (mIsStart == true) {
            return;
        }
        mIsStart = true;

        Log.d(TAG, "startExoPlayer");

        TextView textView = findViewById(R.id.textView);
        textView.setVisibility(View.INVISIBLE);
        textView.setText("");

        Uri videoUri = Uri.parse(url);

        // Create default TrackSelector
        TrackSelector trackSelector = new DefaultTrackSelector(mContext,
                new AdaptiveTrackSelection.Factory());

        // Create player
        mExoPlayer = new ExoPlayer.Builder(mContext).setTrackSelector(trackSelector).build();

        // Get player view
        PlayerView playerView = findViewById(R.id.player_view);
        // Set fullscreen
        playerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_IMMERSIVE | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        // Set player in playerView
        playerView.setPlayer(mExoPlayer);
        playerView.requestFocus();

        // Add listener
        mPlayerListener = new Player.Listener() {
            @Override
            public void onEvents(Player player, Player.Events events) {
                Player.Listener.super.onEvents(player, events);
            }

            @Override
            public void onIsPlayingChanged(boolean isPlaying) {
                Log.d(Screensaver.TAG, "onIsPlayingChanged(" + isPlaying + ")");
                if (isPlaying == false) {
                    if (mIsPlaying == true) {
                        restartExoPlayer();
                    } else {
                        errorContent();
                    }
                }
                mIsPlaying = isPlaying;
            }

            @Override
            public void onPlayerError(PlaybackException error) {
                Log.e(Screensaver.TAG, "onPlayerError(" + error + ")");
                errorContent();
            }

            @Override
            public void onPlayerErrorChanged(@Nullable PlaybackException error) {
                Log.e(Screensaver.TAG, "onPlayerErrorChanged(" + error + ")");
                errorContent();
            }
        };
        mExoPlayer.addListener(mPlayerListener);

        // Create default UDP Datasource
        ExtractorsFactory tsExtractorFactory = () -> new TsExtractor[]{
                new TsExtractor(TsExtractor.MODE_MULTI_PMT,
                        new TimestampAdjuster(0),
                        new DefaultTsPayloadReaderFactory())};
        DataSource.Factory factory = () -> new UdpDataSource();
        MediaSource.Factory mediaSourceFactory =
                new DefaultMediaSourceFactory(mContext, tsExtractorFactory)
                        .setDataSourceFactory(factory);
        MediaItem mediaItem = new MediaItem.Builder().setUri(videoUri).build();
        MediaSource mediaSource = mediaSourceFactory.createMediaSource(mediaItem);

        mExoPlayer.addMediaSource(mediaSource);
        mExoPlayer.prepare();

        mIsPlaying = false;

        // Start play automatically when player is ready
        mExoPlayer.setPlayWhenReady(true);

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mIsPlaying == false) {
                    errorContent();
                }
            }
        }, 10000);
        Log.i(TAG, "Udp " + url + " playing");
    }

    private void stopExoPlayer() {
        if (mIsStart == false) {
            return;
        }
        mIsStart = false;

        Log.d(TAG, "stopExoPlayer");

        // Stop player
        mExoPlayer.stop();
        if (mPlayerListener != null) {
            mExoPlayer.removeListener(mPlayerListener);
        }
    }
}

