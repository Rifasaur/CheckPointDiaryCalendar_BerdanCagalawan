package com.example.checkpointdiarycalendar_berdancagalawan

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class DisplayProfile : AppCompatActivity() {
    private lateinit var firestore: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile_display)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_profile_main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        firestore = FirebaseFirestore.getInstance()

        val uid = intent.getStringExtra("uid")
        val profileN: TextView = findViewById(R.id.profileName)
        val profileE: TextView = findViewById(R.id.profileEmail)
        val logoutButton: Button = findViewById(R.id.logout)

        val imgtodisplay: ImageView = findViewById(R.id.viewEntryDiary)
        val imgtocreate: ImageView = findViewById(R.id.createEntryDiary)
        val imgtohome: Button = findViewById(R.id.nav_Title)

        imgtodisplay.setOnClickListener {
            val intent = Intent(this, DisplayEntryActivity::class.java)
            startActivity(intent)
        }
        imgtocreate.setOnClickListener {
            val intent = Intent(this, CreateEntryActivity::class.java)
            startActivity(intent) }
        imgtohome.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("uid", uid) // Pass the current user's UID
            startActivity(intent) }
        if (uid != null) {
            // Fetch user details from Firestore

            firestore.collection("tbl_User")
                .document(uid)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val userName = document.getString("username") ?: "N/A"
                        val userEmail = document.getString("email") ?: "N/A"

                        profileN.text = userName
                        profileE.text = userEmail

                    } else {
                        profileN.text = "User data not found."
                        profileE.text = "User data not found."
                    }
                }
                .addOnFailureListener { exception ->
                    profileN.text = "Error: ${exception.message}"
                }
        } else {
            profileN.text = "No UID passed."
        }

        logoutButton.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, SignInActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }


    }
}