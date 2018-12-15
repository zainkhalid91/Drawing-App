package smartboard.fyp.com.smartapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class RegistrationActivity extends AppCompatActivity implements View.OnClickListener {
    DatabaseReference refer, refer2;
    int PICK_IMAGE_REQUEST = 234;
    private EditText register_name, register_email, register_password, register_systemid;
    private Button register_button, edit;
    private TextView register_textview, register_signin;
    private ProgressDialog progressdialogue;
    private FirebaseAuth fba;
    private ImageView register_imageview;
    private String imageString;

    public static Bitmap scaleDown(Bitmap realImage, float maxImageSize,
                                   boolean filter) {
        float ratio = Math.min(
                maxImageSize / realImage.getWidth(),
                maxImageSize / realImage.getHeight());
        int width = Math.round(ratio * realImage.getWidth());
        int height = Math.round(ratio * realImage.getHeight());

        Bitmap newBitmap = Bitmap.createScaledBitmap(realImage, width,
                height, filter);
        return newBitmap;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        fba = FirebaseAuth.getInstance();
        imageString = "notgiven";
        refer = FirebaseDatabase.getInstance().getReference("Teacher");

        progressdialogue = new ProgressDialog(this);
        register_imageview = findViewById(R.id.register_imageView);
        register_systemid = findViewById(R.id.register_systemid);
        register_name = findViewById(R.id.register_fullname);
        register_email = findViewById(R.id.register_email);
        register_password = findViewById(R.id.register_password);
        register_button = findViewById(R.id.register_btn);
        edit = findViewById(R.id.upload_button);
        register_signin = findViewById(R.id.register_signin);

        register_button.setOnClickListener(this);
        register_signin.setOnClickListener(this);
        edit.setOnClickListener(this);

        register_imageview.setImageResource(R.drawable.usericon);
    }

    @Override
    public void onClick(View view) {

        if (view == register_button) {
            registerUser();

        }
        if (view == register_signin) {
            Intent i = new Intent(RegistrationActivity.this, LoginActivity.class);
            startActivity(i);
            finish();

        }
        if (view == edit) {
            showFileChooser();

        }

    }

    private void registerUser() {
        final String user_email = register_email.getText().toString();
        final String user_password = register_password.getText().toString();
        final String user_name = register_name.getText().toString();
        final String systemid = register_systemid.getText().toString();
        final String profilepic = "notgiven";
        final String type = "Type";

        if (TextUtils.isEmpty(user_email)) {
            Toast.makeText(this, "Please fill the required fields", Toast.LENGTH_SHORT).show();
            return;

        } else if (TextUtils.isEmpty(user_password)) {
            Toast.makeText(this, "Please fill the required fields", Toast.LENGTH_SHORT).show();
            return;

        } else if (TextUtils.isEmpty(user_name)) {

            Toast.makeText(this, "Please fill the required fields", Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(systemid)) {

            Toast.makeText(this, "Please fill the required fields", Toast.LENGTH_SHORT).show();
            return;
        }
        progressdialogue.setMessage("Registereing User...");
        progressdialogue.show();

        fba.createUserWithEmailAndPassword(user_email, user_password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    UserInformation userinformation = new UserInformation("" + user_name, "" + user_email,
                            "" + user_password, "" + imageString, "" + systemid, "" + fba.getUid());
                    FirebaseUser firebaseuser = fba.getCurrentUser();
                    refer.child(firebaseuser.getUid()).setValue(userinformation);
                    Toast.makeText(RegistrationActivity.this, "Succesfully saved", Toast.LENGTH_SHORT).show();
                    userVerification();
                    Intent i = new Intent(RegistrationActivity.this, LoginActivity.class);
                    startActivity(i);
                    finish();
                    Toast.makeText(RegistrationActivity.this, "Registered Succesfully ", Toast.LENGTH_SHORT).show();
                    progressdialogue.dismiss();

                } else {

                    FirebaseAuthException e = (FirebaseAuthException) task.getException();
                    Toast.makeText(RegistrationActivity.this, "Failed Registration: " + " " + e.getMessage(), Toast.LENGTH_LONG).show();
                    progressdialogue.dismiss();
                    return;
                }


            }
        });
    }

    public void userVerification() {
        final FirebaseUser fbu = FirebaseAuth.getInstance().getCurrentUser();
        if (fbu != null) {
            fbu.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(RegistrationActivity.this, "CHECK YOUR EMAIL VERIFICATION", Toast.LENGTH_SHORT).show();
                        FirebaseAuth.getInstance().signOut();
                    } else {
                        Toast.makeText(RegistrationActivity.this, "Enter a valid email address", Toast.LENGTH_SHORT).show();

                    }
                }
            });

        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            try {
                Uri filepath2 = data.getData();
                imageString = filepath2.toString();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filepath2);
                    Bitmap scaledBitmap = scaleDown(bitmap, 200, true);
                    imageString = imagestorageindatabase(scaledBitmap);
                    Toast.makeText(this, "" + imageString, Toast.LENGTH_SHORT).show();
                    register_imageview.setImageResource(0);
                    Bitmap myBitmap = convertbase64intobitmap(imageString);
                    register_imageview.setImageBitmap(myBitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }


            } catch (Exception t) {
                Toast.makeText(this, "Error " + t, Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
        }

    }

    Bitmap convertbase64intobitmap(String imgString2) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] imageBytes = baos.toByteArray();
        imageBytes = Base64.decode(imgString2, Base64.DEFAULT);
        Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        return decodedImage;
    }

    public String imagestorageindatabase(Bitmap bitmap) {

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteFormat = stream.toByteArray();
        String encodedImage = Base64.encodeToString(byteFormat, Base64.NO_WRAP);
        return encodedImage;


    }

    private void showFileChooser() {
        Intent it = new Intent();
        it.setType("image/*");
        it.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(it, "Select an image"), PICK_IMAGE_REQUEST);
    }
}
