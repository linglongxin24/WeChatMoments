package cn.bluemobi.dylan.wechatmoments;

import java.util.List;

import cn.bluemobi.dylan.wechatmoments.entity.TweetsEntity;
import cn.bluemobi.dylan.wechatmoments.entity.UserEntity;
import retrofit2.http.GET;
import rx.Observable;

/**
 * 接口API
 * Created by yuandl on 2017-03-08.
 * email:13468857714@qq.com
 * phone:13468857714
 */

public interface ApiService {

    String baseUrl = "http://thoughtworks-ios.herokuapp.com/";

    /**
     * 获取用户信息
     *
     * @return
     */
    @GET("user/jsmith")
    Observable<UserEntity> getUserInfo();

    /**
     * 获取朋友圈列表
     *
     * @return
     */
    @GET("user/jsmith/tweets")
    Observable<List<TweetsEntity>> getTweets();
}
