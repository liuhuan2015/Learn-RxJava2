package com.liuh.learn.rxjava2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        ButterKnife.bind(this);

    }

    @OnClick({R.id.btn_rx_operators, R.id.btn_rx_use_example})
    void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_rx_operators:
                startActivity(new Intent(this, RxOperatorsLearnActivity.class));
                break;
            case R.id.btn_rx_use_example:
                startActivity(new Intent(this, RxUseExampleActivity.class));
                break;
        }
    }
}
