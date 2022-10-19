package com.example.myapplication.ui.register

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import android.widget.Toast.makeText
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.example.myapplication.R
import com.example.myapplication.data.FirebaseManager
import com.example.myapplication.data.Repository
import com.example.myapplication.databinding.ActivitySignupBinding
import com.example.myapplication.model.user.User
import com.example.myapplication.util.SharedPreferencesManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class SignUpActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var binding: ActivitySignupBinding
    private lateinit var googleGetContent: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        goToLoginActivityOnClick()
        firebaseAuth = FirebaseAuth.getInstance()

        binding.directToLoginTv.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            finish()

//            googleGetContent =
//                registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { content ->
//                    onGoogleIntentResult(content)
//                }
        }
    }

//    private fun handleFacebookAccessToken(token: AccessToken) {
//        Log.d(TAG, "handleFacebookAccessToken:$token")
//
//        val credential = FacebookAuthProvider.getCredential(token.token)
//        auth.signInWithCredential(credential)
//            .addOnCompleteListener(this) { task ->
//                if (task.isSuccessful) {
//                    // Sign in success, update UI with the signed-in user's information
//                    Log.d(TAG, "signInWithCredential:success")
//                    val user = auth.currentUser
//                    updateUI(user)
//                } else {
//                    // If sign in fails, display a message to the user.
//                    Log.w(TAG, "signInWithCredential:failure", task.exception)
//                    Toast.makeText(baseContext, "Authentication failed.",
//                        Toast.LENGTH_SHORT).show()
//                    updateUI(null)
//                }
//            }
//    }


    private fun goToLoginActivityOnClick() {
        binding.continueButtonSignup.setOnClickListener {
            createFirebaseUserWithCredentials()
        }
    }

    private fun createFirebaseUserWithCredentials() {
        val email = binding.emailSignupEt.text.toString()
        val password = binding.passwordSignupEt.text.toString()

        if (email.isNotEmpty() && password.isNotEmpty()) firebaseAuth.createUserWithEmailAndPassword(
            email, password
        ).addOnCompleteListener {
            if (it.isSuccessful) {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
                makeText(this, "Signed up successfully!", Toast.LENGTH_LONG).show()

            } else makeText(this, it.exception?.message, Toast.LENGTH_SHORT).show()
        }
        else makeText(this, "Email or Password Cannot be empty!", Toast.LENGTH_SHORT).show()
    }

//    @SuppressLint("SuspiciousIndentation")
//    private fun onGoogleIntentResult(content: ActivityResult) {
//        val task: Task<GoogleSignInAccount> =
//            GoogleSignIn.getSignedInAccountFromIntent(content.data)
//        task.addOnSuccessListener {
//                loginOrSignUpToFirebase(it)
//                displayToast(" Hey " + it.displayName.toString())
//            }.addOnFailureListener {
//                displayToast("If you cant use GoogleSignIn please sign in the regular way")
//            }
//    }

//    private fun loginOrSignUpToFirebase(googleSignInAccount: GoogleSignInAccount) {
//        firebaseAuth.fetchSignInMethodsForEmail(googleSignInAccount.email!!).addOnSuccessListener {
//                if (it.signInMethods.isNullOrEmpty()) {
//                    registerUserToFirebase(googleSignInAccount)
//                } else {
//                    FirebaseManager.getInstance(applicationContext).getUser().addOnSuccessListener {
//                            val user = it.toObject(User::class.java)
//                            println(user)
//                            getIntoApp(googleSignInAccount.displayName.toString())
//                        }.addOnFailureListener {}
//                }
//            }.addOnFailureListener { displayToast("Failed !") }
//    }

//    private fun registerUserToFirebase(googleSignInAccount: GoogleSignInAccount) {
//        val authCredential = GoogleAuthProvider.getCredential(googleSignInAccount.idToken, null)
//        firebaseAuth.signInWithCredential(authCredential).addOnSuccessListener {
//                val user = User(
//                    googleSignInAccount.email!!,
//                    googleSignInAccount.givenName!!,
//                    googleSignInAccount.familyName!!,
//                    PersonList()
//                )
//                SharedPreferencesManager.myUser = user
//
//                FirebaseManager.getInstance(applicationContext).addUser(user)
//                    .addOnSuccessListener {
//                        Repository.getInstance(applicationContext).addUser(user)
//                        getIntoApp(googleSignInAccount.displayName.toString())
//
//                    }.addOnFailureListener { println(it) }
//            }.addOnFailureListener {
//                displayToast("Please try again later Exception: ${it.message}")
//            }
//    }


    private fun getIntoApp(userName: String) {
        this.getIntoApp(userName)
    }
}

