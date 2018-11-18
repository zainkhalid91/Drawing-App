package smartboard.fyp.com.smartapp;

import android.content.Intent;
import android.graphics.Color;

import com.daimajia.androidanimations.library.Techniques;

import wail.splacher.com.splasher.lib.SplasherActivity;
import wail.splacher.com.splasher.models.SplasherConfig;
import wail.splacher.com.splasher.utils.Const;

public class SplashActivity extends SplasherActivity {
    @Override
    public void initSplasher(SplasherConfig config) {
        config.setReveal_start(Const.START_CENTER)
                //---------------
                .setAnimationDuration(3000)
                //---------------
                .setLogo(R.drawable.ic_splash_)
                .setLogo_animation(Techniques.FadeIn)
                .setAnimationLogoDuration(2000)
                .setLogoWidth(600)

                //---------------
                .setTitle("Smart Board App")
                .setTitleColor(Color.parseColor("#ffffff"))
                .setTitleAnimation(Techniques.FadeInUp)
                .setTitleSize(24)
                //---------------
                .setSubtitle("Welcome")
                .setSubtitleColor(Color.parseColor("#ffffff"))
                .setSubtitleAnimation(Techniques.FadeIn)
                .setSubtitleSize(16);
        //---------------
        // .setSubtitleTypeFace(Typeface.createFromAsset(getAssets(),"diana.otf"))
        //   .setTitleTypeFace(Typeface.createFromAsset(getAssets(),"stc.otf"));

        //Example of custom view ..
       /* config.setCustomView(R.layout.custom_view)
                .setReveal_start(Const.START_CENTER)
                .setAnimationDuration(5000);
        getCustomView().findViewById(R.id.textView)*/

    }

    @Override
    public void onSplasherFinished() {
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}



