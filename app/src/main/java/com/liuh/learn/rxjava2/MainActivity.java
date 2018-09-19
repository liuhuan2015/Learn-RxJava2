package com.liuh.learn.rxjava2;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.liuh.learn.rxjava2.usecase.RxCaseConcatActivity;
import com.liuh.learn.rxjava2.usecase.RxCaseDebounceActivity;
import com.liuh.learn.rxjava2.usecase.RxCaseFlatmapActivity;
import com.liuh.learn.rxjava2.usecase.RxCaseIntervalActivity;
import com.liuh.learn.rxjava2.usecase.RxCaseThreadSchedulerActivity;
import com.liuh.learn.rxjava2.usecase.RxCaseZipActivity;
import com.liuh.learn.rxjava2.usecase.RxOperatorsLearnActivity;
import com.liuh.learn.rxjava2.usecase.RxUseSimpleHttpRequestActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static java.lang.Thread.sleep;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.btn_rx_operators, R.id.btn_a_simple_http_request, R.id.btn_http_request_concat,
            R.id.btn_http_request_flatmap, R.id.btn_http_request_zip, R.id.btn_http_request_interval,
            R.id.btn_http_request_debounce, R.id.btn_thread_scheduler, R.id.btn_novel_reader,
            R.id.btn_complex_use})
    void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_rx_operators:
                startActivity(new Intent(this, RxOperatorsLearnActivity.class));
                break;
            case R.id.btn_a_simple_http_request:
                startActivity(new Intent(this, RxUseSimpleHttpRequestActivity.class));
                break;
            case R.id.btn_http_request_concat:
                startActivity(new Intent(this, RxCaseConcatActivity.class));
                break;
            case R.id.btn_http_request_flatmap:
                startActivity(new Intent(this, RxCaseFlatmapActivity.class));
                break;
            case R.id.btn_http_request_zip:
                startActivity(new Intent(this, RxCaseZipActivity.class));
                break;
            case R.id.btn_http_request_interval:
                startActivity(new Intent(this, RxCaseIntervalActivity.class));
                break;
            case R.id.btn_http_request_debounce:
                startActivity(new Intent(this, RxCaseDebounceActivity.class));
                break;
            case R.id.btn_thread_scheduler:
                startActivity(new Intent(this, RxCaseThreadSchedulerActivity.class));
                break;
            case R.id.btn_novel_reader:
                novelAndReader();
                break;
            case R.id.btn_complex_use:
                oneComplexUse();
                break;
        }
    }

    /**
     * 观察者模式(连载小说和读者)
     */
    private void novelAndReader() {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                emitter.onNext("连载1");
                sleep(3000);
                emitter.onNext("连载2");
                emitter.onNext("连载3");

            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(String s) {
                        Log.e("-----", "Observer : onNext : " + s);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("-----", "Observer : onError");
                    }

                    @Override
                    public void onComplete() {
                        Log.e("-----", "Observer : onComplete");
                    }
                });
    }


    private int[] drawables = {R.drawable.icon_1, R.drawable.icon_2, R.drawable.icon_3, R.drawable.icon_4, R.drawable.icon_5,
            R.drawable.icon_6, R.drawable.icon_7, R.drawable.icon_8, R.drawable.icon_9, R.drawable.icon_10};

    /**
     * 一个复杂一点的应用场景
     */
    private void oneComplexUse() {

        Observable.create(new ObservableOnSubscribe<Drawable>() {
            @Override
            public void subscribe(ObservableEmitter<Drawable> emitter) throws Exception {
                for (int i = 0; i < drawables.length; i++) {
                    Drawable drawable = getResources().getDrawable(drawables[i]);

                    // 第六张图片延时3秒后加载
                    if (5 == i) {
                        sleep(3000);
                    }

                    // 复制第七张图片到sd卡
                    if (6 == i) {
                        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                        saveBitmap(bitmap, "test.png", Bitmap.CompressFormat.PNG);
                    }

                    // 上传到网络
                    if (7 == i) {
                        uploadIcon(drawable);
                    }
                    emitter.onNext(drawable);
                }
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Drawable>() {
                    @Override
                    public void accept(Drawable drawable) throws Exception {
                        // 回调后在UI界面上展示出来

                    }
                });

    }

    private void saveBitmap(Bitmap bitmap, String s, Bitmap.CompressFormat png) {

    }

    private void uploadIcon(Drawable drawable) {

    }
}
