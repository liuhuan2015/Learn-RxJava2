package com.liuh.learn.rxjava2.usecase;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.liuh.learn.rxjava2.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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


    }

}
