package com.example.myapplication.ui.register

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.myapplication.R
import com.example.myapplication.data.FirebaseManager
import com.example.myapplication.databinding.ActivityLoginBinding
import com.example.myapplication.model.user.User
import com.example.myapplication.ui.MainActivity
import com.example.myapplication.util.SharedPreferencesManager
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider


class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var callbackManager: CallbackManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()
        callbackManager = CallbackManager.Factory.create()
        signInWithGoogleClient()
        onButtonCLick()
    }

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

    fun loginWithFacebook(view: View) {
        LoginManager.getInstance().logInWithReadPermissions(
            this,
            listOf("email", "public_profile")
        )
        LoginManager.getInstance()
            .registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
                override fun onCancel() {
                    Snackbar.make(
                        binding.loginActivity,
                        "Ops... Something went wrong!",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }

                override fun onError(error: FacebookException) {
                    Log.d(ContentValues.TAG, "facebook:onError", error)
                    Snackbar.make(
                        binding.loginActivity,
                        "Ops... Something went wrong! Please try again later",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }

                override fun onSuccess(result: LoginResult) {
                    Log.d(ContentValues.TAG, "facebook:onSuccess:$result")

                    handleFacebookAccessToken(result.accessToken)
                }
            })
    }

    private fun updateUI(user: FirebaseUser?) {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun handleFacebookAccessToken(token: AccessToken) {
        Log.d(ContentValues.TAG, "handleFacebookAccessToken:$token")

        val credential = FacebookAuthProvider.getCredential(token.token)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    FirebaseManager.getInstance(this).createUser(
                        User(
                            firebaseAuth.currentUser!!.email!!,
                            firebaseAuth.currentUser!!.displayName!!,
                            "",
                            firebaseAuth.currentUser!!.photoUrl.toString(),
                        )
                    )
                    updateUI(user)
                } else {
                    Log.w(ContentValues.TAG, "signInWithCredential:failure", task.exception)
                    Toast.makeText(
                        baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                    updateUI(null)
                }
            }
    }

    fun onTextPressToSignUpActivity(view: View) {
        val intent = Intent(this, SignUpActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        finish()
    }

    private fun signInWithGoogleClient() {
        val googleSignInOptions =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail().build()

        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)

        binding.signInGoogle.setOnClickListener {
            signInWithGoogle()
        }
    }

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        activityLauncher.launch(signInIntent)
    }

    private
    val activityLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val task =
                    GoogleSignIn.getSignedInAccountFromIntent(result.data)
                        .addOnSuccessListener {
                            Toast.makeText(
                                this,
                                "Hello ${it.givenName}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }.addOnFailureListener {
                            Toast.makeText(
                                this,
                                it.message.toString(),
                                Toast.LENGTH_LONG
                            ).show()
                        }
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
        val credentials =
            GoogleAuthProvider.getCredential(account.idToken, null)
        firebaseAuth.signInWithCredential(credentials)
            .addOnCompleteListener {
                if (it.isSuccessful) {


                    val intent = Intent(this, MainActivity::class.java)

                    startActivity(intent)
                    finish()

                } else {
                    Snackbar.make(
                        binding.loginActivity,
                        it.exception.toString(),
                        Snackbar.LENGTH_SHORT
                    )
                        .show()
                }
            }.addOnFailureListener {
                Snackbar.make(
                    binding.loginActivity,
                    it.message.toString(),
                    Snackbar.LENGTH_SHORT
                )
                    .show()
            }
    }

    private fun onButtonCLick() {
        binding.continueButtonLogin.setOnClickListener {
            Toast.makeText(
                this,
                "This button is working",
                Toast.LENGTH_SHORT
            ).show()
            loginFireBaseUserWithCredentials()
        }
    }

    private fun loginFireBaseUserWithCredentials() {
        val email = binding.emailLoginEt.text.toString()
        val password = binding.passwordLoginEt.text.toString()

        if (email.isNotEmpty() && password.isNotEmpty()) firebaseAuth.signInWithEmailAndPassword(
            email, password
        ).addOnCompleteListener {
            if (it.isSuccessful) {

                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
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
            Snackbar.make(
                binding.loginActivity,
                it.message.toString(),
                Snackbar.LENGTH_SHORT
            )
                .show()
        }
        else Snackbar.make(
            binding.loginActivity,
            "Email or Password Cannot be empty!",
            Snackbar.LENGTH_SHORT
        ).show()
    }
}