package smartboard.fyp.com.smartapp;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.shockwave.pdfium.PdfDocument;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class PdfActivity extends AppCompatActivity implements OnPageChangeListener, OnLoadCompleteListener {
    PDFView pdfView;
    Integer pageNumber = 0;
    String pdfFileName;
    String TAG = "PdfActivity";
    int position = -1;
    private ImageView screenShotImageView, imageView;
    private View main;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf);
        screenShotImageView = findViewById(R.id.screenshot_imageview);
        imageView = findViewById(R.id.imageView);
        main = findViewById(R.id.main);
        init();


        screenShotImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap b = ScreenShot.takescreenshotOfRootView(imageView);
                imageView.setImageBitmap(b);
                main.setBackgroundColor(Color.parseColor("#999999"));
                saveBitmap(b);

            }
        });
    }


    private void init() {
        pdfView = findViewById(R.id.pdfView);
        position = getIntent().getIntExtra("position", -1);
        displayFromSdcard();
    }

    private void displayFromSdcard() {
        pdfFileName = PdfViewer.fileList.get(position).getName();

        pdfView.fromFile(PdfViewer.fileList.get(position))
                .defaultPage(pageNumber)
                .enableSwipe(true)

                .swipeHorizontal(false)
                .onPageChange(this)
                .enableAnnotationRendering(true)
                .onLoad(this)
                .scrollHandle(new DefaultScrollHandle(this))
                .load();
    }

    private void saveBitmap(Bitmap bitmap) {

        try {
            File mFolder = new File(getFilesDir() + "/SmartApp"); //give a name for the folder
            File imagePath = new File(mFolder + "screenshot.png");
            if (!mFolder.exists()) {
                mFolder.mkdir();
            }
            if (!imagePath.exists()) {
                imagePath.createNewFile();
            }
            FileOutputStream fos = null;

            fos = new FileOutputStream(imagePath);
            // bitmap.compress(CompressFormat.PNG, 100, fos);
            bitmap.compress(Bitmap.CompressFormat.PNG, 60, fos);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            String encodedByte = Base64.encodeToString(byteArray, Base64.DEFAULT);
            Log.e("encodeByte", encodedByte);
            fos.flush();
            fos.close();

            MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "Screen", "screen");

        } catch (FileNotFoundException e) {
            Log.e("no file", e.getMessage(), e);
        } catch (IOException e) {
            Log.e("io", e.getMessage(), e);
        }

    }

    @Override
    public void onPageChanged(int page, int pageCount) {
        pageNumber = page;
        setTitle(String.format("%s %s / %s", pdfFileName, page + 1, pageCount));
    }


    @Override
    public void loadComplete(int nbPages) {
        PdfDocument.Meta meta = pdfView.getDocumentMeta();
        printBookmarksTree(pdfView.getTableOfContents(), "-");

    }

    public void printBookmarksTree(List<PdfDocument.Bookmark> tree, String sep) {
        for (PdfDocument.Bookmark b : tree) {

            Log.e(TAG, String.format("%s %s, p %d", sep, b.getTitle(), b.getPageIdx()));

            if (b.hasChildren()) {
                printBookmarksTree(b.getChildren(), sep + "-");
            }
        }
    }
}
