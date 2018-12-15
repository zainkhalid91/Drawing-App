package smartboard.fyp.com.smartapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    public static int cameraID = 0;
    public static boolean isBlack = true;
    private Button login_button;
    private EditText login_email, login_password;
    private TextView login_sign_up;
    private ProgressDialog progressdialogue;
    private FirebaseAuth fba;
    private DatabaseReference refer55;
    private FirebaseUser currentFirebaseUser55;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        fba = FirebaseAuth.getInstance();

        progressdialogue = new ProgressDialog(this);
        login_button = findViewById(R.id.login_btn);
        login_email = findViewById(R.id.login_email);
        login_password = findViewById(R.id.login_password);
        login_sign_up = findViewById(R.id.login_signup);

        login_button.setOnClickListener(this);
        login_sign_up.setOnClickListener(this);

        if (UserIsLogin()) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

    }


    public boolean UserIsLogin() {


        refer55 = FirebaseDatabase.getInstance().getReference();
        currentFirebaseUser55 = FirebaseAuth.getInstance().getCurrentUser();

        return currentFirebaseUser55 != null;
    }

    @Override
    public void onClick(View view) {
        if (view == login_button) {
            loginUser();

        }
        if (view == login_sign_up) {
            Intent i = new Intent(LoginActivity.this, RegistrationActivity.class);
            startActivity(i);
            finish();

        }

    }

    private void loginUser() {
        String email_user = login_email.getText().toString();
        String pass_user = login_password.getText().toString();

        if (TextUtils.isEmpty(email_user)) {
            Toast.makeText(this, "Enter email", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(pass_user)) {
            Toast.makeText(this, "Enter password", Toast.LENGTH_SHORT).show();
        }
        progressdialogue.setMessage("Logging in User...");
        progressdialogue.show();
        fba.signInWithEmailAndPassword(email_user, pass_user).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Intent i = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(i);
                    checkIfEmailVerified();
                } else {
                    FirebaseAuthException e = (FirebaseAuthException) task.getException();
                    Toast.makeText(LoginActivity.this, "Failed Login: " + " " + e.getMessage(), Toast.LENGTH_LONG).show();
                    progressdialogue.dismiss();

                }
            }
        });
    }

    public void checkIfEmailVerified() {
        try {
            FirebaseUser fbu = FirebaseAuth.getInstance().getCurrentUser();

            boolean emailVerified = fbu.isEmailVerified();
            if (!emailVerified) {
                Toast.makeText(this, "Verify the email address", Toast.LENGTH_SHORT).show();
                fba.signOut();
                Intent i = new Intent(LoginActivity.this, LoginActivity.class);
                startActivity(i);

            } else {
                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
