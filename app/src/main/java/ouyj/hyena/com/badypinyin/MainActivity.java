package ouyj.hyena.com.badypinyin;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //获取动作条引用（隐藏动作条）
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
    }
}
