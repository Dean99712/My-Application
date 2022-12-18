package com.example.myapplication.ui.register


import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import android.widget.Toast.makeText
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R
import com.example.myapplication.data.FirebaseManager
import com.example.myapplication.data.Repository
import com.example.myapplication.databinding.ActivitySignupBinding
import com.example.myapplication.model.user.User
import com.example.myapplication.ui.MainActivity
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
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SignUpActivity : AppCompatActivity() {

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var callbackManager: CallbackManager
    private lateinit var binding: ActivitySignupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()
        callbackManager = CallbackManager.Factory.create()
        signInWithGoogleClient()
    }

    public override fun onStart() {
        super.onStart()
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            updateUI(currentUser)
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
                        binding.signupActivity,
                        "Ops... Something went wrong!",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }

                override fun onError(error: FacebookException) {
                    Log.d(TAG, "facebook:onError", error)
                    Snackbar.make(
                        binding.signupActivity,
                        "Ops... Something went wrong! Please try again later",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }

                override fun onSuccess(result: LoginResult) {
                    Log.d(TAG, "facebook:onSuccess:$result")

                    handleFacebookAccessToken(result.accessToken)
                }
            })
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }

    private fun handleFacebookAccessToken(token: AccessToken) {
        Log.d(TAG, "handleFacebookAccessToken:$token")

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
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    makeText(
                        baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                    updateUI(null)
                }
            }
    }

    private fun updateUI(user: FirebaseUser?) {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun goToLoginActivityOnClick(view: View) {
        createFirebaseUserWithCredentials()
    }

    fun onTextPress(view: View) {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        finish()
    }

    private fun signInWithGoogleClient() {
        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build()

        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)

        binding.signinGoogle.setOnClickListener {
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
                val task =
                    GoogleSignIn.getSignedInAccountFromIntent(result.data).addOnSuccessListener {
                        makeText(this, "Hello ${it.givenName}", Toast.LENGTH_SHORT).show()
                    }.addOnFailureListener {
                        makeText(this, it.message.toString(), Toast.LENGTH_LONG).show()
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
                binding.signupActivity, task.exception.toString(), Snackbar.LENGTH_SHORT
            ).show()
        }
    }

    private fun updateUi(account: GoogleSignInAccount) {
        val credentials = GoogleAuthProvider.getCredential(account.idToken, null)
        firebaseAuth.signInWithCredential(credentials).addOnCompleteListener {
            if (it.isSuccessful) {
                val intent = Intent(this, MainActivity::class.java)

                FirebaseManager.getInstance(this).createUser(
                    User(
                        account.email!!,
                        account.givenName!!,
                        account.familyName!!,
                        account.photoUrl.toString(),
                    )
                )
                startActivity(intent)
                finish()

            } else {
                Snackbar.make(
                    binding.signupActivity,
                    it.exception.toString(),
                    Snackbar.LENGTH_SHORT
                )
                    .show()
            }
        }.addOnFailureListener {
            Snackbar.make(binding.signupActivity, it.message.toString(), Snackbar.LENGTH_SHORT)
                .show()
        }
    }


    private fun createFirebaseUserWithCredentials() {

        val firstName = binding.firstNameSignupEt.text.toString()
        val lastName = binding.lastNameSignupEt.text.toString()
        val email = binding.emailSignupEt.text.toString()
        val password = binding.passwordSignupEt.text.toString()

        val user = User(email, firstName, lastName, "")

        if (email.isNotEmpty() && password.isNotEmpty()) firebaseAuth.createUserWithEmailAndPassword(
            email, password
        ).addOnCompleteListener { it ->
            if (it.isSuccessful) {

                val firebaseUser = Firebase.auth.currentUser

                val profileUpdates = userProfileChangeRequest {
                    displayName = "$firstName $lastName"
                }

                firebaseUser!!.updateProfile(profileUpdates)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d(TAG, "User profile updated.")
                        }
                    }
                Log.d("", firebaseAuth.currentUser!!.displayName.toString())

                GlobalScope.launch(Dispatchers.IO) {
                    Repository.getInstance(this@SignUpActivity).createUser(user)
                }
                FirebaseManager.getInstance(this).createUser(user)
                    .addOnSuccessListener {
                        makeText(this, "Success", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Snackbar.make(
                            binding.signupActivity,
                            it.message.toString(),
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                firebaseAuth.signOut()
                finish()

            } else makeText(this, it.exception?.message, Toast.LENGTH_SHORT).show()

        }.addOnFailureListener() {
            Snackbar.make(binding.signupActivity, it.message.toString(), Snackbar.LENGTH_LONG)
                .show()
        }
        else makeText(this, "Email or Password Cannot be empty!", Toast.LENGTH_SHORT).show()
    }

}



