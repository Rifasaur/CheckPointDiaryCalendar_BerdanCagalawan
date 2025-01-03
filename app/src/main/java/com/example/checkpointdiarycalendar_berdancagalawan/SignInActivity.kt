package com.example.checkpointdiarycalendar_berdancagalawan
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SignInActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.sign_in_main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        val signin: Button = findViewById(R.id.signinbutton)
        val txttosignup: Button = findViewById(R.id.textViewMove)
        val emailEt: EditText = findViewById(R.id.emailEt)
        val passwordEt: EditText = findViewById(R.id.passEt)

        txttosignup.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        signin.setOnClickListener {
            val email = emailEt.text.toString().trim()
            val password = passwordEt.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter both email and password.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Firebase Authentication Sign-In
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val currentUser = auth.currentUser
                        if (currentUser != null) {
                            val uid = currentUser.uid

                            // Fetch user details from Firestore
                            firestore.collection("tbl_User")
                                .document(uid)
                                .get()
                                .addOnSuccessListener { document ->
                                    if (document != null && document.exists()) {
                                        val intent = Intent(this, DisplayProfile::class.java)
                                        intent.putExtra("uid", uid) // Pass the current user's UID
                                        startActivity(intent)
                                        Toast.makeText(this, "Sign In Successful!", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(this, "User data not found.", Toast.LENGTH_SHORT).show()
                                    }
                                }
                                .addOnFailureListener { exception ->
                                    Toast.makeText(this, "Error: ${exception.message}", Toast.LENGTH_SHORT).show()
                                }
                        }
                    } else {
                        Toast.makeText(this, "Authentication failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}
