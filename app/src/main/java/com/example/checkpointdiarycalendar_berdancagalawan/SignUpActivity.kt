package com.example.checkpointdiarycalendar_berdancagalawan

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class SignUpActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        // Edge-to-Edge UI
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.sign_up_main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = FirebaseAuth.getInstance()

        val userEt: EditText = findViewById(R.id.usernameEt)
        val emailEt: EditText = findViewById(R.id.emailEt)
        val passwordEt: EditText = findViewById(R.id.passEt)
        val confirmpasswordEt: EditText = findViewById(R.id.confirmPassEt)
        val registerButton: Button = findViewById(R.id.registerbutton)
        val txtToSignIn: Button = findViewById(R.id.textViewMove)

        txtToSignIn.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }

        registerButton.setOnClickListener {
            val username = userEt.text.toString().trim()
            val email = emailEt.text.toString().trim()
            val password = passwordEt.text.toString().trim()
            val confirmPassword = confirmpasswordEt.text.toString().trim()

            if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Please fill out all fields.", Toast.LENGTH_SHORT).show()
            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Please enter a valid email address.", Toast.LENGTH_SHORT).show()
            } else if (password != confirmPassword) {
                Toast.makeText(this, "Passwords do not match.", Toast.LENGTH_SHORT).show()
            } else {
                // Firebase Authentication: Create user
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val currentUser = auth.currentUser
                            if (currentUser != null) {
                                val uid = currentUser.uid

                                // Add user details to Firestore
                                val newUser = hashMapOf(
                                    "uid" to uid,
                                    "username" to username,
                                    "email" to email,
                                    "created_at" to LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                                )

                                firestore.collection("tbl_User").document(uid).set(newUser)
                                    .addOnSuccessListener {
                                        Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show()
                                        val intent = Intent(this, SignInActivity::class.java)
                                        startActivity(intent)
                                    }
                                    .addOnFailureListener { e ->
                                        Toast.makeText(this, "Error saving user data: ${e.message}", Toast.LENGTH_SHORT).show()
                                    }
                            }
                        } else {
                            Toast.makeText(this, "Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }
    }
}
