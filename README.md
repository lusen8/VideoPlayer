# VideoPlayer
红岩半期考核

* 温馨提示： `使用此app时，请在wifi条件下使用。流量超出概不负责哈。/坏笑~`

#实现的功能（好少啊）  

   **播放控制**  
   
1、 `进度条，拖动改变视频进度`  
  
2、 `开始、暂停、停止`  
  
3、 `重力感应，自动调整横竖屏`

  **列表**

1、`把数据加载出来，用列列表（用RecyclerView ）的形式展示`

2、`每个Item需要显示视频的部分具体信息，如视频描述，作者名、作者头像等`

3、`截取视频第一帧做封面`

  **拓展功能**
  
1、`视频缓冲，下面的进度条两种，当前播放到的进度，当前缓冲到的进度`


## 各种BUG ##

 * `加载时出现卡顿`   
    原因：由于加载视频第一帧做封面，超级费时。 尝试放入子线程，加载不出来，不要问我为什么，我也很绝望。所以，使用时一定注意：`轻划，轻按` /捂脸哭~~
 * `播放视频时一定几率可能出现点击返回键 直接重启app/抛异常`  
    原因：具体还在排查中，前期猜想可能由于MediaPlay 操作不当，导致触发播放错误监听事件。
 * `视频总时间显示，一定几率第一次点击播放时，不会显示出来视频总时间`  
    原因：不明。   解决方法：点击reset按键总时间必出。无多大影响。  
    
## 截图 ##

### RecycleView ###
![RecycleView](https://github.com/lusen8/VideoPlayer/blob/master/screenShot/recycleView.png)

### 播放 ###
![播放](https://github.com/lusen8/VideoPlayer/blob/master/screenShot/%E6%92%AD%E6%94%BE.png) 

### 暂停 ###
![暂停](https://github.com/lusen8/VideoPlayer/blob/master/screenShot/%E6%9A%82%E5%81%9C.png)

### 横屏 ###
![横屏](https://github.com/lusen8/VideoPlayer/blob/master/screenShot/%E6%A8%AA%E5%B1%8F.png)
