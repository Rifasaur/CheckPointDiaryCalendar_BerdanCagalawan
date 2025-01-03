package com.example.checkpointdiarycalendar_berdancagalawan

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Handle edge-to-edge layout adjustments
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize Firestore instance
        firestore = FirebaseFirestore.getInstance()

        // Get references to UI components
        val imgtodisplay: ImageView = findViewById(R.id.viewEntryDiary)
        val imgtocreate: ImageView = findViewById(R.id.createEntryDiary)
        val imgtoprofile: ImageView = findViewById(R.id.openprofile)
        val vlayout: LinearLayout = findViewById(R.id.LBC_Mainlayoutvert)

        // Set up navigation button actions
        imgtodisplay.setOnClickListener {
            startActivity(Intent(this, DisplayEntryActivity::class.java))
        }

        imgtocreate.setOnClickListener {
            startActivity(Intent(this, CreateEntryActivity::class.java))
        }

        imgtoprofile.setOnClickListener {
            startActivity(Intent(this, DisplayProfile::class.java))
        }

        // Load entries from Firestore
        loadEntries(vlayout)
    }

    private fun loadEntries(vlayout: LinearLayout) {
        firestore.collection("tbl_Diary").get()
            .addOnSuccessListener { records ->
                if (records.isEmpty) {
                    Log.d("Firestore", "No entries found.")
                    return@addOnSuccessListener
                }

                for (post in records) {
                    // Inflate the entry card template
                    val inflater = LayoutInflater.from(this)
                    val template = inflater.inflate(R.layout.activity_entry_card, vlayout, false)

                    // Get references to the entry card UI components
                    val LBCcontent: TextView = template.findViewById(R.id.EntryFormat_MainContent)
                    val LBCtitle: TextView = template.findViewById(R.id.EntryFormat_Title)
                    val LBCfavorite: ImageView = template.findViewById(R.id.EntryFormat_Favorite)
                    val LBCdate: TextView = template.findViewById(R.id.EntryFormat_Date)
                    val LBCdelete: ImageView = template.findViewById(R.id.EntryFormat_Delete)

                    // Populate entry card data
                    val content = post.getString("content") ?: "No content"
                    val title = post.getString("title") ?: "No title"
                    val date = post.getString("date") ?: "No date"
                    val recordId = post.id

                    LBCcontent.text = content
                    LBCtitle.text = title
                    LBCdate.text = date

                    // Handle favorite action
                    LBCfavorite.setOnClickListener {
                        toggleFavorite(recordId, LBCfavorite)
                    }

                    // Handle delete action
                    LBCdelete.setOnClickListener {
                        if (recordId != null && vlayout != null && template != null) {
                            showDeleteConfirmation(recordId, vlayout, template)
                        } else {
                            Log.e("MainActivity", "Null values detected: recordId=$recordId, vlayout=$vlayout, template=$template")
                        }
                    }

                    // Add the entry card to the vertical layout
                    vlayout.addView(template)
                }
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore", "Error fetching records: ${exception.message}")
            }
    }

    private fun toggleFavorite(recordId: String, favoriteIcon: ImageView) {
        firestore.collection("tbl_Diary").document(recordId)
            .update("favorite", true)
            .addOnSuccessListener {
                favoriteIcon.setImageResource(R.drawable.baseline_favorite_24)
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore", "Error updating favorite: ${exception.message}")
            }
    }

    private fun showDeleteConfirmation(recordId: String, vlayout: LinearLayout, template: View) {
        AlertDialog.Builder(this)
            .setTitle("Delete Confirmation")
            .setMessage("Are you sure you want to delete this entry?")
            .setPositiveButton("Yes") { _, _ ->
                deleteEntry(recordId, vlayout, template)
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun deleteEntry(recordId: String, vlayout: LinearLayout, template: View) {
        firestore.collection("tbl_Diary").document(recordId).delete()
            .addOnSuccessListener {
                vlayout.removeView(template)
                Log.d("Firestore", "Entry deleted successfully.")
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore", "Error deleting entry: ${exception.message}")
            }
    }
}
