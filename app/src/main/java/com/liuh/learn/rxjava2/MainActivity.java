package com.liuh.learn.rxjava2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.btn_rx_operators, R.id.btn_a_simple_http_request, R.id.btn_http_request_concat,
            R.id.btn_http_request_flatmap, R.id.btn_http_request_zip, R.id.btn_http_request_interval,
            R.id.btn_http_request_debounce, R.id.btn_thread_scheduler})
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

        }
    }
}
