package com.example.lusen.videoplayer;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;

import com.example.lusen.videoplayer.adapt.MyRecycleAdapter;
import com.example.lusen.videoplayer.date.FirstRecycleData;
import com.example.lusen.videoplayer.date.ItemData;
import com.example.lusen.videoplayer.util.MyGsonUtil;
import com.example.lusen.videoplayer.util.MyHttpURL;
import com.example.lusen.videoplayer.widget.RefreshRecyclerView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    final String res = "http://route.showapi.com/255-1?showapi_appid=38533&showapi_sign=a95174ffbd954db0bceae83171635d4d&num=18875141493&type=41";


    private Handler handler = new Handler();
    ArrayList<FirstRecycleData> firstRecycleDatas;
    ArrayList<ItemData> itemDataArrayList = new ArrayList<ItemData>();
    RefreshRecyclerView recyclerView = null;
    private SwipeRefreshLayout swipeRefreshLayout;
    MyRecycleAdapter recycleAdapter = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadHttp();
        initView();
        initListener();

    }

    private void loadHttp(){
        MyHttpURL.get(res, new MyHttpURL.Callback() {
            @Override
            public void onResponse(String response) {
                response = "["+response+"]";
                Log.d("百思不得姐的接口，回调",response);
                firstRecycleDatas = (ArrayList<FirstRecycleData>) MyGsonUtil.getObjectList(response,FirstRecycleData.class);
                Log.d("解析后的数据",firstRecycleDatas.get(0).getShowapi_res_body().getPagebean().getContentlist().size()+"");

                for (int i= 0; i<firstRecycleDatas.get(0).getShowapi_res_body().getPagebean().getContentlist().size();i++){
                    if (firstRecycleDatas.get(0).getShowapi_res_body().getPagebean().getContentlist().get(i).getId() == "24982481"){
                        continue;
                    }
                    ItemData itemData = new ItemData();
                    itemData.setMainImg(firstRecycleDatas.get(0).getShowapi_res_body().getPagebean().getContentlist().
                            get(i).getVideo_uri());
                    itemData.setName(firstRecycleDatas.get(0).getShowapi_res_body().getPagebean().getContentlist().get(i).getText());
                    itemData.setTime(firstRecycleDatas.get(0).getShowapi_res_body().getPagebean().getContentlist().get(i).getCreate_time());
                    itemData.setUserImg(firstRecycleDatas.get(0).getShowapi_res_body().getPagebean().getContentlist().get(i).getProfile_image());
                    itemData.setUserName(firstRecycleDatas.get(0).getShowapi_res_body().getPagebean().getContentlist().get(i).getName());
                    itemData.setHelfVideo(firstRecycleDatas.get(0).getShowapi_res_body().getPagebean().getContentlist().get(i).getVideo_uri());
                    itemDataArrayList.add(itemData);
                }
                recycleAdapter.addData(itemDataArrayList);
                recyclerView.setAdapter(recycleAdapter);

            }
        });
    }

    private void initView(){
        recyclerView = (RefreshRecyclerView) findViewById(R.id.recycle_main);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_main);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_red_light, android.R.color.holo_blue_light, android.R.color.holo_green_light);
        recyclerView.setLoadMoreEnable(true);//允许加载更多
        recyclerView.setFooterResource(R.layout.foot_main);//设置脚布局
        recycleAdapter = new MyRecycleAdapter(this,itemDataArrayList,this);
//        recyclerView.setAdapter(recycleAdapter);

    }

    private void initListener(){
        recyclerView.setOnLoadMoreListener(new RefreshRecyclerView.OnLoadMoreListener() {
            @Override
            public void loadMoreListener() {
               handler.postDelayed(new Runnable() {
                   @Override
                   public void run() {
//                       MyHttpURL.get(res+"&page=2", new MyHttpURL.Callback() {
//                           @Override
//                           public void onResponse(String response) {
//                               response = "["+response+"]";
//                               Log.d("百思不得姐的接口，回调",response);
//                               firstRecycleDatas = (ArrayList<FirstRecycleData>) MyGsonUtil.getObjectList(response,FirstRecycleData.class);
//                               Log.d("解析后的数据",firstRecycleDatas.get(0).getShowapi_res_body().getPagebean().getContentlist().size()+"");
//                               recycleAdapter.addData(itemDataArrayList);
//                           }
//                       });
                       recyclerView.notifyData();
                   }
               },2000);

            }
        });
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                        loadHttp();
                        recyclerView.notifyData();//刷新数据
                    }
                }, 2000);
            }
        });
    }
}
