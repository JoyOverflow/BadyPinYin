package ouyj.hyena.com.badypinyin;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //获取动作条引用（设置动作条上的Logo图标）
        ActionBar actionBar = getSupportActionBar();
        actionBar.setLogo(R.mipmap.ic_launcher);
        //设置是否显示Logo
        actionBar.setDisplayUseLogoEnabled(true);
        //设置左上角图标是否显示（false则没有应用图标仅显示标题）
        actionBar.setDisplayShowHomeEnabled(true);


    }
}
