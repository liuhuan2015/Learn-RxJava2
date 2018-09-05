package com.liuh.learn.rxjava2;

import android.util.Log;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Date: 2018/9/5 17:44
 * Description:
 */
public class TestClass {


    public static void main(String[] args) {

        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {

                e.onNext(1);
                Log.e("-------", "Observable emit 1 \n");

                e.onNext(2);
                Log.e("-------", "Observable emit 2 \n");

                e.onNext(3);
                Log.e("-------", "Observable emit 3 \n");

                e.onNext(4);
                Log.e("-------", "Observable emit 4 \n");

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

}
