package smartboard.fyp.com.smartapp;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.File;

public class MaterialActivity extends AppCompatActivity {

    private Button mSelectFile;
    private Button mPause;
    private Button mCancel;
    private ProgressBar mProgress;
    private TextView mFileNameLable;
    private TextView mSizeLable;
    private TextView mProgressLable;

    private StorageReference mStorageRef;

    private StorageTask mStoragetask;

    private final static int FILE_SELECT_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_material);


        mSelectFile = findViewById(R.id.btn_selectfiletoupload);
        mCancel = findViewById(R.id.btn_cancel);
        mProgress = findViewById(R.id.upload_progress);
        mPause = findViewById(R.id.btn_pause);
        mFileNameLable = findViewById(R.id.filename_label);

        mProgressLable = findViewById(R.id.progress_label);
        mSizeLable = findViewById(R.id.size_label);
        mStorageRef = FirebaseStorage.getInstance().getReference();


        mSelectFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileSelector();
            }
        });


        mPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String btnText = mPause.getText().toString();

                if (btnText.equals("Pause Upload")) {
                    try {
                        Toast.makeText(getApplicationContext(), "Please select a file first", Toast.LENGTH_SHORT).show();
                    } catch (Exception ex) {

                    }
                    mStoragetask.pause();
                    mPause.setText("Resume Upload");

                } else {
                    mStoragetask.resume();
                    mPause.setText("Pause Upload");
                }
            }
        });

        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mStoragetask.cancel();
                try {
                    Toast.makeText(getApplicationContext(), "Please select a file first", Toast.LENGTH_SHORT).show();
                } catch (Exception ex) {

                }
                startActivity(new Intent(MaterialActivity.this, MaterialActivity.class));

            }
        });

    }


    private void openFileSelector() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Select a file to upload"), FILE_SELECT_CODE);

        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(MaterialActivity.this, "Please install a File Manager.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == FILE_SELECT_CODE && resultCode == RESULT_OK) {

            Uri fileUri = data.getData();


            String uriString = fileUri.toString();

            File myFile = new File(uriString);
            //String path = myFile.getAbsolutePath();

            String displayName = null;

            if (uriString.startsWith("content://")) {
                Cursor cursor = null;
                try {
                    cursor = MaterialActivity.this.getContentResolver().query(fileUri, null, null, null, null);
                    if (cursor != null && cursor.moveToFirst()) {
                        displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                    }
                } finally {
                    cursor.close();
                }

            } else if (uriString.startsWith("file://")) {
                displayName = myFile.getName();
            }

            mFileNameLable.setText(displayName);
            final String fileName = System.currentTimeMillis() + ".pdf";
            StorageReference riversRef = mStorageRef.child("files/" + displayName);

            mStoragetask = riversRef.putFile(fileUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Get a URL to the uploaded content
                            Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!urlTask.isSuccessful()) ;
                            Uri downloadUrl = urlTask.getResult();

                            Toast.makeText(MaterialActivity.this, "File Uploaded.", Toast.LENGTH_SHORT).show();

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Toast.makeText(MaterialActivity.this, "There was an error while Uploading the file.", Toast.LENGTH_SHORT).show();

                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            mProgress.setProgress((int) progress);

                            String progressText = taskSnapshot.getBytesTransferred() / (1024 * 1024) + " / " + taskSnapshot.getTotalByteCount() / (1024 * 1024) + " mb";
                            mSizeLable.setText(progressText);
                            mProgressLable.setText((int) progress + "٪");
                        }
                    });


        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
