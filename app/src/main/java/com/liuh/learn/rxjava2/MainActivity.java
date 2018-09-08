package com.liuh.learn.rxjava2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

    }

    @OnClick({R.id.btn_test_rx_create, R.id.btn_test_rx_map, R.id.btn_test_rx_zip, R.id.btn_test_rx_concat,
            R.id.btn_test_rx_flatmap, R.id.btn_test_rx_concatmap})
    void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_test_rx_create:
                rxExample_create();
                break;
            case R.id.btn_test_rx_map:
                rxExample_map();
                break;
            case R.id.btn_test_rx_zip:
                rxExample_zip();
                break;
            case R.id.btn_test_rx_concat:
                rxExample_concat();
                break;
            case R.id.btn_test_rx_flatmap:
                rxExample_flatmap();
                break;
            case R.id.btn_test_rx_concatmap:
                rxExample_concatmap();
                break;
        }
    }

    /**
     * create
     * <p>
     * 用于产生一个 Observable 被观察者对象
     * <p>
     * 在发射了数值 3 之后，直接调用了 e.onComlete()，虽然无法接收事件，但发送事件还是继续的
     * <p>
     * 2.x 中有一个 Disposable 概念，这个东西可以直接调用切断，可以看到，当它的 isDisposed() 返回为 false 的时候，接收器能正常接收事件，
     * 但当其为 true 的时候，接收器停止了接收。所以可以通过此参数动态控制接收事件。
     */
    private void rxExample_create() {

        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {

                Log.e("-------", "Observable emit 1 \n");
                e.onNext(1);

                Log.e("-------", "Observable emit 2 \n");
                e.onNext(2);

                Log.e("-------", "Observable emit 3 \n");
                e.onNext(3);
                e.onComplete();

                Log.e("-------", "Observable emit 4 \n");
                e.onNext(4);
            }
        }).subscribe(new Observer<Integer>() {
            private int i;
            private Disposable mDisposable;

            @Override
            public void onSubscribe(Disposable d) {
                Log.e("-------", "onSubscribe : " + d.isDisposed() + "\n");
                mDisposable = d;
            }

            @Override
            public void onNext(Integer integer) {
                Log.e("-------", "onNext : value : " + integer + "\n");
                i++;
                if (i == 2) {
                    mDisposable.dispose();
                    Log.e("-------", "onNext : isDisposable : " + mDisposable.isDisposed() + "\n");
                }
            }

            @Override
            public void onError(Throwable e) {
                Log.e("-------", "onError : value : " + e.getMessage() + "\n");
            }

            @Override
            public void onComplete() {
                Log.e("-------", "onComplete\n");
            }
        });
    }

    /**
     * map
     * <p>
     * 对发射器发射的每一个事件应用一个函数，每一个事件都会按照指定的函数去变化。
     * <p>
     * map 基本作用就是将一个 Observable 通过某种函数关系，转换为另一种 Observable，这个例子中就是把我们的 Integer 数据变成了 String 类型
     */
    private void rxExample_map() {

        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                e.onNext(1);
                e.onNext(2);
                e.onNext(3);
            }
        }).map(new Function<Integer, String>() {

            @Override
            public String apply(Integer integer) throws Exception {
                return "This is result " + integer;
            }
        }).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(String s) {
                Log.e("-------", "onNext: " + s);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    /**
     * zip
     * <p>
     * zip 专用于合并事件，该合并不是连接（连接有另外一个操作符），而是两两配对，也就意味着，最终配对出的 Observable 发射事件数目只和少的那个相同。
     * <p>
     * zip 组合事件的过程就是分别从发射器 A 和发射器 B 各取出一个事件来组合，并且一个事件只能被使用一次，组合的顺序是严格按照事件发送的顺序来进行的，
     * 所以上面截图中，可以看到，1 永远是和 A 结合的，2 永远是和 B 结合的。
     */
    private void rxExample_zip() {
        Observable.zip(getStringObservable(), getIntegerObservable(), new BiFunction<String, Integer, String>() {

            @Override
            public String apply(String s, Integer integer) throws Exception {
                return s + integer;
            }
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                Log.e("-------", "zip :accept : " + s + "\n");
            }
        });
    }

    private Observable<String> getStringObservable() {
        return Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                if (!e.isDisposed()) {
                    Log.e("-------", "String emit :A \n");
                    e.onNext("A");
                    Log.e("-------", "String emit :B \n");
                    e.onNext("B");
                    Log.e("-------", "String emit :C \n");
                    e.onNext("C");
                }
            }
        });
    }

    private Observable<Integer> getIntegerObservable() {
        return Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                if (!e.isDisposed()) {
                    Log.e("-------", "String emit :1 \n");
                    e.onNext(1);
                    Log.e("-------", "String emit :2 \n");
                    e.onNext(2);
                    Log.e("-------", "String emit :3 \n");
                    e.onNext(3);
                    Log.e("-------", "String emit :4 \n");
                    e.onNext(4);
                    Log.e("-------", "String emit :5 \n");
                    e.onNext(5);
                }
            }
        });
    }

    /**
     * concat
     * <p>
     * 把两个发射器连接成一个发射器
     * <p>
     * 第二个发射器把自己的孩子交给了第一个发射器，事件发射是有顺序的
     */
    private void rxExample_concat() {
        Observable.concat(Observable.just(1, 2, 3), Observable.just(4, 5, 6)).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {

                Log.e("-------", "concat : " + integer + "\n");
            }
        });
    }

    /**
     * flatMap
     * <p>
     * flatMap是一个很有趣的操作符。可以把一个发射器  Observable 通过某种方法转换为多个 Observables，然后再把这些分散的 Observables装进一个单一的发射器 Observable。<br>
     * 需要注意的是，flatMap 并不能保证事件的顺序，如果需要保证，需要用到ConcatMap。
     */
    private void rxExample_flatmap() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                e.onNext(1);
                e.onNext(2);
                e.onNext(3);
            }
        }).flatMap(new Function<Integer, ObservableSource<String>>() {
            @Override
            public ObservableSource<String> apply(Integer integer) throws Exception {
                List<String> list = new ArrayList<>();
                for (int i = 0; i < 3; i++) {
                    list.add("I am value " + integer);
                }
                int delayTime = (int) (1 + Math.random() * 10);
                return Observable.fromIterable(list).delay(0, TimeUnit.MILLISECONDS);
            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        Log.e("-------", "flatMap : accept : " + s + "\n");
                    }
                });
    }

    /**
     * concatMap
     * <p>
     * concatMap 与 flatMap 的唯一区别就是 concatMap 保证了顺序
     */
    private void rxExample_concatmap() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                emitter.onNext(1);
                emitter.onNext(2);
                emitter.onNext(3);
            }
        }).concatMap(new Function<Integer, ObservableSource<String>>() {
            @Override
            public ObservableSource<String> apply(Integer integer) throws Exception {
                List<String> list = new ArrayList<>();
                for (int i = 0; i < 3; i++) {
                    list.add("I am value " + integer);
                }
                int delayTime = (int) (1 + Math.random() * 10);
                return Observable.fromIterable(list).delay(delayTime, TimeUnit.MILLISECONDS);
            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        Log.e("-------", "concatMap : accept : " + s + "\n");
                    }
                });
    }

}
