package smartboard.fyp.com.smartapp

import android.content.Intent
import android.graphics.*
import com.daimajia.androidanimations.library.Techniques
import wail.splacher.com.splasher.lib.SplasherActivity
import wail.splacher.com.splasher.models.SplasherConfig
import wail.splacher.com.splasher.utils.Const

class SplashActivity : SplasherActivity() {
    override fun initSplasher(config: SplasherConfig) {
        config.setReveal_start(Const.START_CENTER) //---------------
            .setAnimationDuration(3000) //---------------
            .setLogo(R.drawable.ic_splash_)
            .setLogo_animation(Techniques.FadeIn)
            .setAnimationLogoDuration(2000)
            .setLogoWidth(600) //---------------
            .setTitle("Smart Board App")
            .setTitleColor(Color.parseColor("#ffffff"))
            .setTitleAnimation(Techniques.FadeInUp)
            .setTitleSize(24) //---------------
            .setSubtitle("Welcome")
            .setSubtitleColor(Color.parseColor("#ffffff"))
            .setSubtitleAnimation(Techniques.FadeIn).subtitleSize = 16
        //---------------
        // .setSubtitleTypeFace(Typeface.createFromAsset(getAssets(),"diana.otf"))
        //   .setTitleTypeFace(Typeface.createFromAsset(getAssets(),"stc.otf"));

        //Example of  view ..
        /*  config.setCustomView(R.layout.custom_splash_activity)
                .setAnimationDuration(3000);
        getCustomView().findViewById(R.id.spin_kit);*/
    }

    override fun onSplasherFinished() {
        val intent: Intent = Intent(this@SplashActivity, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}