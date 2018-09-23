package com.liuh.learn.sample1;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * RxJava 中 Flowable 的使用，涉及到背压策略（BackPressure），响应式拉取.
 * <p>
 * 在Flowable里默认有一个大小为128的水缸, 当上下游工作在不同的线程中时, 上游就会先把事件发送到这个水缸中, 因此, <br>
 * 下游虽然没有调用request, 但是上游在水缸中保存着这些事件, 只有当下游调用request时, 才从水缸里取出事件发给下游.
 * <p>
 * MissingBackpressureException 异常.
 * <p>
 * Observable<----------subscribe------->Observer(Consumer)<br>
 * <p>
 * Flowable<----------subscribe------->Subscriber
 * <p>
 * BackpressureStrateg有五种策略：MISSING，ERROR，BUFFER，DROP，LATEST。
 * <p>
 * Flowable在设计的时候采用了一种新的思路也就是 响应式拉取 的方式来更好的解决上下游流速不均衡的问题。
 * <p>
 * 可以把request当做是一种能力, 当成下游处理事件的能力, 下游能处理几个就告诉上游我要几个, 这样只要上游根据下游的处理能力来决定发送多少事件,
 * 就不会造成一窝蜂的发出一堆事件来, 从而导致OOM.
 */
public class MainActivity extends AppCompatActivity {

