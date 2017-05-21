package com.example.lusen.videoplayer.adapt;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lusen.videoplayer.R;
import com.example.lusen.videoplayer.VideoActivity;
import com.example.lusen.videoplayer.date.ItemData;
import com.example.myglide.MyGlide;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by lusen on 2017/5/19.
 */

public class MyRecycleAdapter extends RecyclerView.Adapter<MyRecycleAdapter.MyHolder>
{

    private ArrayList<ItemData> itemDataArrayList;
    private Context context;
    private Activity activity;

    public MyRecycleAdapter(Context context,ArrayList<ItemData> itemDataArrayList, Activity activity) {
        this.itemDataArrayList = itemDataArrayList;
        this.context = context;
        this.activity = activity;
    }
    public void addData(ArrayList<ItemData> itemDataArrayList){
        this.itemDataArrayList.addAll(itemDataArrayList);
        notifyDataSetChanged();
    }
    @Override
    public MyRecycleAdapter.MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_main,parent,false));
}

    @Override
    public void onBindViewHolder(final MyHolder holder, final int position) {
        //用户名
        holder.userName.setText(itemDataArrayList.get(position).getUserName());
        //标题
        holder.titlt.setText(itemDataArrayList.get(position).getName());
        //创建时间
        holder.creatTime.setText(itemDataArrayList.get(position).getTime());
        //用户头像
        MyGlide.with(context).load(itemDataArrayList.get(position).getUserImg()).
                                into(holder.userImg);
        //取视频第一帧做封面
        try {
            holder.pagerImg.setImageBitmap(changBitmap(itemDataArrayList.get(position).getMainImg(),250,150));
        }catch (Exception e){e.printStackTrace();}

        holder.pagerImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, VideoActivity.class);

                intent.putExtra("arrlist",itemDataArrayList);
                intent.putExtra("number",position);
                intent.setAction("action");

                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return itemDataArrayList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder{
        private TextView userName;
        private TextView creatTime;
        private TextView titlt;
        private ImageView userImg;
        private ImageView pagerImg;
        public MyHolder(View itemView) {
            super(itemView);
            creatTime = (TextView) itemView.findViewById(R.id.send_time);
            userName= (TextView) itemView.findViewById(R.id.author_tv);
            userImg = (ImageView) itemView.findViewById(R.id.author_img);
            pagerImg = (ImageView) itemView.findViewById(R.id.item_img);
            titlt = (TextView) itemView.findViewById(R.id.item_title);

        }
    }
    private Bitmap changBitmap(String url,int width, int height){
            Bitmap bitmap = null;
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            int kind = MediaStore.Video.Thumbnails.MINI_KIND;
            try {
                if (Build.VERSION.SDK_INT >= 12) {
                    retriever.setDataSource(url, new HashMap<String, String>());
                } else {
                    retriever.setDataSource(url);
                }
                bitmap = retriever.getFrameAtTime();
            } catch (IllegalArgumentException ex) {
                // Assume this is a corrupt video file
            } catch (RuntimeException ex) {
                // Assume this is a corrupt video file.
            } finally {
                try {
                    retriever.release();
                } catch (RuntimeException ex) {
                    // Ignore failures while cleaning up.
                }
            }
            if (kind == MediaStore.Images.Thumbnails.MICRO_KIND && bitmap != null) {
                bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
                        ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
            }
            return bitmap;
    }

}
