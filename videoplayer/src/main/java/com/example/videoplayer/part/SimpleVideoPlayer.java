package com.example.videoplayer.part;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.videoplayer.R;

import java.io.IOException;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.Vitamio;

/**
 * Created by Administrator on 2017/1/1.
 */
/**
 * 一个自定义的VideoPlayer，使用MdeiaPlayer+SurfaceView来实现视频的播放
 * MediaPlayer来做视频播放的控制，SurfaceView来显示视频
 * 视图方面（initView初始化）简单实现：一个播放/暂停按钮，一个进度条，一个全屏按钮，一个SurfaceView
 * <p>
 * 结构：
 * 提供setVideoPath方法：设置数据源
 * 提供OnResume方法(在activity的onResume调用)：初始化MediaPlayer，准备MediaPlayer
 * 提供OnPause方法(在activity的onPause调用)：释放mediaplayer，暂停mediaplayer
 */
public class SimpleVideoPlayer extends FrameLayout {
    //进度条的控制（长度，进度）
    private static final int PROGRESS_MAX = 1000;
    private String videoPath;  //视频播放Url
    private MediaPlayer mediaPlayer;
    private boolean isPrepared; //是否准备好
    private boolean isPlaying;  // 是否正在播放

    //试图相关
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private ImageView ivpreview; //预览图
    private ImageButton btnTogglt; //播放暂停
    private ProgressBar progressBar; //进度条

    public SimpleVideoPlayer(Context context) {
        this(context,null);
    }

    public SimpleVideoPlayer(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public SimpleVideoPlayer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    //做视图相关的初始化
    public void init(){
        //vitamio初始化
        Vitamio.isInitialized(getContext());
        //填充布局
        LayoutInflater.from(getContext()).inflate(R.layout.view_simple_video_player,this,true);
        //初始化SurfaceView
        initSurfaceView();
        //初始化视频播放控制视图
        initControllerViews();
        }
    //设置数据源
    public void setVideoPath(String videoPath){
        this.videoPath = videoPath;
    }
    //提供onResume方法（在Activity的onResume调用）
    public void onResume(){
        //初始化MediaPlayer
        initMediaPlayer();
        //准备mediaplayer
        prepareMediaplayer();
    }
    //提供OnPause方法（在activity里的onPause里调用）
    public void onPause(){
        //暂停mediaplayer
        pauseMediaPlayer();
        //释放mediaplayer
        releaseMediaPlayer();
    }
    //初始化sufaceview
    private void initSurfaceView(){
        surfaceView = (SurfaceView) findViewById(R.id.surfaceview);
        surfaceHolder = surfaceView.getHolder();
        //注意，vitamio使用sufaceview要设置pixelFormat，否则会花屏
        surfaceHolder.setFormat(PixelFormat.RGBA_8888);
    }
    //初始化视频播放控制视图
    private void initControllerViews(){
        //预览图
        ivpreview = (ImageView) findViewById(R.id.ivPreview);
        //播放，暂停
        btnTogglt = (ImageButton) findViewById(R.id.btnToggle);
        btnTogglt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlayer.isPlaying()){
                    //暂停播放
                    pauseMediaPlayer();
                }else if (isPrepared){
                    //开始播放
                    startMediaPlayer();
                }else{
                    Toast.makeText(getContext(), "Can't play now", Toast.LENGTH_SHORT).show();
                }
            }
        });
        //设置进度条
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setMax(PROGRESS_MAX);
        //全屏播放按钮
        ImageButton btnFullScreen = (ImageButton) findViewById(R.id.btnFullScreen);
        btnFullScreen.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: 2017/1/2 跳转至全屏页面
                Toast.makeText(getContext(), "全屏页面待实现", Toast.LENGTH_SHORT).show();
            }
        });
    }
    //初始化MediaPlayer，设置一系列监听
    private void initMediaPlayer(){
        mediaPlayer = new MediaPlayer(getContext());
        mediaPlayer.setDisplay(surfaceHolder);
        //准备的监听
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                isPrepared=true;
                startMediaPlayer();
            }
        });
        //audio处理。info监听
        mediaPlayer.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra) {
                if (what==MediaPlayer.MEDIA_INFO_FILE_OPEN_OK){
                    mediaPlayer.audioInitedOk(mediaPlayer.audioTrackInit());
                    return true;
                }
                return false;
            }
        });
        //视频大小改变监听
        mediaPlayer.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
            @Override
            public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
                int layoutWidth = surfaceView.getWidth();
                int layoutHeight = layoutWidth*height/width;
                //更新Surfaceview上的size
                ViewGroup.LayoutParams params =  surfaceView.getLayoutParams();
                params.width = layoutWidth;
                params.height = layoutHeight;
                surfaceView.setLayoutParams(params);
            }
        });
    }
    //准备MediaPlayer
    private void prepareMediaplayer(){
        try {
            //重置MediaPlayer
            mediaPlayer.reset();
            mediaPlayer.setDataSource(videoPath);
            //设置循环播放
            mediaPlayer.setLooping(true);
            mediaPlayer.prepareAsync();
            ivpreview.setVisibility(View.VISIBLE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //开始播放
    private void startMediaPlayer(){
        ivpreview.setVisibility(View.INVISIBLE);
        btnTogglt.setImageResource(R.drawable.ic_pause);
        mediaPlayer.start();
        isPlaying=true;
        handler.sendEmptyMessage(0);
    }
    //暂停播放
    private  void pauseMediaPlayer(){
        if (mediaPlayer.isPlaying()){
            mediaPlayer.pause();
        }
        isPlaying=false;
        btnTogglt.setImageResource(R.drawable.ic_play_arrow);
        //通知progressbar不更新
        handler.removeMessages(0);
    }
    //用handle更新播放进度条
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (isPlaying){
                //每0.2秒更新一次播放进度
                int progress = (int) (mediaPlayer.getCurrentPosition()*PROGRESS_MAX/mediaPlayer.getDuration());
                progressBar.setProgress(progress);
                //发送一个空的延迟消息，不停地调用本身，执行内部方法，实现自动更新进度条
                handler.sendEmptyMessageDelayed(0,200);
            }
        }
    };
    //释放mediaplayer
    private void releaseMediaPlayer(){
        mediaPlayer.release();
        mediaPlayer = null;
        isPrepared = false;
        progressBar.setProgress(0);
    }
}