    private static Subscription mSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        demo_flowable();
    }

    @OnClick({R.id.btn_flowable_test, R.id.btn_flowable_test_synchro, R.id.btn_flowable_test_asynchronous,
            R.id.btn_flowable_test_asynchronous2, R.id.btn_flowable_test_readfile_from_sd})
    void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_flowable_test:
                request(96);
                break;
            case R.id.btn_flowable_test_synchro:
                demo_flowable_synchro();
                break;
            case R.id.btn_flowable_test_asynchronous:
                demo_flowable_asynchronous();
                break;
            case R.id.btn_flowable_test_asynchronous2:
                demo_flowable_asynchronous2();
                break;
            case R.id.btn_flowable_test_readfile_from_sd:
                practice1();
                break;
        }
    }

    private static void request(int count) {
        mSubscription.request(count);
    }

    private static void demo_flowable() {
        Flowable.create(new FlowableOnSubscribe<Integer>() {
            @Override
            public void subscribe(FlowableEmitter<Integer> emitter) throws Exception {
                emitter.onNext(1);
                emitter.onNext(2);
                emitter.onNext(3);
                emitter.onNext(4);
                emitter.onComplete();
            }
        }, BackpressureStrategy.ERROR)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onSubscribe(Subscription s) {
                        mSubscription = s;
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.e("---", "onNext: " + integer);
                    }

                    @Override
                    public void onError(Throwable t) {
                        Log.e("---", "onError: " + t.getLocalizedMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.e("---", "onComplete.");
                    }
                });
    }

    /**
     * 同步的，即上游和下游同在主线程中
     */
    private static void demo_flowable_synchro() {
        Flowable.create(new FlowableOnSubscribe<Integer>() {
            @Override
            public void subscribe(FlowableEmitter<Integer> emitter) throws Exception {
                Log.e("---", "brfore emit, requested= " + emitter.requested());

                Log.e("---", "emit 1");
                emitter.onNext(1);
                Log.e("---", "after emit 1,request= " + emitter.requested());

                Log.e("---", "emit 2");
                emitter.onNext(2);
                Log.e("---", "after emit 2,request= " + emitter.requested());

                Log.e("---", "emit 3");
                emitter.onNext(3);
                Log.e("---", "after emit 3,request= " + emitter.requested());

                Log.e("---", "emit complete");
                emitter.onComplete();

                Log.e("---", "after emit complete,request= " + emitter.requested());
            }
        }, BackpressureStrategy.ERROR)
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onSubscribe(Subscription s) {
                        mSubscription = s;
                        s.request(2);
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.e("---", "onNext: " + integer);
                    }

                    @Override
                    public void onError(Throwable t) {
                        Log.e("---", "onError: " + t);
                    }

                    @Override
                    public void onComplete() {
                        Log.e("---", "onComplete");
                    }
                });
    }

    /**
     * 异步的
     * <p>
     * 因为异步的话，上游和下游之间有一个缓冲池，即作者说的水缸，容量为128.
     * <p>
     * 设置上游requested的值的这个内部调用会在合适的时候自动触发，那到底什么时候是合适的时候呢？
     * <p>
     * 作者演示的时：当下游消费掉96个事件之后，即我们调用了request(96)后，上游才回继续发送事件。
     */
    private static void demo_flowable_asynchronous() {
        Flowable.create(new FlowableOnSubscribe<Integer>() {
            @Override
            public void subscribe(FlowableEmitter<Integer> emitter) throws Exception {
                Log.e("---", "brfore emit, requested= " + emitter.requested());

                Log.e("---", "emit 1");
                emitter.onNext(1);
                Log.e("---", "after emit 1,request= " + emitter.requested());

                Log.e("---", "emit 2");
                emitter.onNext(2);
                Log.e("---", "after emit 2,request= " + emitter.requested());

                Log.e("---", "emit 3");
                emitter.onNext(3);
                Log.e("---", "after emit 3,request= " + emitter.requested());

                Log.e("---", "emit complete");
                emitter.onComplete();

                Log.e("---", "after emit complete,request= " + emitter.requested());
            }
        }, BackpressureStrategy.ERROR)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onSubscribe(Subscription s) {
                        mSubscription = s;
                        s.request(2);
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.e("---", "onNext: " + integer);
                    }

                    @Override
                    public void onError(Throwable t) {
                        Log.e("---", "onError: " + t);
                    }

                    @Override
                    public void onComplete() {
                        Log.e("---", "onComplete");
                    }
                });
    }

    private static void demo_flowable_asynchronous2() {
        Flowable.create(new FlowableOnSubscribe<Integer>() {
            @Override
            public void subscribe(FlowableEmitter<Integer> emitter) throws Exception {
                boolean flag;

                for (int i = 0; ; i++) {
                    flag = false;
                    while (emitter.requested() == 0) {
                        if (!flag) {
                            Log.e("---", "我不能再发送事件了");
                            flag = true;
                        }
                    }
                    emitter.onNext(i);
                    Log.e("---", "emitter : " + i + ", request= " + emitter.requested());
                }


            }
        }, BackpressureStrategy.ERROR).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onSubscribe(Subscription s) {
                        mSubscription = s;
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.e("---", "onNext: " + integer);
                    }

                    @Override
                    public void onError(Throwable t) {
                        Log.e("---", "onError: " + t);
                    }

                    @Override
                    public void onComplete() {
                        Log.e("---", "onComplete");
                    }
                });
    }


    public void practice1() {
        Flowable.create(new FlowableOnSubscribe<String>() {
            @Override
            public void subscribe(FlowableEmitter<String> emitter) throws Exception {

                try {

                    InputStream inputStream = getResources().openRawResource(R.raw.kangqiao);
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "gbk");
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                    String str;

                    while ((str = bufferedReader.readLine()) != null && !emitter.isCancelled()) {
                        while (emitter.requested() == 0) {
                            if (emitter.isCancelled()) {
                                break;
                            }
                        }
                        emitter.onNext(str);
                    }
                    bufferedReader.close();
                    inputStreamReader.close();
                    inputStream.close();

                    emitter.onComplete();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, BackpressureStrategy.ERROR)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onSubscribe(Subscription s) {
                        mSubscription = s;
                        mSubscription.request(1);
                    }

                    @Override
                    public void onNext(String s) {
                        System.out.println(s);
                        try {
                            Thread.sleep(2000);
                            mSubscription.request(1);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable t) {
                        System.out.println(t);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

}
