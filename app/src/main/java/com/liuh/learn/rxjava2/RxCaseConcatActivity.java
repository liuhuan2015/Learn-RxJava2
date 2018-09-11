package com.liuh.learn.rxjava2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import com.rx2androidnetworking.Rx2AndroidNetworking;

/**
 * 先读取缓存，如果缓存没数据再通过网络请求获取数据，更新UI
 * <p>
 * concat 操作符
 * <p>
 * concat 可以做到不交错的发射两个甚至多个 Observable 的发射事件，并且只有前一个 Observable 终止( onComplete() ) 后才会定义下一个 Observable。
 * <p>
 * 利用这个特性，我们就可以先读取缓存数据，倘若获取到的缓存数据不是我们想要的，再调用 onComplete() 以执行获取网络数据的 Observable，<br>
 * 如果缓存数据能应我们所需，则直接调用 onNext() ，防止过度的网络请求，浪费用户的流量。
 */
public class RxCaseConcatActivity extends AppCompatActivity {

    @BindView(R.id.tv_data_about)
    TextView tvDataAbout;

    private boolean isFromNet = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rx_case_concat);
        ButterKnife.bind(this);


    }

    @OnClick(R.id.btn_request_data)
    void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_request_data:
                requestDataConcat();
                break;
        }
    }

    private void requestDataConcat() {
        Observable<GirlsDataRequest> cache = Observable.create(new ObservableOnSubscribe<GirlsDataRequest>() {
            @Override
            public void subscribe(ObservableEmitter<GirlsDataRequest> emitter) throws Exception {
                Log.e("-----", "create 当前线程： " + Thread.currentThread().getName() + "\n");
                GirlsDataRequest data = CacheManager.getInstance().getGirlsDataRequest();

                // 在操作符 concat 中，只有调用 onComplete 之后才会执行下一个 Observable
                if (data != null) { // 如果缓存数据不为空，则直接读取缓存数据，而不读取网络数据
                    isFromNet = false;

                    Log.e("-----", "\nsubscribe: 读取缓存数据");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tvDataAbout.append("\nsubscribe读取缓存数据：\n");
                        }
                    });
                    emitter.onNext(data);
                } else {
                    isFromNet = true;

                    Log.e("-----", "\nsubscribe: 读取网络数据");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tvDataAbout.append("\nsubscribe读取网络数据：\n");
                        }
                    });
                    emitter.onComplete();
                }
            }
        });

        Observable<GirlsDataRequest> network = Rx2AndroidNetworking.get("http://gank.io/api/data/福利/5/1")
                .build()
                .getObjectObservable(GirlsDataRequest.class);

        Observable.concat(cache, network)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<GirlsDataRequest>() {
                    @Override
                    public void accept(GirlsDataRequest girlsDataRequest) throws Exception {
                        if (isFromNet) {
                            tvDataAbout.append("accept : 网络获取数据，设置缓存: \n");
                            Log.e("-----", "accept : 网络获取数据，设置缓存: \n" + girlsDataRequest.toString());
                            CacheManager.getInstance().setGirlsDataRequest(girlsDataRequest);
                        }
                        tvDataAbout.append("accept: 读取数据成功:" + girlsDataRequest.toString() + "\n");
                        Log.e("-----", "accept: 读取数据成功:" + girlsDataRequest.toString());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e("-----", "subscribe 失败:"+Thread.currentThread().getName() );
                        Log.e("-----", "accept: 读取数据失败："+throwable.getMessage() );
                        tvDataAbout.append("accept: 读取数据失败："+throwable.getMessage()+"\n");
                    }
                });


    }

}



















