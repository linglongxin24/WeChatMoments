package cn.bluemobi.dylan.wechatmoments;

import java.util.List;

import retrofit2.http.GET;
import rx.Observable;

/**
 * Created by yuandl on 2017-03-08.
 */

public interface ApiService {
    String baseUrl = "http://thoughtworks-ios.herokuapp.com/";

    @GET("user/jsmith/tweets")
    Observable<List<TweetsEntity>> getTweets();
}
