package com.example.myapplication.ui.register


import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import android.widget.Toast.makeText
import androidx.activity.result.contract.ActivityResultContracts
import com.example.myapplication.R
import com.example.myapplication.data.FirebaseManager
import com.example.myapplication.data.Repository
import com.example.myapplication.databinding.ActivitySignupBinding
import com.example.myapplication.model.user.User
import com.example.myapplication.ui.MainActivity
import com.example.myapplication.util.SharedPreferencesManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlin.concurrent.thread

class SignUpActivity : AppCompatActivity() {

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var binding: ActivitySignupBinding
    private lateinit var sharedPreferences: SharedPreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPreferences = SharedPreferencesManager.getInstance(this)
        firebaseAuth = FirebaseAuth.getInstance()
        signInWithGoogleClient()
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
                val editor = sharedPreferences.sharedPreferences.edit()
                editor.putString(sharedPreferences.SHARED_PREFERENCES_NAME, account.givenName)
                editor.putString(sharedPreferences.SHARED_PREFERENCES_LAST_NAME, account.familyName)
                editor.putString(sharedPreferences.SHARED_PREFERENCES_EMAIL, account.email)
                editor.apply()
                val intent = Intent(this, MainActivity::class.java)
                FirebaseManager.getInstance(this).createUser(
                    User(
                        account.email!!,
                        account.displayName!!,
                        account.familyName!!,
                        null
                    )
                )
                startActivity(intent)
                finish()

            } else {
                Snackbar.make(binding.signupActivity, it.exception.toString(), Snackbar.LENGTH_SHORT)
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
        val user = User(
            email,
            firstName,
            lastName,
            null
        )

        if (email.isNotEmpty() && password.isNotEmpty()) firebaseAuth.createUserWithEmailAndPassword(
            email, password
        ).addOnCompleteListener { it ->
            if (it.isSuccessful) {
                val editor = sharedPreferences.sharedPreferences.edit()
                editor.putString(sharedPreferences.SHARED_PREFERENCES_NAME, firstName)
                editor.putString(sharedPreferences.SHARED_PREFERENCES_LAST_NAME, lastName)
                editor.putString(sharedPreferences.SHARED_PREFERENCES_EMAIL, email)
                editor.apply()

                thread(start = true) {
                    Repository.getInstance(this).addUser(user)
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
                firebaseAuth.signOut()
                startActivity(intent)

                finish()
                makeText(this, "Signed up successfully!", Toast.LENGTH_LONG).show()

            } else makeText(this, it.exception?.message, Toast.LENGTH_SHORT).show()
        }.addOnFailureListener() {
            Snackbar.make(binding.signupActivity, it.message.toString(), Snackbar.LENGTH_LONG)
                .show()
        }
        else makeText(this, "Email or Password Cannot be empty!", Toast.LENGTH_SHORT).show()
    }

}


