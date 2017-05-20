package com.example.lusen.videoplayer;

import android.content.Intent;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class VideoActivity extends AppCompatActivity implements View.OnClickListener, SurfaceHolder.Callback {

    private SurfaceView surface_view;                           /* 播放视频载体 */
    private TextView status;                                     /* 显示播放状态 */
    private SeekBar seekBar;                                     /* 显示进度条 */
    private TextView allTimeView;                               /* 显示总时间 */
    private TextView playTimeView;                              /* 显示当前时间 */
    private String playTime;                                    /* 当前时间 */
    private Thread thread;
    private String allTime;                                     /* 总时间 */
    private Button play;                                        /* 播放按钮 */
    private Button pause;                                       /* 咱提供按钮 */
    private Button reset;                                       /* 重放按钮 */
    private Button stop;                                        /* 停止按钮 */
    String urls = "http://mvideo.spriteapp.cn/video/2017/0518/591d47758e2d4_wpc.mp4";

    private MediaPlayer mediaPlayer;                            /* 播放器 */
    private SurfaceHolder surface_holder;                       /* Surface 控制器 */

    private Handler handler1=new Handler(){
        @Override
        public void handleMessage(Message msg){
            if(msg.what == 1){
                Log.d("子线程返回的",msg.getData().getLong("info")+"");
                playTime = showData(msg.getData().getLong("info"));
                seekBar.setProgress((int) msg.getData().getLong("info"));
            }
        }
    };
    private boolean isStartPlaying;     /* 是否开始了播放 */



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        initViews();
        initData();
    }

    /**
     * 初始化成员变量中的组件变量
     */
    private void initViews() {
        /* 通过 findViewById 获取相关方法 */
        surface_view = (SurfaceView) findViewById(R.id.surface_view);
        status = (TextView) findViewById(R.id.status);
        allTimeView = (TextView) findViewById(R.id.all_time);
        playTimeView = (TextView) findViewById(R.id.play_time);
        play = (Button) findViewById(R.id.play);
        seekBar = (SeekBar) findViewById(R.id.progress);
        pause = (Button) findViewById(R.id.pause);
        reset = (Button) findViewById(R.id.reset);
        stop = (Button) findViewById(R.id.stop);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        Intent intent = getIntent();
        urls = intent.getStringExtra("urlvedio");
        /* 使窗口支持透明度, 把当前 Activity 窗口设置成透明, 设置了该选项就可以使用 setAlpha 等函数设置窗口透明度 */
        getWindow().setFormat(PixelFormat.TRANSPARENT);

        this.thread = new Thread() {
            @Override
            public void run() {
                super.run();
                // 增加对异常的捕获，防止在判断mediaPlayer.isPlaying的时候，报IllegalStateException异常
                try {
                   /*
                    * mediaPlayer不为空且处于正在播放状态时，使进度条滚动。
                    * 通过指定类名的方式判断mediaPlayer防止状态发生不一致
                    */

                    while (isStartPlaying) {
                        seekBar.setProgress(mediaPlayer.getCurrentPosition());
                        Log.d("currenttt",mediaPlayer.getCurrentPosition()+"");

                        Message msg= Message.obtain();
                        msg.what=1;
                        Bundle bundle = new Bundle();
                        bundle.putLong("info", mediaPlayer.getCurrentPosition());
                        msg.setData(bundle);
                        handler1.sendMessage(msg);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }
            /**
             * 初始化相关数据变量
             */
            private void initData() {

        /* 获取并设置 SurfaceHolder 对象 */
                surface_holder = surface_view.getHolder();                      /* 根据 SurfaceView 组件, 获取 SurfaceHolder 对象 */
                surface_holder.addCallback(this);                               /* 为 SurfaceHolder 设置回调函数, 即 SurfaceHolder.Callback 子类对象 */
                surface_holder.setFixedSize(160, 128);                          /* 设置视频大小比例 */
                surface_holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);/* 设置视频类型 */
                seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                        if (i >= 0) {
                            if (b) {
                                mediaPlayer.seekTo(i);
                            }

                           playTimeView.setText(showData(i));
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        isStartPlaying = false;
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        isStartPlaying = true;
                    }
                });

            }

            /**
             * 设置点击事件
             *
             * @param view
             */
            public void onClick(View view) {
                int id = view.getId();

                switch (id) {
                    case R.id.play:
            /* 播放该 url 代表的网络视频 */
                        playVideo(urls);
                        break;

                    case R.id.pause:
                        if (mediaPlayer != null) {
                            mediaPlayer.pause();
                            status.setText("暂停");
                        }
                        break;

                    case R.id.reset:
                        if (mediaPlayer != null) {
                            mediaPlayer.seekTo(0);
                            mediaPlayer.start();
                            Log.d("playtime", allTime);
                            allTimeView.setText(allTime);
                            status.setText("播放中");
                        }
                        break;

                    case R.id.stop:
                        if (mediaPlayer != null) {
                            mediaPlayer.stop();
                            mediaPlayer.release();
                            isStartPlaying = false;
                            status.setText("停止");
                        }
                        break;

                    default:
                        break;
                }
            }

            /**
             * 播放网络视频
             * a. 创建并配置 MediaPlayer 对象 (音量, SurfaceHolder)
             * b. 为 MediaPlayer 设置错误监听器, 缓冲进度监听器, 播放完毕监听器, 准备完毕监听器
             * c. 未 MediaPlayer 设置数据源
             * d. 调用 prepare() 进入 Prapared 状态
             * e. 调用 start() 进入 Started 状态
             *
             * @param dataSource 播放视频的网络地址
             */
            private void playVideo(final String dataSource) {

        /* 点击播放有两种情况
         * a. 第一次点击 : 需要初始化 MediaPlayer 对象, 设置监听器
         * b. 第二次点击 : 只需要 调用 mediaPlayer 的 start() 方法
         * 两种情况通过 isStartPlaying 点击时间判断 */

                if (isStartPlaying) {                             /* 如果已经开始了播放, 就直接开始播放 */
                    mediaPlayer.start();
                } else {                                          /* 如果是第一次开始播放, 需要初始化 MediaPlayer 设置监听器等操作 */
                    mediaPlayer = new MediaPlayer();            /* 创建 MediaPlayer 对象 */
                    mediaPlayer.setAudioStreamType(2);          /* 设置播放音量 */
                    mediaPlayer.setDisplay(surface_holder);     /* 设置播放载体 */

            /* 设置 MediaPlayer 错误监听器, 如果出现错误就会回调该方法打印错误代码 */
                    mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                        @Override
                        public boolean onError(MediaPlayer arg0, int what, int extra) {
                            System.out.println("MediaPlayer 出现错误 what : " + what + " , extra : " + extra);
                            return false;
                        }
                    });

            /* 设置缓冲进度更新监听器 */
                    mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
                        @Override
                        public void onBufferingUpdate(MediaPlayer arg0, int percent) {
                    /* 打印缓冲的百分比, 如果缓冲 */
                            System.out.println("缓冲了的百分比 : " + percent + " %");
                        }
                    });

            /* 设置播放完毕监听器 */
                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer arg0) {
                            System.out.println("播放完毕了");
                            status.setText("播放完毕");
                        }
                    });

            /* 设置准备完毕监听器 */
                    mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer arg0) {
                            System.out.println("准备完毕");
                    /* 设置播放状态 */
                            status.setText("播放中");
                            allTimeView.setText(allTime);
                        }
                    });

                    new Thread() {
                        public void run() {
                            try {
                                System.out.println("设置数据源");

                                mediaPlayer.setDataSource(dataSource);
                                mediaPlayer.prepare();

                        /* 打印播放视频的时长 */
                                System.out.println("视频播放长度 : " + mediaPlayer.getDuration());
                                seekBar.setMax(mediaPlayer.getDuration());

                                allTime = showData(mediaPlayer.getDuration());
                                mediaPlayer.start();

                            } catch (IllegalStateException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                        ;
                    }.start();

            /* 设置 MediaPlayer 开始播放标识为 true */
                    isStartPlaying = true;

            /* 设置播放状态 */
                    status.setText("正在缓冲");
                    thread.start();
                }


            }

            /**
             * 在 Surface 大小发生改变的时候回调
             * 实现的 SurfaceHolder.Callback 接口方法
             */
            @Override
            public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
                System.out.println("SurfaceHolder.Callback.surfaceChanged : Surface 大小发生改变");
            }

            /**
             * 在 Surface 创建的时候回调, 一般在该方法中开始绘图
             * 实现的 SurfaceHolder.Callback 接口方法
             */
            @Override
            public void surfaceCreated(SurfaceHolder arg0) {
                System.out.println("SurfaceHolder.Callback.surfaceCreated : Surface 开始创建");
            }

            /**
             * 在 Surface 销毁之前回调, 在该方法中停止渲染线程, 释放相关资源
             * 实现的 SurfaceHolder.Callback 接口方法
             */
            @Override
            public void surfaceDestroyed(SurfaceHolder arg0) {
                System.out.println("SurfaceHolder.Callback.surfaceDestroyed : Surface 销毁");
            }

            @Override
            protected void onDestroy() {
                if (mediaPlayer != null)
                    mediaPlayer.release();
                super.onDestroy();
            }

    public String showData(long num){
        Date date = new Date(num);
        SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");

        return sdf.format(date);
    }
}