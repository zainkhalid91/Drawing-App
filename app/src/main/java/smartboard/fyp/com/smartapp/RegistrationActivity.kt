package smartboard.fyp.com.smartapp

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Base64
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.io.ByteArrayOutputStream
import java.io.IOException

class RegistrationActivity : AppCompatActivity(), View.OnClickListener {
    var refer: DatabaseReference? = null
    var refer2: DatabaseReference? = null
    var PICK_IMAGE_REQUEST: Int = 234
    private var register_name: EditText? = null
    private var register_email: EditText? = null
    private var register_password: EditText? = null
    private var register_systemid: EditText? = null
    private lateinit var register_button: Button
    private lateinit var edit: Button
    private val register_textview: TextView? = null
    private lateinit var register_signin: TextView
    private var progressdialogue: ProgressDialog? = null
    private var fba: FirebaseAuth? = null
    private lateinit var register_imageview: ImageView
    private var imageString: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)
        fba = FirebaseAuth.getInstance()
        imageString = "notgiven"
        refer = FirebaseDatabase.getInstance().getReference("Teacher")
        progressdialogue = ProgressDialog(this)
        register_imageview = findViewById(R.id.register_imageView)
        register_systemid = findViewById(R.id.register_systemid)
        register_name = findViewById(R.id.register_fullname)
        register_email = findViewById(R.id.register_email)
        register_password = findViewById(R.id.register_password)
        register_button = findViewById(R.id.register_btn)
        edit = findViewById(R.id.upload_button)
        register_signin = findViewById(R.id.register_signin)
        register_button.setOnClickListener(this)
        register_signin.setOnClickListener(this)
        edit.setOnClickListener(this)
        register_imageview.setImageResource(R.drawable.usericon)
    }

    override fun onClick(view: View) {
        if (view === register_button) {
            registerUser()
        }
        if (view === register_signin) {
            /*  val i: Intent = Intent(this@RegistrationActivity, LoginActivity::class.java)
              startActivity(i)
              finish()*/
        }
        if (view === edit) {
            showFileChooser()
        }
    }

    private fun registerUser() {
        val user_email: String = register_email!!.text.toString()
        val user_password: String = register_password!!.text.toString()
        val user_name: String = register_name!!.text.toString()
        val systemid: String = register_systemid!!.text.toString()
        val profilepic: String = "notgiven"
        val type: String = "Type"
        if (TextUtils.isEmpty(user_email)) {
            Toast.makeText(this, "Please fill the required fields", Toast.LENGTH_SHORT).show()
            return
        } else if (TextUtils.isEmpty(user_password)) {
            Toast.makeText(this, "Please fill the required fields", Toast.LENGTH_SHORT).show()
            return
        } else if (TextUtils.isEmpty(user_name)) {
            Toast.makeText(this, "Please fill the required fields", Toast.LENGTH_SHORT).show()
            return
        } else if (TextUtils.isEmpty(systemid)) {
            Toast.makeText(this, "Please fill the required fields", Toast.LENGTH_SHORT).show()
            return
        }
        progressdialogue!!.setMessage("Registereing User...")
        progressdialogue!!.show()
        fba!!.createUserWithEmailAndPassword(user_email, user_password)
            .addOnCompleteListener(this, object : OnCompleteListener<AuthResult?> {
                override fun onComplete(task: Task<AuthResult?>) {
                    if (task.isSuccessful) {
                        val userinformation: UserInformation = UserInformation(
                            "" + user_name, "" + user_email,
                            "" + user_password, "" + imageString, "" + systemid, "" + fba!!.uid
                        )
                        val firebaseuser: FirebaseUser? = fba!!.currentUser
                        refer!!.child(firebaseuser!!.uid).setValue(userinformation)
                        Toast.makeText(
                            this@RegistrationActivity,
                            "Succesfully saved",
                            Toast.LENGTH_SHORT
                        ).show()
                        userVerification()
                        /* val i: Intent = Intent(this@RegistrationActivity, LoginActivity::class.java)
                         startActivity(i)
                         finish()*/
                        Toast.makeText(
                            this@RegistrationActivity,
                            "Registered Succesfully ",
                            Toast.LENGTH_SHORT
                        ).show()
                        progressdialogue!!.dismiss()
                    } else {
                        val e: FirebaseAuthException? =
                            task.exception as FirebaseAuthException?
                        Toast.makeText(
                            this@RegistrationActivity,
                            "Failed Registration: " + " " + e!!.message,
                            Toast.LENGTH_LONG
                        ).show()
                        progressdialogue!!.dismiss()
                        return
                    }
                }
            })
    }

    fun userVerification() {
        val fbu: FirebaseUser? = FirebaseAuth.getInstance().currentUser
        if (fbu != null) {
            fbu.sendEmailVerification().addOnCompleteListener(object : OnCompleteListener<Void?> {
                override fun onComplete(task: Task<Void?>) {
                    if (task.isSuccessful) {
                        Toast.makeText(
                            this@RegistrationActivity,
                            "CHECK YOUR EMAIL VERIFICATION",
                            Toast.LENGTH_SHORT
                        ).show()
                        FirebaseAuth.getInstance().signOut()
                    } else {
                        Toast.makeText(
                            this@RegistrationActivity,
                            "Enter a valid email address",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            })
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if ((requestCode == PICK_IMAGE_REQUEST) && (resultCode == RESULT_OK) && (data != null) && (data.data != null)) {
            try {
                val filepath2: Uri? = data.data
                imageString = filepath2.toString()
                try {
                    val bitmap: Bitmap =
                        MediaStore.Images.Media.getBitmap(contentResolver, filepath2)
                    val scaledBitmap: Bitmap = scaleDown(bitmap, 200f, true)
                    imageString = imagestorageindatabase(scaledBitmap)
                    Toast.makeText(this, "" + imageString, Toast.LENGTH_SHORT).show()
                    register_imageview.setImageResource(0)
                    val myBitmap: Bitmap = convertbase64intobitmap(imageString)
                    register_imageview.setImageBitmap(myBitmap)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            } catch (t: Exception) {
                Toast.makeText(this, "Error " + t, Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
        }
    }

    fun convertbase64intobitmap(imgString2: String?): Bitmap {
        val baos: ByteArrayOutputStream = ByteArrayOutputStream()
        var imageBytes: ByteArray = baos.toByteArray()
        imageBytes = Base64.decode(imgString2, Base64.DEFAULT)
        val decodedImage: Bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        return decodedImage
    }

    fun imagestorageindatabase(bitmap: Bitmap): String {
        val stream: ByteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        val byteFormat: ByteArray = stream.toByteArray()
        val encodedImage: String = Base64.encodeToString(byteFormat, Base64.NO_WRAP)
        return encodedImage
    }

    private fun showFileChooser() {
        val it: Intent = Intent()
        it.type = "image/*"
        it.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(it, "Select an image"), PICK_IMAGE_REQUEST)
    }

    companion object {
        fun scaleDown(
            realImage: Bitmap, maxImageSize: Float,
            filter: Boolean
        ): Bitmap {
            val ratio: Float = Math.min(
                maxImageSize / realImage.width,
                maxImageSize / realImage.height
            )
            val width: Int = Math.round(ratio * realImage.width)
            val height: Int = Math.round(ratio * realImage.height)
            val newBitmap: Bitmap = Bitmap.createScaledBitmap(
                realImage, width,
                height, filter
            )
            return newBitmap
        }
    }
}