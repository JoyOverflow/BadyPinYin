package ouyj.hyena.com.bottleflip;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    Button flip_button;
    ImageView bottle_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        flip_button = findViewById(R.id.flipButton);
        bottle_image = findViewById(R.id.bottle);

        //按钮事件设置
        flip_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //设置旋转动画
                RotateAnimation rotateAnimation = new RotateAnimation(
                        0,
                        2160,
                        RotateAnimation.RELATIVE_TO_SELF,
                        .5f,
                        RotateAnimation.RELATIVE_TO_SELF,
                        0.5f
                );
                rotateAnimation.setDuration(2000);
                bottle_image.startAnimation(rotateAnimation);
            }
        });
    }







}
