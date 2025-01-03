package com.example.checkpointdiarycalendar_berdancagalawan

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.*
import androidx.appcompat.widget.AppCompatButton
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class CreateEntryActivity : AppCompatActivity() {
    val conn = FirebaseFirestore.getInstance()
    private var selectedDate: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_entry_creation)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_entry_creation_main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val imgtodisplay: ImageView = findViewById(R.id.viewEntryDiary)
        val imgtoprofile: ImageView = findViewById(R.id.openprofile)
        val imgtohome: Button = findViewById(R.id.nav_Title)

        imgtodisplay.setOnClickListener {
            val intent = Intent(this, DisplayEntryActivity::class.java)
            startActivity(intent)
        }
        imgtoprofile.setOnClickListener {
            val intent = Intent(this, DisplayProfile::class.java)
            startActivity(intent)
        }
        imgtohome.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent) }

        val calendarView: CalendarView = findViewById(R.id.calendarView)
        val txtentryname: EditText = findViewById(R.id.titleET)
        val txtmood: EditText = findViewById(R.id.moodET)
        val txtcontent: EditText = findViewById(R.id.contentET)
        val sub_button: AppCompatButton = findViewById(R.id.sub_button)

        // Set a listener on the CalendarView to capture the selected date
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            // CalendarView uses 0-based indexing for months, so add 1 to the month
            val calendar = Calendar.getInstance()
            calendar.set(year, month, dayOfMonth)
            val sdf = SimpleDateFormat("MM dd", Locale.getDefault())
            selectedDate = sdf.format(calendar.time) // Save the selected date as a formatted string
        }

        sub_button.setOnClickListener {
            val title = txtentryname.text.toString()
            val mood = txtmood.text.toString()
            val content = txtcontent.text.toString()

            if (title.isEmpty() || mood.isEmpty() || content.isEmpty() || selectedDate.isEmpty()) {
                Toast.makeText(this, "Please fill out all fields and select a date.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val new_entry = hashMapOf(
                "title" to title,
                "mood" to mood,
                "content" to content,
                "date" to selectedDate
            )

            // Add the entry to Firestore
            conn.collection("tbl_Diary").add(new_entry)
                .addOnSuccessListener {
                    Toast.makeText(this, "Entry saved successfully!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, DisplayEntryActivity::class.java)
                    startActivity(intent)
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
