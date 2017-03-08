package cn.bluemobi.dylan.wechatmoments;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.orhanobut.logger.Logger;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static cn.bluemobi.dylan.wechatmoments.ApiService.baseUrl;

public class MainActivity extends AppCompatActivity {
    private final String TAG="HTTP";
    private ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lv = (ListView) findViewById(R.id.lv);
        x.Ext.init(getApplication());
        getTweets();
    }

    private void getTweets() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder().addInterceptor(new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                Log.d(TAG, "OkHttp====message " + message);
            }

        })).build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(okHttpClient)
                .build();
        ApiService apiService = retrofit.create(ApiService.class);
        apiService.getTweets()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<TweetsEntity>>() {
                    @Override
                    public void onCompleted() {
                        Toast.makeText(MainActivity.this, "Get Top Movie TweetsEntity", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(List<TweetsEntity> entityList) {
                        Logger.d("tweetsEntity="+entityList.toString());
                        Logger.d("entityList.size()="+entityList.size());
                    }
                });
//        x.http().get(new RequestParams("http://thoughtworks-ios.herokuapp.com/user/jsmith/tweets"), new Callback.CommonCallback<String>() {
//            @Override
//            public void onSuccess(String result) {
//                Logger.json(result);
//            }
//
//            @Override
//            public void onError(Throwable ex, boolean isOnCallback) {
//                ex.printStackTrace();
//            }
//
//            @Override
//            public void onCancelled(Callback.CancelledException cex) {
//
//            }
//
//            @Override
//            public void onFinished() {
//
//            }
//        })
//        ;
    }

}
