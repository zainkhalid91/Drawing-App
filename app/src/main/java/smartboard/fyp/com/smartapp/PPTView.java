package smartboard.fyp.com.smartapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;

public class PPTView extends AppCompatActivity {
    public static ArrayList<File> fileList = new ArrayList<File>();
    public static int REQUEST_PERMISSIONS = 1;
    ListView lv_ppt;
    PPTAdapter obj_ppt_adapter;
    boolean boolean_permission;
    File dir;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lv_ppt);
        init();
    }

    private void init() {

        lv_ppt = findViewById(R.id.lv_ppt);
        dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
        fn_permission();


        lv_ppt.setOnItemClickListener((adapterView, view, i, l) -> {
            Intent intent = new Intent(getApplicationContext(), PPTActivity.class);
            intent.putExtra("position", i);
            startActivity(intent);

          /*  Uri uri = Uri.fromFile(dir);
            Intent intent = new Intent(Intent.ACTION_VIEW);
                    if(dir.toString().contains(".ppt") || dir.toString().contains(".pptx")){
                        intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        getApplicationContext().startActivity(intent);

            }*/

            Log.e("Position", i + "");
        });
    }

    public ArrayList<File> getfile(File dir) {
        File listFile[] = dir.listFiles();
        if (listFile != null && listFile.length > 0) {
            for (int i = 0; i < listFile.length; i++) {

                if (listFile[i].isDirectory()) {
                    getfile(listFile[i]);

                } else {

                    boolean booleanppt = false;
                    if (listFile[i].getName().endsWith(".ppt")) {

                        for (int j = 0; j < fileList.size(); j++) {
                            if (fileList.get(j).getName().equals(listFile[i].getName())) {
                                booleanppt = true;
                            } else {

                            }
                        }

                        if (booleanppt) {
                            booleanppt = false;
                        } else {
                            fileList.add(listFile[i]);

                        }
                    }
                }
            }
        }
        return fileList;
    }

    private void fn_permission() {
        if ((ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {

            if ((ActivityCompat.shouldShowRequestPermissionRationale(PPTView.this, android.Manifest.permission.READ_EXTERNAL_STORAGE))) {


            } else {
                ActivityCompat.requestPermissions(PPTView.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_PERMISSIONS);

            }
        } else {
            boolean_permission = true;

            getfile(dir);

            obj_ppt_adapter = new PPTAdapter(getApplicationContext(), fileList);
            lv_ppt.setAdapter(obj_ppt_adapter);

        }
    }
}
