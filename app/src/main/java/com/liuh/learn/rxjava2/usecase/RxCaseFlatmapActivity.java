package com.liuh.learn.rxjava2.usecase;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.liuh.learn.rxjava2.R;
import com.liuh.learn.rxjava2.model.CategoryDataRequest;
import com.liuh.learn.rxjava2.model.ItemBean;
import com.rx2androidnetworking.Rx2AndroidNetworking;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * 多个网络请求依次依赖
 * <p>
 * 这种情况在实际使用中比比皆是，例如用户注册成功后需要自动登录，只需要先通过注册接口注册用户信息，注册成功后马上调用登录接口进行自动登录即可。
 * <p>
 * flatMap 操作符恰好解决了这种应用场景。flatMap 操作符可以将一个发射数据的 Observable 变换为多个 Observables ，<br>
 * 然后将它们发射的数据合并后放到一个单独的 Observable，利用这个特性，可以很轻松地达到了我们的需求。
 */
public class RxCaseFlatmapActivity extends AppCompatActivity {

    @BindView(R.id.tv_data_about_flatmap)
    TextView tvDataAbout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rx_case_flatmap);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.btn_request_data_flatmap})
    void onRequestDataClicked(View view) {
        Rx2AndroidNetworking.get("http://gank.io/api/data/福利/2/1")
                .build()
                .getObjectObservable(CategoryDataRequest.class) // 发起获取girls列表的请求，并解析到GirlsDataRequest
                .subscribeOn(Schedulers.io()) // 在io线程进行网络请求
                .observeOn(AndroidSchedulers.mainThread()) // 在主线程处理获取girls列表的请求结果
                .doOnNext(new Consumer<CategoryDataRequest>() {
                    @Override
                    public void accept(CategoryDataRequest categoryDataRequest) throws Exception {
                        // 先根据获取girls列表的响应结果做一些操作
                        Log.e("-----", "accept---doOnNext");
                        tvDataAbout.append("accept : doOnNext : " + categoryDataRequest.toString() + "\n");
                    }
                })
                .observeOn(Schedulers.io()) // 回到 io 线程去处理获取girl详情的请求
                .flatMap(new Function<CategoryDataRequest, ObservableSource<ItemBean>>() {
                    @Override
                    public ObservableSource<ItemBean> apply(CategoryDataRequest categoryDataRequest) throws Exception {
                        if (categoryDataRequest != null && categoryDataRequest.getResults() != null &&
                                categoryDataRequest.getResults().size() > 0) {
                            // 这里会 error 的，因为这个接口时不存在的，只是为了写成依赖上一个接口的样子。
                            return Rx2AndroidNetworking.post("http://gank.io/api/data/福利/detail")
                                    .addBodyParameter("id", categoryDataRequest.getResults().get(0).get_id())
                                    .build()
                                    .getObjectObservable(ItemBean.class);
                        }

                        return null;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ItemBean>() {
                    @Override
                    public void accept(ItemBean itemBean) throws Exception {
                        Log.e("-----", "access---success");
                        tvDataAbout.append("access---success : " + itemBean.toString());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e("-----", "access---error");
                        tvDataAbout.append("access---error : " + throwable.getMessage());
                    }
                });
    }

}
