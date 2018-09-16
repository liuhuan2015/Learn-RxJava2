package com.liuh.learn.rxjava2.usecase;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.liuh.learn.rxjava2.R;
import com.liuh.learn.rxjava2.model.CategoryDataRequest;
import com.rx2androidnetworking.Rx2AndroidNetworking;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * debounce 操作符 减少频繁的网络请求
 * <p>
 * debounce 操作符可以过滤掉发射频率过快的数据项
 * <p>
 * 设想场景：
 * 1 . 输入框数据变化时就要进行网络请求，这样会产生大量的网络请求。这时候可以通过debounce进行处理
 * 2 . 点击一次按钮就进行一次网络请求
 */
public class RxCaseDebounceActivity extends AppCompatActivity {

    @BindView(R.id.btn_debounce)
    Button btnDebounce;

    @BindView(R.id.tv_data_about_debounce)
    TextView tvDataAbout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rx_case_debounce);
        ButterKnife.bind(this);

        RxView.clicks(btnDebounce)
                .debounce(2, TimeUnit.SECONDS) // 过滤掉发射频率小于两秒的发射事件
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        clickBtn();
                    }
                });
    }

    private void clickBtn() {
        Rx2AndroidNetworking.get("http://gank.io/api/data/福利/2/1") // 获取两条数据
                .build()
                .getObjectObservable(CategoryDataRequest.class)
                .subscribeOn(Schedulers.io()) // 在 io 线程进行网络请求
                .observeOn(AndroidSchedulers.mainThread()) // 在主线程进行更新Ui等操作
                .subscribe(new Consumer<CategoryDataRequest>() {
                    @Override
                    public void accept(CategoryDataRequest categoryDataRequest) throws Exception {
                        Log.e("-----", "accept : 获取数据成功 ：" + categoryDataRequest.toString());
                        tvDataAbout.append("accept : 获取数据成功 ：" + categoryDataRequest.toString());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e("-----", "accept : 获取数据失败 ：" + throwable.getMessage());
                        tvDataAbout.append("accept : 获取数据失败 ：" + throwable.getMessage());
                    }
                });
    }


    @OnClick(R.id.btn_debounce)
    void onViewClicked(View view) {
        // 禁止响应点击事件

    }

}
