package com.liuh.learn.rxjava2.net;

import com.liuh.learn.rxjava2.model.CategoryDataRequest;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Date: 2018/9/14 08:58
 * Description:
 */
public interface GankApi {
    /**
     * 获取某一个分类数据
     *
     * @param category 分类
     * @param count 请求个数
     * @param page 第几页
     * @return
     */
    @GET("data/{category}/{count}/{page}")
    Observable<CategoryDataRequest> getCategoryData(@Path("category") String category, @Path("count") int count, @Path("page") int page);


}
