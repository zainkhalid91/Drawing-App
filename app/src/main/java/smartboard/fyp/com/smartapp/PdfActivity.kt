package smartboard.fyp.com.smartapp

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.github.barteksc.pdfviewer.PDFView
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle
import com.shockwave.pdfium.PdfDocument.Bookmark
import java.io.*

class PdfActivity : AppCompatActivity(), OnPageChangeListener, OnLoadCompleteListener {
    var pdfView: PDFView? = null
    var pageNumber = 0
    var pdfFileName: String? = null
    var TAG = "PdfActivity"
    var position = -1
    private lateinit var screenShotImageView: ImageView
    private lateinit var imageView: ImageView
    private lateinit var main: View
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pdf)
        screenShotImageView = findViewById(R.id.screenshot_imageview)
        imageView = findViewById(R.id.imageView)
        main = findViewById(R.id.main)
        init()
        screenShotImageView.setOnClickListener(View.OnClickListener {
            val b = ScreenShot.takescreenshotOfRootView(imageView)
            imageView.setImageBitmap(b)
            main.setBackgroundColor(Color.parseColor("#999999"))
            saveBitmap(b)
        })
    }

    private fun init() {
        pdfView = findViewById(R.id.pdfView)
        position = intent.getIntExtra("position", -1)
        displayFromSdcard()
    }

    private fun displayFromSdcard() {
        pdfFileName = PdfViewer.fileList.get(position).name
        pdfView!!.fromFile(PdfViewer.fileList.get(position))
            .defaultPage(pageNumber)
            .enableSwipe(true)
            .swipeHorizontal(false)
            .onPageChange(this)
            .enableAnnotationRendering(true)
            .onLoad(this)
            .scrollHandle(DefaultScrollHandle(this))
            .load()
    }

    private fun saveBitmap(bitmap: Bitmap?) {
        try {
            val mFolder = File("$filesDir/SmartApp") //give a name for the folder
            val imagePath = File(mFolder.toString() + "screenshot.png")
            if (!mFolder.exists()) {
                mFolder.mkdir()
            }
            if (!imagePath.exists()) {
                imagePath.createNewFile()
            }
            var fos: FileOutputStream?
            fos = FileOutputStream(imagePath)
            // bitmap.compress(CompressFormat.PNG, 100, fos);
            bitmap!!.compress(Bitmap.CompressFormat.PNG, 60, fos)
            val byteArrayOutputStream = ByteArrayOutputStream()
            val byteArray = byteArrayOutputStream.toByteArray()
            val encodedByte = Base64.encodeToString(byteArray, Base64.DEFAULT)
            Log.e("encodeByte", encodedByte)
            fos.flush()
            fos.close()
            MediaStore.Images.Media.insertImage(contentResolver, bitmap, "Screen", "screen")
        } catch (e: FileNotFoundException) {
            Log.e("no file", e.message, e)
        } catch (e: IOException) {
            Log.e("io", e.message, e)
        }
    }

    override fun onPageChanged(page: Int, pageCount: Int) {
        pageNumber = page
        title = String.format("%s %s / %s", pdfFileName, page + 1, pageCount)
    }

    override fun loadComplete(nbPages: Int) {
        val meta = pdfView!!.documentMeta
        printBookmarksTree(pdfView!!.tableOfContents, "-")
    }

    fun printBookmarksTree(tree: List<Bookmark>, sep: String) {
        for (b in tree) {
            Log.e(TAG, String.format("%s %s, p %d", sep, b.title, b.pageIdx))
            if (b.hasChildren()) {
                printBookmarksTree(b.children, "$sep-")
            }
        }
    }
}