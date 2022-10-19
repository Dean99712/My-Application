package com.example.myapplication.ui.register

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivityLoginBinding
import com.example.myapplication.ui.MainActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider


class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        onTextPressToSignUpActivity()
        firebaseAuth = FirebaseAuth.getInstance()
        signInWithGoogleClient()
        onButtonCLick()
    }

    private fun onTextPressToSignUpActivity() {
        binding.directToSignupTv.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            finish()
        }
    }

    private fun signInWithGoogleClient() {
        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)

        binding.signInGoogle.setOnClickListener {
            signInWithGoogle()
        }
    }

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        activityLauncher.launch(signInIntent)
    }

    private val activityLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                handleResults(task)
            }
        }

    private fun handleResults(task: Task<GoogleSignInAccount>) {
        if (task.isSuccessful) {
            val account: GoogleSignInAccount? = task.result
            if (account != null) {
                updateUi(account)
            }
        } else {
            Snackbar.make(
                requireViewById(R.id.personFragment),
                task.exception.toString(),
                Snackbar.LENGTH_SHORT
            ).show()
        }
    }

    private fun updateUi(account: GoogleSignInAccount) {
        val credentials = GoogleAuthProvider.getCredential(account.idToken, null)
        firebaseAuth.signInWithCredential(credentials).addOnCompleteListener {
            if (it.isSuccessful) {
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("email", account.email)
                intent.putExtra("name", account.displayName)
                startActivity(intent)
                finish()

            } else {
                showSnackBar(binding.loginActivity, it.exception.toString())
            }
        }
            .addOnFailureListener {
                showSnackBar(binding.loginActivity, it.message.toString())
            }

    }

//    private fun displayCurrentTime() {
//        binding.button.setOnClickListener {
//
//            val currentTime = LocalDateTime.now()
//
//            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
//
//            currentLogin = LocalDateTime.now()
//            val userLastLogin = currentLogin.format(formatter)
//            val currentDateTime = currentLogin.format(formatter)
//            println(userLastLogin)
//
//            GlobalScope.launch(Dispatchers.IO){
//                delay(3600)
//                currentLogin = LocalDateTime.now()
//                println(currentLogin)
//            }
//        }
//    }

    override fun onStart() {
        super.onStart()
        if (firebaseAuth.currentUser != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            intent = Intent(this, LoginActivity::class.java)
        }
    }

    private fun onButtonCLick() {
        binding.continueButtonLogin.setOnClickListener {
            loginFireBaseUserWithCredentials()
        }
    }

    private fun loginFireBaseUserWithCredentials() {

        val email = findViewById<EditText>(R.id.email_login_et).text.toString()
        val password = findViewById<EditText>(R.id.password_login_et).text.toString()

        if (email.isNotEmpty() && password.isNotEmpty()) firebaseAuth.signInWithEmailAndPassword(
            email, password
        ).addOnCompleteListener {
            if (it.isSuccessful) {

                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_top)
                finish()

            } else {
                Toast.makeText(this, it.exception?.message, Toast.LENGTH_SHORT).show()
                it.exception?.message?.let { it1 ->
                    showSnackBar(binding.loginActivity, it.exception.toString())
                }
            }

        }.addOnFailureListener {
            showSnackBar(binding.loginActivity, "Oops Something went wrong")
        }
        else showSnackBar(binding.loginActivity, "Email or Password Cannot be empty!")
    }

    private fun showSnackBar(view: View ,text: String) {
        Snackbar.make(applicationContext, view, text,Snackbar.LENGTH_SHORT).show()
    }
}