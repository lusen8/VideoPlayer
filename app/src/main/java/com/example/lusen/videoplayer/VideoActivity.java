package com.example.lusen.videoplayer;

import android.content.Intent;
import android.content.res.Configuration;
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
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.lusen.videoplayer.date.ItemData;
import com.example.lusen.videoplayer.util.ScreenRotateUtil;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class VideoActivity extends AppCompatActivity implements View.OnClickListener, SurfaceHolder.Callback {

    private SurfaceView surface_view;                           /* 播放视频载体 */
    private TextView status;                                     /* 显示播放状态 */
    private SeekBar seekBar;                                     /* 显示进度条 */
    private TextView allTimeView;                               /* 显示总时间 */
    private TextView playTimeView;                              /* 显示当前时间 */
    private String playTime;                                    /* 当前时间 */
    private LinearLayout control;                               /*控制区，并没啥暖用*/
    private boolean flagUnPaly = true;                         /*用于切换播放和暂停图标的标志*/
    private Thread thread;                                      /*负责更新时间和进度条的子线程*/
    private String allTime;                                     /* 总时间 */
    private ImageView previous;
    private ImageView next;
    private ImageView play;                                        /* 播放按钮 */
    private ImageView reset;                                       /* 重放按钮 */
    private ImageView stop;                                        /* 停止按钮 */
    private String urls = null;
    private ArrayList<ItemData> itemDataArrayList;
    private int mPosition = 0;

    private MediaPlayer mediaPlayer;                            /* 播放器 */
    private SurfaceHolder surface_holder;                       /* Surface 控制器 */

    /*子线程返回当前播放时间，主线程更新UI*/
    private Handler handler1=new Handler(){
        @Override
        public void handleMessage(Message msg){
            if(msg.what == 1){
                playTime = showData(msg.getData().getLong("info"));
                seekBar.setProgress((int) msg.getData().getLong("info"));
            }
        }
    };
    private boolean isStartPlaying;     /* 是否开始了播放 */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /*
        * 隐藏状态栏
        * */
        Window window = getWindow();
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        int flag= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        //设置当前窗体为全屏显示
        window.setFlags(flag, flag);


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        initViews();
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ScreenRotateUtil.getInstance(this).start(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        ScreenRotateUtil.getInstance(this).stop();
    }
    /**
     * 横竖屏切换或者输入法等事件触发时调用
     * 需要在清单文件中配置权限
     * 需要在当前Activity配置configChanges属性
     * @param newConfig
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (ScreenRotateUtil.getInstance(this).isLandscape()) {
            //动态设置视频播放区域的为整个屏幕区
            /*
            * 遇到玄学了，不知道为啥？原本设置隐藏控制区，和显示控制区的代码，变成了bug，注释掉，全他妈好了，不到那能继续播放，而且还能隐藏控制区，
            * 算了，不管了，写个TODO 记录一下，日后再来研究研究
            * */
            //TODO: 重力感应，旋转后 隐藏/显示 控制区，玄学。
//            DisplayMetrics dm = new DisplayMetrics();
//            this.getWindowManager().getDefaultDisplay().getMetrics(dm);
//            LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) surface_view.getLayoutParams();
//            linearParams.height = dm.heightPixels;
//            linearParams.width = dm.widthPixels;
//            linearParams.setMargins(0, 0, 0, 0);
//            surface_view.setLayoutParams(linearParams);
//
//            control.setVisibility(View.GONE);

        } else {
            //动态设置视频播放区域为之前的大小
//            LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) surface_view.getLayoutParams();
//            linearParams.height = 200;
//            linearParams.width = 300;
//            surface_view.setLayoutParams(linearParams);
//
//            control.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 满屏播放
     */
    public void scaleFull(View view) {
        ScreenRotateUtil.getInstance(this).toggleRotate();
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
        control = (LinearLayout) findViewById(R.id.control_space);
        play = (ImageView) findViewById(R.id.play);
        seekBar = (SeekBar) findViewById(R.id.progress);
        reset = (ImageView) findViewById(R.id.reset);
        previous = (ImageView) findViewById(R.id.previous);
        next = (ImageView) findViewById(R.id.next);
        stop = (ImageView) findViewById(R.id.stop);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        Intent intent = getIntent();
        mPosition = intent.getIntExtra("number",mPosition);
        Log.d("............",mPosition+"");
        if ("action".equals(intent.getAction())) {
            itemDataArrayList = (ArrayList<ItemData>) intent.getSerializableExtra("arrlist");
            urls = itemDataArrayList.get(mPosition).getHelfVideo();
        }
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
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
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
                if (flagUnPaly){/* 播放该 url 代表的网络视频 */
                    play.setImageResource(R.drawable.ic_pause);
                    flagUnPaly = false;
                    playVideo(urls);
                }else {/*暂停播放*/
                    if (mediaPlayer != null) {
                        play.setImageResource(R.drawable.ic_play_arrow);
                        mediaPlayer.pause();
                        status.setText("暂停");
                        flagUnPaly = true;
                    }
                }break;
            case R.id.previous:
                if(mPosition > 0) {
                    //启动另一个PlayVideoActicity
                    Intent preIntent = new Intent(VideoActivity.this, VideoActivity.class);
                    preIntent.putExtra("number", mPosition - 1);
                    startActivity(preIntent);
                    //结束当前的活动
                    finish();
                }
                break;
            case  R.id.next:
                if(mPosition < itemDataArrayList.size()) {
                    //启动另一个PlayVideoActicity
                    Intent nextIntent = new Intent(VideoActivity.this, VideoActivity.class);
                    nextIntent.putExtra("number", mPosition + 1);
                    startActivity(nextIntent);
                    //结束当前的活动
                    finish();
                }
                break;

            case R.id.reset:
                if (mediaPlayer != null) {
                    mediaPlayer.seekTo(0);
                    mediaPlayer.start();
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
                    play.setImageResource(R.drawable.ic_play_arrow);
                }
                break;
            default:break;
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
                return true;
            }
        });

        /* 设置缓冲进度更新监听器 */
        mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer arg0, int percent) {
                /* 打印缓冲的百分比, 如果缓冲 */
                System.out.println("缓冲了的百分比 : " + percent + " %");
                seekBar.setSecondaryProgress(percent * mediaPlayer.getDuration()/100);
            }
        });

        /* 设置播放完毕监听器 */
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer arg0) {
                System.out.println("播放完毕了");
                status.setText("播放完毕");
                play.setImageResource(R.drawable.ic_play_arrow);
            }
        });

     /* 设置准备完毕监听器 */
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer arg0) {
                //检查是否有过播放记录

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

//                     SharedPreferences settings =  PreferenceManager.getDefaultSharedPreferences(VideoActivity.this);
//                     long memoryTime = settings.getLong("current"+urls,0);
//                     Log.d("第二次获取到的时间",memoryTime+"");

                     seekBar.setMax(mediaPlayer.getDuration());
                     allTime = showData(mediaPlayer.getDuration());
                     mediaPlayer.start();

//                     if (memoryTime != 0){
//                         seekBar.setProgress((int) memoryTime);
//                         mediaPlayer.seekTo((int) memoryTime);
//                         playTimeView.setText(showData(memoryTime));
//                     }

                 } catch (IllegalStateException e) {
                     e.printStackTrace();
                 } catch (IOException e) {
                     e.printStackTrace();
                 }
             }
         }.start();

        /* 设置 MediaPlayer 开始播放标识为 true */
        isStartPlaying = true;

        /* 设置播放状态 */
        status.setText("正在缓冲");
        thread.start();
    }


}
    /**
     ** 在 Surface 大小发生改变的时候回调
     ** 实现的 SurfaceHolder.Callback 接口方法
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
//        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(VideoActivity.this).edit();
//        editor.putInt("current"+urls,mediaPlayer.getCurrentPosition());
//        editor.apply();
//        Log.d("记录的时间",mediaPlayer.getCurrentPosition()+"");
        if (mediaPlayer != null)
            mediaPlayer.release();
        super.onDestroy();
    }
 /*将获取到的毫秒转化为 00:00 格式*/
  public String showData(long num){
     Date date = new Date(num);
     SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");
     return sdf.format(date);
  }

}