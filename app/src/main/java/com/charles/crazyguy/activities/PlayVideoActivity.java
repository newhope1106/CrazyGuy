package com.charles.crazyguy.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.charles.crazyguy.R;

import com.charles.crazyguy.adapter.VideoListAdapter;
import com.charles.crazyguy.dto.VideoItem;
import com.charles.crazyguy.util.CommonUtil;
import com.charles.crazyguy.widget.VideoView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PlayVideoActivity extends BaseActivity {
    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
        System.loadLibrary("avutil");
        System.loadLibrary("avcodec");
        System.loadLibrary("avfilter");
        System.loadLibrary("swscale");
        System.loadLibrary("avformat");
        System.loadLibrary("native-ffmpeg");
        System.loadLibrary("swresample");
    }

    //读写权限
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    //请求状态码
    private static int REQUEST_PERMISSION_CODE = 1;

    private VideoView mVideo;
    private ListView mVideoListView;
    private VideoListAdapter mVideoAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_play_video);
        mVideo = (VideoView) findViewById(R.id.video);
        mVideoListView = findViewById(R.id.video_list);

        // Example of a call to a native method
        findViewById(R.id.play_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playVideo();
            }
        });

        findViewById(R.id.stop_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopVideo();
            }
        });

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_PERMISSION_CODE);
        }

        initAdapter();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CODE) {
            for (int i = 0; i < permissions.length; i++) {
                Toast.makeText(this, "申请的权限为：" + permissions[i] + ",申请结果：" + grantResults[i], Toast.LENGTH_SHORT).show();
            }
        }
        initAdapter();
    }

    private void initAdapter(){
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            if(mVideoAdapter == null) {
                mVideoAdapter = new VideoListAdapter(this);
                mVideoListView.setAdapter(mVideoAdapter);

                mVideoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if(mVideoAdapter != null) {
                            VideoItem videoItem = mVideoAdapter.getVideoItem(position);
                            if(videoItem != null) {
                                playVideo(videoItem.videoPath);
                            }
                        }
                    }
                });
            } else {
                mVideoAdapter.notifyDataSetChanged();
            }

            initVideoData();
        }
    }

    private void initVideoData() {
        List<VideoItem> videoItemList = new ArrayList<>();
        String[] projection = new String[]{MediaStore.Video.Media.DATA, MediaStore.Video.Media
                .DURATION, MediaStore.Video.Media.TITLE};
        Cursor cursor = getContentResolver().query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection, null,
                null, null);
        try {
            if(cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                do{
                    String path = cursor
                            .getString(cursor
                                    .getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
                    long duration = cursor
                            .getInt(cursor
                                    .getColumnIndexOrThrow(MediaStore.Video.Media.DURATION));
                    String title = cursor
                            .getString(cursor
                                    .getColumnIndexOrThrow(MediaStore.Video.Media.TITLE));

                    VideoItem videoItem = new VideoItem();
                    videoItem.duration = duration;
                    videoItem.title = title;
                    videoItem.videoPath = path;
                    videoItem.formatDuration = CommonUtil.formatVideoTime(duration);

                    videoItemList.add(videoItem);
                }while (cursor.moveToNext());
            }
        } finally {
            if(cursor != null) {
                cursor.close();
            }
        }

        mVideoAdapter.setVideoItems(videoItemList);
        mVideoAdapter.notifyDataSetChanged();
    }

    public void playVideo() {
        String dir = Environment.getExternalStorageDirectory().getAbsolutePath();
        String path = dir + File.separator + "MuFan.mp4";

        String output = new File(Environment.getExternalStorageDirectory(), "Output.pcm").getAbsolutePath();

        Toast.makeText(this, "path = " + path, Toast.LENGTH_SHORT).show();
        if (new File(path).exists()) {
            playVideo(path, mVideo.getHolder().getSurface(), output);
        }
    }


    public void playVideo(String path) {
        String output = new File(Environment.getExternalStorageDirectory(), "Output.pcm").getAbsolutePath();

        Toast.makeText(this, "path = " + path, Toast.LENGTH_SHORT).show();
        if (new File(path).exists()) {
            playVideo(path, mVideo.getHolder().getSurface(), output);
        }
    }


    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();

    public native void playVideo(String path, Object view, String soundOutput);

    public native void stopVideo();

    public AudioTrack createAudioTrack(int sampleRateInHz, int nb_channels) {
        //固定格式的音频码流
        int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
        //声道布局
        int channelConfig;
        if (nb_channels == 1) {
            channelConfig = android.media.AudioFormat.CHANNEL_OUT_MONO;
        } else if (nb_channels == 2) {
            channelConfig = android.media.AudioFormat.CHANNEL_OUT_STEREO;
        } else {
            channelConfig = android.media.AudioFormat.CHANNEL_OUT_STEREO;
        }

        int bufferSizeInBytes = AudioTrack.getMinBufferSize(sampleRateInHz, channelConfig, audioFormat);

        android.util.Log.e("xxxx", "bufferSizeInBytes = " + bufferSizeInBytes + ", sampleRateInHz =" + sampleRateInHz);
        AudioTrack audioTrack = new AudioTrack(
                AudioManager.STREAM_MUSIC,
                sampleRateInHz, channelConfig,
                audioFormat,
                bufferSizeInBytes, AudioTrack.MODE_STREAM);
        //播放
        //audioTrack.play();
        //写入PCM
        //audioTrack.write(audioData, offsetInBytes, sizeInBytes);
        return audioTrack;
    }
}