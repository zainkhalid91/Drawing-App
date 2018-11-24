package smartboard.fyp.com.smartapp;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.itsrts.pptviewer.PPTViewer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static android.content.ContentValues.TAG;

public class PPTActivity extends AppCompatActivity {
    final int REQUEST_PERMISSION_ID = 10;
    PPTViewer pptViewer;
    private String path = null;

    /**
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri     The Uri to query.
     * @author paulburke
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String getPath(final Context context, final Uri uri) {

        Log.d(TAG + " File -",
                "Authority: " + uri.getAuthority() +
                        ", Fragment: " + uri.getFragment() +
                        ", Port: " + uri.getPort() +
                        ", Query: " + uri.getQuery() +
                        ", Scheme: " + uri.getScheme() +
                        ", Host: " + uri.getHost() +
                        ", Segments: " + uri.getPathSegments().toString()
        );

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // LocalStorageProvider
	/*		if (isLocalStorageDocument(uri)) {
				// The path is the id
				return DocumentsContract.getDocumentId(uri);
			}
	*/        // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                } else {
                    String sdpath = null;
                    Boolean isSDPresent = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);

                    if (new File("/storage/extSdCard/").exists()) {
                        sdpath = "/storage/extSdCard/";
                        Log.i("Sd Cardext Path", sdpath);
                    }
                    if (isSDPresent) {
                        File extdir = Environment.getExternalStorageDirectory();
                        File stats = new File(extdir.getAbsolutePath());
                        sdpath = extdir.getAbsolutePath() + "/";
                        Log.i("Sd Card1 Path", sdpath);
                    }

                    if (new File(sdpath + split[1]).exists()) {
                        return sdpath + split[1];
                    } else {
                        return null;
                    }
/*
					Log.i ("EXT", sdpath + split[1]);
					return sdpath + split[1];*/
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }



   /* @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_PERMISSION_ID:
                if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    String path = null;
                    Intent i = getIntent();
                    if (i != null) {
                        Uri uri = i.getData();
                        if (uri != null) {
                            Log.d(TAG, "uri.getPath: " + uri.getPath());
                            path = Environment.getExternalStorageDirectory() + File.separator + "presentyourppt.ppt";
                            try {
                                InputStream inputStream = this.getContentResolver().openInputStream(uri);
                                CopyRAWtoSDCard(inputStream, path);
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            path = Environment.getExternalStorageDirectory() + File.separator + "presentyourppt.ppt";
                            try {
                                InputStream in = getResources().openRawResource(R.raw.presentyourppt);
                                CopyRAWtoSDCard(in, path);
                            } catch (IOException e) {
                                e.printStackTrace();
                                Log.e("Error", e.getMessage());
                            }
                            //path = Uri.parse(path).getPath();
                            //path = i.getStringExtra("dst");//"/sdcard/talkaboutjvm.pptx";
                *//*if(TextUtils.isEmpty(path)){
//                    Toast.makeText(this,"Path is Empty!!", Toast.LENGTH_LONG).show();
//                    finish();return;
                    path="/sdcard/aaa/example.pptx";
                }*//*
                            File demoFile = new File(path);
                            if (!demoFile.exists()) {
                                Toast.makeText(this, path + " not exist!", Toast.LENGTH_LONG).show();
                                finish();
                                return;
                            }
                        }
                    }


                    pptViewer.setNext_img(R.drawable.next).setPrev_img(R.drawable.prev)
                            .setSettings_img(R.drawable.settings)
                            .setZoomin_img(R.drawable.zoomin)
                            .setZoomout_img(R.drawable.zoomout);
                    pptViewer.loadPPT(this, path);


                }
        }
    }*/

    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ppt);

        boolean permissionsAvailable = false;

        int storagePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        permissionsAvailable = storagePermission == PackageManager.PERMISSION_GRANTED;

        pptViewer = findViewById(R.id.pptviewer);


        pptViewer.setNext_img(R.drawable.ic_skip_next_black_24dp).setPrev_img(R.drawable.ic_skip_previous_black_24dp)
                .setSettings_img(R.drawable.ic_settings_power_black_24dp)
                .setZoomin_img(R.drawable.ic_zoom_in_black_24dp)
                .setZoomout_img(R.drawable.ic_zoom_out_black_24dp);
        pptViewer.loadPPT(this, path);
    }

    private void CopyRAWtoSDCard(InputStream inputStream, String path) throws IOException {
        FileOutputStream out = new FileOutputStream(path);
        byte[] buff = new byte[1024];
        int read = 0;
        try {
            while ((read = inputStream.read(buff)) > 0) {
                out.write(buff, 0, read);
            }
        } finally {
            inputStream.close();
            out.close();
        }
    }

}

