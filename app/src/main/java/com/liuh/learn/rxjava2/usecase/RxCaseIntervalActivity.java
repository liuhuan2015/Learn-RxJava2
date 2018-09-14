package com.liuh.learn.rxjava2.usecase;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.liuh.learn.rxjava2.R;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * 间隔任务实现心跳
 * <p>
 * 即时通讯等需要轮询任务的场景在如今的 app 中已经很常见，RxJava2 中的 interval 操作符可以处理这种场景。
 * <p>
 * 这里演示的是一个简单的轮询
 */
public class RxCaseIntervalActivity extends AppCompatActivity {

    @BindView(R.id.tv_data_about_interval)
    TextView tvDataAboutInterval;

    private Disposable mDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rx_case_interval);
        ButterKnife.bind(this);
    }


    @OnClick(R.id.btn_http_request_interval_task)
    void startIntervalTask(View view) {

        mDisposable = Flowable.interval(1, TimeUnit.SECONDS)
                .doOnNext(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        Log.e("-----", "accept: doOnNext : " + aLong);
                    }
                }).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        Log.e("-----", "accept: 设置文本 ：" + aLong);
                        tvDataAboutInterval.append("accept: 设置文本 : " + aLong + "\n");
                    }
                });
    }

    /**
     * 销毁时停止心跳
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDisposable != null) {
            mDisposable.dispose();
        }
    }
}
