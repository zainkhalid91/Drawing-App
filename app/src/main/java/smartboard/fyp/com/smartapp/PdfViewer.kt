package smartboard.fyp.com.smartapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.File
import java.util.*

class PdfViewer : AppCompatActivity() {
    lateinit var lv_pdf: ListView
    var obj_adapter: PDFAdapter? = null
    var boolean_permission = false
    var dir: File? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.lv_pdf)
        init()
    }

    private fun init() {
        lv_pdf = findViewById(R.id.lv_pdf)
        dir = File(Environment.getExternalStorageDirectory().absolutePath)
        fn_permission()
        lv_pdf.onItemClickListener = OnItemClickListener { adapterView, view, i, l ->
            val intent = Intent(applicationContext, PdfActivity::class.java)
            intent.putExtra("position", i)
            startActivity(intent)
            Log.e("Position", i.toString() + "")
        }
    }

    fun getfile(dir: File?): ArrayList<File> {
        val listFile = dir!!.listFiles()
        if (listFile != null && listFile.size > 0) {
            for (i in listFile.indices) {
                if (listFile[i].isDirectory) {
                    getfile(listFile[i])
                } else {
                    var booleanpdf = false
                    if (listFile[i].name.endsWith(".pdf")) {
                        for (j in fileList.indices) {
                            if (fileList[j].name == listFile[i].name) {
                                booleanpdf = true
                            } else {
                            }
                        }
                        if (booleanpdf) {
                            booleanpdf = false
                        } else {
                            fileList.add(listFile[i])
                        }
                    }
                }
            }
        }
        return fileList
    }

    private fun fn_permission() {
        if (ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this@PdfViewer,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            ) {
            } else {
                ActivityCompat.requestPermissions(
                    this@PdfViewer, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    REQUEST_PERMISSIONS
                )
            }
        } else {
            boolean_permission = true
            getfile(dir)
            obj_adapter = PDFAdapter(applicationContext, fileList)
            lv_pdf.adapter = obj_adapter
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSIONS) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                boolean_permission = true
                getfile(dir)
                obj_adapter = PDFAdapter(applicationContext, fileList)
                lv_pdf.adapter = obj_adapter
            } else {
                Toast.makeText(applicationContext, "Please allow the permission", Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    companion object {
        var fileList = ArrayList<File>()
        var REQUEST_PERMISSIONS = 1
    }
}