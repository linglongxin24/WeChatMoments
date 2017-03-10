package cn.bluemobi.dylan.wechatmoments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.lcodecore.tkrefreshlayout.Footer.LoadingView;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.lcodecore.tkrefreshlayout.header.progresslayout.ProgressLayout;

import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.bluemobi.dylan.wechatmoments.adapter.MyAdapter;
import cn.bluemobi.dylan.wechatmoments.entity.TweetsEntity;
import cn.bluemobi.dylan.wechatmoments.entity.UserEntity;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static cn.bluemobi.dylan.wechatmoments.ApiService.baseUrl;

/**
 * 朋友圈主页
 */
public class MainActivity extends AppCompatActivity {
    /**
     * HTTP请求LOG
     */
    private final String TAG = "OKHTTP";
    /**
     * 朋友圈数据列表
     */
    private ListView lv;
    /**
     * 用户头像
     */
    private ImageView iv_head;
    /**
     * 朋友圈列表数据
     */
    private List<TweetsEntity> entityList;
    /**
     * 朋友圈列表适配器
     */
    private MyAdapter myAdapter;
    /**
     * 上下文
     */
    private Context mContext;
    /**
     * 下拉刷新上拉加载
     */
    private TwinklingRefreshLayout refreshLayout;
    /**
     * 第几页
     */
    private int pageNo = 1;
    /**
     * 每页数量
     */
    private int pageSize = 5;
    /**
     * 总页数
     */
    private int pageCount = 0;
    /**
     * 服务器接口API
     */
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        initView();
        initRetrofit();
        initAdapter();
        initRefreshView();
    }

    /**
     * 初始化view
     */
    private void initView() {
        lv = (ListView) findViewById(R.id.lv);
        View headView = LayoutInflater.from(mContext).inflate(R.layout.headview, null);
        iv_head = (ImageView) headView.findViewById(R.id.iv_head);
        lv.addHeaderView(headView);
    }

    /**
     * 初始化网络请求
     */
    private void initRetrofit() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(3, TimeUnit.MINUTES)
                .addInterceptor(new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
                    @Override
                    public void log(String message) {
                        Log.d(TAG, "OkHttp====message " + message);
                    }

                }).setLevel(HttpLoggingInterceptor.Level.BODY)).build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create(new Gson()))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(okHttpClient)
                .build();

        apiService = retrofit.create(ApiService.class);
    }


    /**
     * 初始化适配器
     */
    private void initAdapter() {
        myAdapter = new MyAdapter(entityList, mContext);
        lv.setAdapter(myAdapter);
    }

    /**
     * 初始化下拉刷新与上拉加载更多
     */
    private void initRefreshView() {
        refreshLayout = (TwinklingRefreshLayout) findViewById(R.id.refreshLayout);
        ProgressLayout header = new ProgressLayout(mContext);
        refreshLayout.setHeaderView(header);
        refreshLayout.setFloatRefresh(true);
        refreshLayout.setOverScrollRefreshShow(false);
        refreshLayout.setHeaderHeight(140);
        refreshLayout.setWaveHeight(240);
        refreshLayout.setOverScrollHeight(200);
        header.setColorSchemeResources(R.color.Blue, R.color.Orange, R.color.Yellow, R.color.Green);
        LoadingView loadingView = new LoadingView(mContext);
        refreshLayout.setBottomView(loadingView);
        refreshLayout.setEnableOverScroll(false);
        refreshLayout.startRefresh();
        refreshLayout.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onRefresh(final TwinklingRefreshLayout refreshLayout) {
                pageNo = 1;
                getUserInfo();
                loadData();
            }

            @Override
            public void onLoadMore(final TwinklingRefreshLayout refreshLayout) {
                if (pageNo == pageCount) {
                    loadOnFinish();
                    Toast.makeText(mContext, "没有了", Toast.LENGTH_SHORT).show();
                } else {
                    pageNo++;
                    loadData();
                }
            }
        });
    }
    public void loadOnFinish() {
        refreshLayout.finishRefreshing();
        refreshLayout.finishLoadmore();
    }

    /**
     * 获取用户信息
     */
    private void getUserInfo() {
        apiService.getUserInfo()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<UserEntity>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(UserEntity userEntity) {
                        Glide.with(mContext).load(userEntity.getAvatar()).placeholder(R.mipmap.ic_launcher).error(R.mipmap.ic_launcher).into(iv_head);
                    }

                });
    }

    /**
     * 初始化获取朋友圈列表数据
     */
    private void getTweets() {

        apiService.getTweets()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<TweetsEntity>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        loadOnFinish();
                    }

                    @Override
                    public void onNext(List<TweetsEntity> entityList) {

                        for (int i = 0; i < entityList.size(); i++) {
                            TweetsEntity tweetsEntity = entityList.get(i);
                            if (tweetsEntity.getSender() == null) {
                                entityList.remove(tweetsEntity);
                                i--;
                            }
                        }
                        MainActivity.this.entityList = entityList;
                        pageNo = 1;
                        pageCount = entityList.size() / pageSize + 1;
                        loadData();
                    }

                });
    }

    /**
     * 模拟分页加载
     */
    private void loadData() {
        if (entityList == null) {
            getTweets();
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    int endIndex = pageNo * pageSize > (entityList.size()) ? entityList.size() : pageNo * pageSize;
                    List<TweetsEntity> data = entityList.subList(0, endIndex);
                    myAdapter.notifyDataSetChanged(data);
                    loadOnFinish();
                }
            }, 1 * 1000);
        }
    }

}
