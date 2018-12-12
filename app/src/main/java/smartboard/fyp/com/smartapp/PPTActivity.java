package smartboard.fyp.com.smartapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.itsrts.pptviewer.PPTViewer;

public class PPTActivity extends AppCompatActivity {
    PPTViewer pptViewer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ppt);

        pptViewer = findViewById(R.id.pptviewer);

        /*String path = Environment.getExternalStorageDirectory().getAbsolutePath();*/
        pptViewer.setNext_img(R.drawable.ic_skip_next_black_24dp).setPrev_img(R.drawable.ic_skip_previous_black_24dp)
                .setSettings_img(R.drawable.ic_settings_power_black_24dp)
                .setZoomin_img(R.drawable.ic_zoom_in_black_24dp)
                .setZoomout_img(R.drawable.ic_zoom_out_black_24dp);
        pptViewer.loadPPT(this);
    }

}

