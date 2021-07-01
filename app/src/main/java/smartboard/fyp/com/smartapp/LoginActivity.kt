package smartboard.fyp.com.smartapp

class LoginActivity constructor():AppCompatActivity(),View.OnClickListener{
private var login_button:Button?=null
private var login_email:EditText?=null
private var login_password:EditText?=null
private var login_sign_up:TextView?=null
private var progressdialogue:ProgressDialog?=null
private var fba:FirebaseAuth?=null
private var refer55:DatabaseReference?=null
private var currentFirebaseUser55:FirebaseUser?=null
        override fun onCreate(savedInstanceState:Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        fba=FirebaseAuth.getInstance()
        progressdialogue=ProgressDialog(this)
        login_button=findViewById(R.id.login_btn)
        login_email=findViewById(R.id.login_email)
        login_password=findViewById(R.id.login_password)
        login_sign_up=findViewById(R.id.login_signup)
        login_button.setOnClickListener(this)
        login_sign_up.setOnClickListener(this)
        if(UserIsLogin()){
        val intent:Intent=Intent(this@LoginActivity,MainActivity::class.java)
        startActivity(intent)
        finish()
        }
        }

        fun UserIsLogin():Boolean{
        refer55=FirebaseDatabase.getInstance().getReference()
        currentFirebaseUser55=FirebaseAuth.getInstance().getCurrentUser()
        return currentFirebaseUser55!=null
        }

public override fun onClick(view:View){
        if(view===login_button){
        loginUser()
        }
        if(view===login_sign_up){
        val i:Intent=Intent(this@LoginActivity,RegistrationActivity::class.java)
        startActivity(i)
        finish()
        }
        }

private fun loginUser(){
        val email_user:String=login_email!!.getText().toString()
        val pass_user:String=login_password!!.getText().toString()
        if(TextUtils.isEmpty(email_user)){
        Toast.makeText(this,"Enter email",Toast.LENGTH_SHORT).show()
        }
        if(TextUtils.isEmpty(pass_user)){
        Toast.makeText(this,"Enter password",Toast.LENGTH_SHORT).show()
        }
        progressdialogue!!.setMessage("Logging in User...")
        progressdialogue!!.show()
        fba!!.signInWithEmailAndPassword(email_user,pass_user)
        .addOnCompleteListener(this,object:OnCompleteListener<AuthResult?>{
public override fun onComplete(task:Task<AuthResult?>){
        if(task.isSuccessful()){
        val i:Intent=Intent(this@LoginActivity,MainActivity::class.java)
        startActivity(i)
        checkIfEmailVerified()
        }else{
        val e:FirebaseAuthException?=
        task.getException()as FirebaseAuthException?
        Toast.makeText(
        this@LoginActivity,
        "Failed Login: "+" "+e!!.message,
        Toast.LENGTH_LONG
        ).show()
        progressdialogue!!.dismiss()
        }
        }
        })
        }

        fun checkIfEmailVerified(){
        try{
        val fbu:FirebaseUser?=FirebaseAuth.getInstance().getCurrentUser()
        val emailVerified:Boolean=fbu!!.isEmailVerified()
        if(!emailVerified){
        Toast.makeText(this,"Verify the email address",Toast.LENGTH_SHORT).show()
        fba!!.signOut()
        val i:Intent=Intent(this@LoginActivity,LoginActivity::class.java)
        startActivity(i)
        }else{
        val i:Intent=Intent(this@LoginActivity,MainActivity::class.java)
        startActivity(i)
        finish()
        }
        }catch(e:Exception){
        Toast.makeText(this,e.message,Toast.LENGTH_SHORT).show()
        }
        }

        companion object{
        var cameraID:Int=0
        var isBlack:Boolean=true
        }
        }