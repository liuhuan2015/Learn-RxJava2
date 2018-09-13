package com.liuh.learn.rxjava2.usecase;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.liuh.learn.rxjava2.model.GirlsDataRequest;
import com.liuh.learn.rxjava2.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Request.Builder;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class RxUseSimpleHttpRequestActivity extends AppCompatActivity {

    @BindView(R.id.tv_rx_http_request_use_result)
    TextView tvHttpRequestResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rx_use_example);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.btn_rx_http_request_use})
    void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_rx_http_request_use:
                rxHttpRequest();
                break;
        }
    }

    /**
     * 一个简单的网络请求的例子
     * <p>
     * https://gank.io/api/data/福利/10/1
     * <p>
     * 原文提供的接口失效了，打算使用 gank.io 的
     * <p>
     * 1) 通过 Observable.create() 方法，调用 OkHttp 网络请求；
     * 2) 通过 map 操作符集合 gson，将 Response 转换为 bean 类；
     * 3) 通过 doOnNext() 方法，解析 bean 中的数据，并进行数据库存储等操作；
     * 4) 调度线程，在子线程中进行耗时操作任务，在主线程中更新 UI ；
     * 5) 通过 subscribe()，根据请求成功或者失败来更新 UI 。
     */
    private void rxHttpRequest() {
        Observable.create(new ObservableOnSubscribe<Response>() {
            @Override
            public void subscribe(ObservableEmitter<Response> e) throws Exception {
                Builder builder = new Builder()
                        .url("http://gank.io/api/data/福利/2/1")
                        .get();
                Request request = builder.build();
                Call call = new OkHttpClient().newCall(request);
                Response response = call.execute();
                e.onNext(response);
            }
        }).map(new Function<Response, GirlsDataRequest>() {
            @Override
            public GirlsDataRequest apply(Response response) throws Exception {

                Log.e("-------", "map 线程:" + Thread.currentThread().getName() + "\n");
                if (response.isSuccessful()) {
                    ResponseBody body = response.body();
                    if (body != null) {
                        Log.e("-------", "map:转换前:" + response.body());//这里如果写成 body.string() ,就会有问题，想不通
                        return new Gson().fromJson(body.string(), GirlsDataRequest.class);
                    }
                }
                return null;
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<GirlsDataRequest>() {
                    @Override
                    public void accept(GirlsDataRequest s) throws Exception {
                        Log.e("-------", "doOnNext 线程:" + Thread.currentThread().getName() + "\n");
                        Log.e("-------", "doOnNext: 保存成功：" + s.toString() + "\n");
                        tvHttpRequestResult.append("doOnNext 线程:" + Thread.currentThread().getName() + "\n");
                        tvHttpRequestResult.append("doOnNext: 保存成功：" + s.toString() + "\n");
                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<GirlsDataRequest>() {
                    @Override
                    public void accept(GirlsDataRequest data) throws Exception {
                        Log.e("-------", "subscribe 线程:" + Thread.currentThread().getName() + "\n");
                        Log.e("-------", "成功:" + data.toString() + "\n");
                        tvHttpRequestResult.append("subscribe 线程:" + Thread.currentThread().getName() + "\n");
                        tvHttpRequestResult.append("成功:" + data.toString() + "\n");
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e("-------", "subscribe 线程:" + Thread.currentThread().getName() + "\n");
                        Log.e("-------", "失败：" + throwable.getMessage() + "\n");
                        tvHttpRequestResult.append("subscribe 线程:" + Thread.currentThread().getName() + "\n");
                        tvHttpRequestResult.append("失败：" + throwable.getMessage() + "\n");
                    }
                });
    }
}
