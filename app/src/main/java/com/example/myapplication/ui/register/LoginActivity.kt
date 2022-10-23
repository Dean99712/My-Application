package com.example.myapplication.ui.register

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
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
    private lateinit var sharedPreferences: SharedPreferences
    private final val SHARED_PREFERENCES = "sharedPref"
    private final val SHARED_PREFERENCES_NAME = "name"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPreferences = getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE)
        firebaseAuth = FirebaseAuth.getInstance()
        signInWithGoogleClient()
        onButtonCLick()
    }

    fun onTextPressToSignUpActivity(view: View) {
        val intent = Intent(this, SignUpActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        finish()
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
                binding.loginActivity,
                task.exception.toString(),
                Snackbar.LENGTH_SHORT
            ).show()
        }
    }

    private fun updateUi(account: GoogleSignInAccount) {
        val credentials = GoogleAuthProvider.getCredential(account.idToken, null)
        firebaseAuth.signInWithCredential(credentials).addOnCompleteListener {
            if (it.isSuccessful) {
                val editor = sharedPreferences.edit()
                editor.putString(SHARED_PREFERENCES_NAME, account.givenName)
                editor.apply()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()

            } else {
                Snackbar.make(binding.loginActivity, it.exception.toString(), Snackbar.LENGTH_SHORT)
                    .show()
            }
        }
            .addOnFailureListener {
                Snackbar.make(binding.loginActivity, it.message.toString(), Snackbar.LENGTH_SHORT)
                    .show()
            }

    }

    override fun onStart() {
        super.onStart()
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
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

                val editor = sharedPreferences.edit()
                editor.putString(SHARED_PREFERENCES_NAME, email)
                editor.apply()

                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_top)
                finish()

            } else {
                it.exception?.message?.let { _ ->
                    Snackbar.make(
                        binding.loginActivity,
                        it.exception!!.message.toString(),
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }

        }.addOnFailureListener {
            Snackbar.make(binding.loginActivity, "Oops Something went wrong", Snackbar.LENGTH_SHORT)
                .show()
        }
        else Snackbar.make(
            binding.loginActivity,
            "Email or Password Cannot be empty!",
            Snackbar.LENGTH_SHORT
        ).show()
    }

    private fun showSnackBar(view: View, text: String) {
        Snackbar.make(applicationContext, view, text, Snackbar.LENGTH_SHORT).show()
    }
}