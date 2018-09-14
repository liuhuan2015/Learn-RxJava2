package com.liuh.learn.rxjava2.usecase;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.liuh.learn.rxjava2.R;
import com.liuh.learn.rxjava2.model.CategoryDataRequest;
import com.liuh.learn.rxjava2.model.MobileAddress;
import com.liuh.learn.rxjava2.net.NetWork;
import com.rx2androidnetworking.Rx2AndroidNetworking;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * 结合多个接口的数据更新UI
 * <p>
 * 在实际应用中，极有可能在一个页面显示的数据来源于多个接口，这时候可以使用 zip 操作符。
 * <p>
 * zip 操作符可以将多个 Observable 的数据结合为一个数据源再发射出去。
 */
public class RxCaseZipActivity extends AppCompatActivity {

    @BindView(R.id.tv_data_about_zip)
    TextView tvDataAboutZip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rx_case_zip);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btn_updateUI_with_multi_interface)
    void updateUIWithMultiInterface(View view) {
        // 接口失效了
//        Observable<MobileAddress> observable1 = Rx2AndroidNetworking
//                .get("http://api.avatardata.cn/MobilePlace/LookUp?key=ec47b85086be4dc8b5d941f5abd37a4e&mobileNumber=18503069172")
//                .build()
//                .getObjectObservable(MobileAddress.class);

        Observable<CategoryDataRequest> observable1 = Rx2AndroidNetworking
                .get("http://gank.io/api/data/福利/2/1")
                .build()
                .getObjectObservable(CategoryDataRequest.class);

        Observable<CategoryDataRequest> observable2 = NetWork.getGankApi()
                .getCategoryData("Android", 2, 1);

        Observable.zip(observable1, observable2, new BiFunction<CategoryDataRequest, CategoryDataRequest, String>() {
            @Override
            public String apply(CategoryDataRequest categoryDataRequest1, CategoryDataRequest categoryDataRequest2) throws Exception {
                return "合并后的数据为：接口1 的描述信息：" + categoryDataRequest1.getResults().get(0).getDesc() + " 接口2 的人名：" + categoryDataRequest2.getResults().get(0).getWho();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        Log.e("-----", "成功： " + s + "\n");
                        tvDataAboutZip.append("成功： " + s + "\n");
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e("-----", "失败：" + throwable.getMessage() + "\n");
                        tvDataAboutZip.append("失败：" + throwable.getMessage() + "\n");
                    }
                });
    }

}
