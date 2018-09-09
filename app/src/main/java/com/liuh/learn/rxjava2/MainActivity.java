package com.liuh.learn.rxjava2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

    }

    @OnClick({R.id.btn_test_rx_create, R.id.btn_test_rx_map, R.id.btn_test_rx_zip, R.id.btn_test_rx_concat,
            R.id.btn_test_rx_flatmap, R.id.btn_test_rx_concatmap, R.id.btn_test_rx_distinct, R.id.btn_test_rx_filter,
            R.id.btn_test_rx_buffer, R.id.btn_test_rx_timer, R.id.btn_test_rx_interval, R.id.btn_test_rx_doonnext,
            R.id.btn_test_rx_skip, R.id.btn_test_rx_take, R.id.btn_test_rx_just, R.id.btn_test_rx_single,
            R.id.btn_test_rx_debounce, R.id.btn_test_rx_defer, R.id.btn_test_rx_last, R.id.btn_test_rx_merge,
            R.id.btn_test_rx_reduce, R.id.btn_test_rx_scan})
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
            case R.id.btn_test_rx_distinct:
                rxExample_distinct();
                break;
            case R.id.btn_test_rx_filter:
                rxExample_filter();
                break;
            case R.id.btn_test_rx_buffer:
                rxExample_buffer();
                break;
            case R.id.btn_test_rx_timer:
                rxExample_timer();
                break;
            case R.id.btn_test_rx_interval:
                rxExample_interval();
                break;
            case R.id.btn_test_rx_doonnext:
                rxExample_doonnext();
                break;
            case R.id.btn_test_rx_skip:
                rxExample_skip();
                break;
            case R.id.btn_test_rx_take:
                rxExample_take();
                break;
            case R.id.btn_test_rx_just:
                rxExample_just();
                break;
            case R.id.btn_test_rx_single:
                rxExample_single();
                break;
            case R.id.btn_test_rx_debounce:
                rxExample_debounce();
                break;
            case R.id.btn_test_rx_defer:
                rxExample_defer();
                break;
            case R.id.btn_test_rx_last:
                rxExample_last();
                break;
            case R.id.btn_test_rx_merge:
                rxExample_merge();
                break;
            case R.id.btn_test_rx_reduce:
                rxExample_reduce();
                break;
            case R.id.btn_test_rx_scan:
                rxExample_scan();
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

    /**
     * distinct
     * <p>
     * 去重操作符
     */
    private void rxExample_distinct() {
        Observable.just(1, 1, 1, 2, 2, 3, 4, 5)
                .distinct()
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.e("-------", "distinct : " + integer + "\n");
                    }
                });
    }

    /**
     * filter
     * <p>
     * 过滤操作符
     */
    private void rxExample_filter() {
        Observable.just(1, 20, 65, -5, 7, 19)
                .filter(new Predicate<Integer>() {
                    @Override
                    public boolean test(Integer integer) throws Exception {
                        return integer >= 10;
                    }
                }).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                Log.e("-------", "filter : " + integer + "\n");
            }
        });
    }

    /**
     * buffer
     * <p>
     * buffer 操作符接受两个参数，buffer(count,skip)，作用是将 Observable 中的数据按 skip (步长) 分成最大不超过 count 的 buffer ，然后生成一个  Observable
     * <p>
     * 输出依次为123，345，5
     */
    private void rxExample_buffer() {
        Observable.just(1, 2, 3, 4, 5)
                .buffer(3, 2)
                .subscribe(new Consumer<List<Integer>>() {
                    @Override
                    public void accept(List<Integer> integers) throws Exception {
                        Log.e("-------", "buffer size : " + integers.size() + "\n");

                        Log.e("-------", "buffer value : ");

                        for (Integer i : integers) {
                            Log.e("-------", i + "");
                        }
                    }
                });
    }

    /**
     * timer
     * <p>
     * 相当于一个定时任务。在 1.x 中它还可以执行间隔逻辑，但在 2.x 中此功能被交给了 interval。
     * <p>
     * 需要注意的是，timer 和 interval 均默认在新线程
     */
    private void rxExample_timer() {
        Log.e("-------", "startTime: " + TimeUtils.millis2String(System.currentTimeMillis()));
        Observable.timer(2, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())// timer 默认在新线程，所以需要切换回主线程
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        Log.e("-------", "timer: " + aLong + "  " + TimeUtils.millis2String(System.currentTimeMillis()));
                    }
                });
    }

    /**
     * interval
     * <p>
     * 用于间隔时间执行某个操作，接受三个参数，分别是第一次发送延迟，间隔时间，时间单位
     * <p>
     * 需要处理的问题：如何关闭？
     */

    private Disposable mDisposable;

    private void rxExample_interval() {
        Log.e("-------", "startTime: " + TimeUtils.millis2String(System.currentTimeMillis()));
        mDisposable = Observable.interval(3, 2, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        Log.e("-------", "interval: aLong: " + aLong + "  "
                                + TimeUtils.millis2String(System.currentTimeMillis()));
                    }
                });
    }

    /**
     * doOnNext
     * <p>
     * 应该不算是一个操作符，但是比较常用。作用是让订阅者在接收到数据之前干点有意思的事情，假如我们在获取到数据之前想先保存一下它。
     */
    private void rxExample_doonnext() {
        Observable.just(1, 2, 3, 4)
                .doOnNext(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.e("-------", "保存" + integer + "成功");
                    }
                }).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                Log.e("-------", "accept : " + integer);
            }
        });
    }

    /**
     * skip
     * <p>
     * 作用和字面意思一样，接受一个long型的参数count，表示跳过count个数目开始接受事件
     */
    private void rxExample_skip() {
        Observable.just(1, 2, 3, 4, 5)
                .skip(2)
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.e("-------", "skip : " + integer + "\n");
                    }
                });
    }

    /**
     * take
     * <p>
     * 接受一个 long 型参数 count ，表示至多接收 count 个数据。
     */
    private void rxExample_take() {
        Flowable.fromArray(1, 2, 3, 4, 5)
                .take(3)
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.e("-------", "accept : take : " + integer);
                    }
                });
    }

    /**
     * just
     * <p>
     * 是一个简单的发射器依次调用onNext方法的操作符
     */
    private void rxExample_just() {
        Observable.just(1, 2, 3)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.e("-------", "accept : just : " + integer);
                    }
                });
    }

    /**
     * single
     * <p>
     * 只会接受一个参数，SingleObserver 只会调用 onError() 或者 onSuccess()
     */
    private void rxExample_single() {
        Single.just(new Random().nextInt())
                .subscribe(new SingleObserver<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(Integer integer) {
                        Log.e("-------", "single : onSuccess : " + integer + "\n");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("-------", "single : onError : " + e.getMessage() + "\n");
                    }
                });
    }

    /**
     * debounce
     * <p>
     * 去除发送频率过快的项，很有用的
     * <p>
     * 下面的代码会去除发送间隔时间小于 500 毫秒的发射事件，所以 1 和 3 被去掉了
     */
    private void rxExample_debounce() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                emitter.onNext(1);
                Thread.sleep(400);
                emitter.onNext(2);
                Thread.sleep(505);
                emitter.onNext(3);
                Thread.sleep(100);
                emitter.onNext(4);
                Thread.sleep(605);
                emitter.onNext(5);
                Thread.sleep(510);
                emitter.onComplete();
            }
        }).debounce(500, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.e("-------", "debounce : accept : " + integer);
                    }
                });
    }

    /**
     * defer
     * <p>
     * 简单的说，就是每次订阅都会创建一个新的Observable，如果没有被订阅，就不会产生新的Observable
     */
    private void rxExample_defer() {

        Observable<Integer> observable = Observable.defer(new Callable<ObservableSource<Integer>>() {
            @Override
            public ObservableSource<Integer> call() throws Exception {
                return Observable.just(1, 2, 3);
            }
        });

        observable.subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Integer integer) {
                Log.e("-------", "defer : " + integer + "\n");
            }

            @Override
            public void onError(Throwable e) {
                Log.e("-------", "defer : onError : " + e.getMessage() + "\n");
            }

            @Override
            public void onComplete() {
                Log.e("-------", "defer : onComplete\n");
            }
        });
    }

    /**
     * last
     * <p>
     * 仅取出可观察到的最后一个值，或者是满足某些条件的最后一项。last接收的参数是一个默认值，表示发射器什么也没有发射的时候的默认值
     * Returns a Single that emits only the last item emitted by this Observable, or a default item if this Observable completes without emitting any items.
     */
    private void rxExample_last() {
        Observable.just(1, 2, 3)
                .last(4)
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.e("-------", "last : accept : " + integer + "\n");
                    }
                });
    }

    /**
     * merge 合并
     * <p>
     * 作用是把多个 Observable 结合起来，接受可变参数，也支持迭代器集合。它和 concat 的区别在于，不用等到 发射器 A 发送完所有的事件再进行发射器 B 的发送。
     */
    private void rxExample_merge() {
        Observable.merge(Observable.just(1, 2), Observable.just(3, 4, 5))
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.e("-------", "accept : merge : " + integer + "\n");
                    }
                });
    }

    /**
     * reduce
     * <p>
     * 每次用一个方法处理一个值，可以有一个 seed 作为初始值
     */
    private void rxExample_reduce() {
        Observable.just(1, 2, 3)
                .reduce(new BiFunction<Integer, Integer, Integer>() {
                    @Override
                    public Integer apply(Integer integer, Integer integer2) throws Exception {
                        return integer + integer2;
                    }
                }).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                Log.e("-------", "accept : reduce : " + integer + "\n");
            }
        });
    }

    /**
     * scan
     * <p>
     * scan操作符的作用和reduce的作用一致，唯一区别是 reduce 只讲结果，不讲过程；而 scan 会始终如一地把每一个步骤都输出。
     */
    private void rxExample_scan() {
        Observable.just(1, 2, 3)
                .scan(new BiFunction<Integer, Integer, Integer>() {
                    @Override
                    public Integer apply(Integer integer, Integer integer2) throws Exception {
                        return integer + integer2;
                    }
                }).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                Log.e("-------", "accept : scan : " + integer + "\n");
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("-------", "onDestroy");
        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
        }
    }
}
