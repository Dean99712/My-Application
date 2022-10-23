package com.example.myapplication.ui.register


import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import android.widget.Toast.makeText
import androidx.activity.result.ActivityResultLauncher
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivitySignupBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class SignUpActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var binding: ActivitySignupBinding
    private lateinit var googleGetContent: ActivityResultLauncher<Intent>
    private lateinit var googleAuthProvider: GoogleAuthProvider
    private lateinit var sharedPreferences: SharedPreferences
    private final val SHARED_PREFERENCES = "sharedPref"
    private final val SHARED_PREFERENCES_NAME = "name"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPreferences = getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE)
        firebaseAuth = FirebaseAuth.getInstance()
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

    private fun createFirebaseUserWithCredentials() {
        val email = binding.emailSignupEt.text.toString()
        val password = binding.passwordSignupEt.text.toString()

        if (email.isNotEmpty() && password.isNotEmpty()) firebaseAuth.createUserWithEmailAndPassword(
            email, password
        ).addOnCompleteListener {
            if (it.isSuccessful) {
                val editor = sharedPreferences.edit()
                editor.putString(SHARED_PREFERENCES_NAME, email)
                editor.apply()

                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
                makeText(this, "Signed up successfully!", Toast.LENGTH_LONG).show()

            } else makeText(this, it.exception?.message, Toast.LENGTH_SHORT).show()
        }
        else makeText(this, "Email or Password Cannot be empty!", Toast.LENGTH_SHORT).show()
    }

}

