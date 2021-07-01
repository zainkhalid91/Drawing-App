package smartboard.fyp.com.smartapp

import android.content.ActivityNotFoundException
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.*
import smartboard.fyp.com.smartapp.MaterialActivity
import java.io.File

class MaterialActivity : AppCompatActivity() {
    private lateinit var mSelectFile: Button
    private lateinit var mPause: Button
    private lateinit var mCancel: Button
    private var mProgress: ProgressBar? = null
    private var mFileNameLable: TextView? = null
    private var mSizeLable: TextView? = null
    private var mProgressLable: TextView? = null
    private var mStorageRef: StorageReference? = null
    private var mStoragetask: StorageTask<*>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_material)
        mSelectFile = findViewById(R.id.btn_selectfiletoupload)
        mCancel = findViewById(R.id.btn_cancel)
        mProgress = findViewById(R.id.upload_progress)
        mPause = findViewById(R.id.btn_pause)
        mFileNameLable = findViewById(R.id.filename_label)
        mProgressLable = findViewById(R.id.progress_label)
        mSizeLable = findViewById(R.id.size_label)
        mStorageRef = FirebaseStorage.getInstance().reference
        mSelectFile.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                openFileSelector()
            }
        })
        mPause.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                val btnText: String = mPause.text.toString()
                if ((btnText == "Pause Upload")) {
                    try {
                        Toast.makeText(
                            applicationContext,
                            "Please select a file first",
                            Toast.LENGTH_SHORT
                        ).show()
                    } catch (ex: Exception) {
                    }
                    mStoragetask!!.pause()
                    mPause.text = "Resume Upload"
                } else {
                    mStoragetask!!.resume()
                    mPause.text = "Pause Upload"
                }
            }
        })
        mCancel.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                mStoragetask!!.cancel()
                try {
                    Toast.makeText(
                        applicationContext,
                        "Please select a file first",
                        Toast.LENGTH_SHORT
                    ).show()
                } catch (ex: Exception) {
                }
                startActivity(Intent(this@MaterialActivity, MaterialActivity::class.java))
            }
        })
    }

    private fun openFileSelector() {
        val intent: Intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        try {
            startActivityForResult(
                Intent.createChooser(intent, "Select a file to upload"), FILE_SELECT_CODE
            )
        } catch (ex: ActivityNotFoundException) {
            Toast.makeText(
                this@MaterialActivity,
                "Please install a File Manager.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == FILE_SELECT_CODE && resultCode == RESULT_OK) {
            val fileUri: Uri? = data!!.data
            val uriString: String = fileUri.toString()
            val myFile: File = File(uriString)
            //String path = myFile.getAbsolutePath();
            var displayName: String? = null
            if (uriString.startsWith("content://")) {
                var cursor: Cursor? = null
                try {
                    cursor = contentResolver.query((fileUri)!!, null, null, null, null)
                    if (cursor != null && cursor.moveToFirst()) {
                        displayName =
                            cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                    }
                } finally {
                    cursor!!.close()
                }
            } else if (uriString.startsWith("file://")) {
                displayName = myFile.name
            }
            mFileNameLable!!.text = displayName
            val fileName: String = System.currentTimeMillis().toString() + ".pdf"
            val riversRef: StorageReference = mStorageRef!!.child("files/" + displayName)
            mStoragetask = riversRef.putFile((fileUri)!!)
                .addOnSuccessListener(object : OnSuccessListener<UploadTask.TaskSnapshot> {
                    override fun onSuccess(taskSnapshot: UploadTask.TaskSnapshot) {
                        // Get a URL to the uploaded content
                        val urlTask: Task<Uri> = taskSnapshot.storage.downloadUrl
                        while (!urlTask.isSuccessful);
                        val downloadUrl: Uri? = urlTask.result
                        Toast.makeText(this@MaterialActivity, "File Uploaded.", Toast.LENGTH_SHORT)
                            .show()
                    }
                })
                .addOnFailureListener(object : OnFailureListener {
                    override fun onFailure(exception: Exception) {
                        Toast.makeText(
                            this@MaterialActivity,
                            "There was an error while Uploading the file.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }).addOnProgressListener { taskSnapshot ->
                    val progress: Double =
                        (100.0 * taskSnapshot.bytesTransferred) / taskSnapshot.totalByteCount
                    mProgress!!.progress = progress.toInt()
                    val progressText: String =
                        (taskSnapshot.bytesTransferred / (1024 * 1024)).toString() + " / " + (taskSnapshot.totalByteCount / (1024 * 1024)) + " mb"
                    mSizeLable!!.text = progressText
                    mProgressLable!!.text = progress as String + "٪"
                }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    companion object {
        private const val FILE_SELECT_CODE: Int = 1
    }
}