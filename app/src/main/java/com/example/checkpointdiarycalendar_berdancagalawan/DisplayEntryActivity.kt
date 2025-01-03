package com.example.checkpointdiarycalendar_berdancagalawan

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SearchView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class DisplayEntryActivity: AppCompatActivity() {
    val conn = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_entry_display)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_entry_display_main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val imgtocreate: ImageView = findViewById(R.id.createEntryDiary)
        val imgtoprofile: ImageView = findViewById(R.id.openprofile)
        val imgtohome: Button = findViewById(R.id.nav_Title)

        val vlayout: LinearLayout = findViewById(R.id.LBC_Mainlayoutvert)
        val LBCsearch: SearchView = findViewById(R.id.LBC_Search)

        conn.collection("tbl_Diary").get().addOnSuccessListener {
                records ->
            val record = records
            for(post in record){

                val Inflater = LayoutInflater.from(this)
                val template = Inflater.inflate(R.layout.activity_entry_card, vlayout, false)

                val LBCcontent: TextView = template.findViewById(R.id.EntryFormat_MainContent)
                val LBCtitle : TextView = template.findViewById(R.id.EntryFormat_Title)
                val LBCfavorite: ImageView = template.findViewById(R.id.EntryFormat_Favorite)
                val LBCdate : TextView = template.findViewById(R.id.EntryFormat_Date)
                val LBCdelete: ImageView = template.findViewById(R.id.EntryFormat_Delete)

                LBCcontent.text = post.getString("content")
                LBCtitle.text = post.getString("title")

                val created_at = post.getString("date")
                val created_at_date = LocalDateTime.parse(created_at, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                LBCdate.text = created_at_date?.format(DateTimeFormatter.ofPattern("MM dd"))

                val record_id = post.id

                LBCfavorite.setOnClickListener{
                    conn.collection("tbl_Diary").document(record_id)
                        .update("favorite", true).addOnSuccessListener {
                            LBCfavorite.setImageResource(R.drawable.baseline_favorite_24)
                        }
                }
                LBCdelete.setOnClickListener{
                    AlertDialog.Builder(this)
                        .setTitle("Delete Notification")
                        .setMessage("Are you sure you want to proceed with this action?")
                        .setPositiveButton("Yes"){
                                _,_ ->
                            conn.collection("tbl_post").document(record_id).delete()
                                .addOnSuccessListener { vlayout.removeView(template) }
                        }
                        .setNegativeButton("No", null).show()
                }
                vlayout.addView(template)
            }
        LBCsearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(searchinput: String?): Boolean {
                //search input
                val searchInput = searchinput ?: ""
                //filtered record after search submit
                val filtered_post = record.filter {
                    it.getString("title")?.contains(searchInput,true) == true
                }
                vlayout.removeAllViews()
                for(post in record){

                    val Inflater = LayoutInflater.from(this@DisplayEntryActivity)
                    val template = Inflater.inflate(R.layout.activity_entry_card, vlayout, false)

                    val LBCcontent: TextView = template.findViewById(R.id.EntryFormat_MainContent)
                    val LBCtitle : TextView = template.findViewById(R.id.EntryFormat_Title)
                    val LBCfavorite: ImageView = template.findViewById(R.id.EntryFormat_Favorite)
                    val LBCdate : TextView = template.findViewById(R.id.EntryFormat_Date)
                    val LBCdelete: ImageView = template.findViewById(R.id.EntryFormat_Delete)

                    LBCcontent.text = post.getString("content")
                    LBCtitle.text = post.getString("title")

                    val created_at = post.getString("date")

                    val created_at_date = LocalDateTime.parse(created_at, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                    LBCdate.text = created_at_date?.format(DateTimeFormatter.ofPattern("MM dd"))

                    val record_id = post.id

                    LBCfavorite.setOnClickListener{
                        conn.collection("tbl_Diary").document(record_id)
                            .update("favorite", true).addOnSuccessListener {
                                LBCfavorite.setImageResource(R.drawable.baseline_favorite_24)
                            }
                    }
                    LBCdelete.setOnClickListener{
                        AlertDialog.Builder(this@DisplayEntryActivity)
                            .setTitle("Delete Notification")
                            .setMessage("Are you sure you want to proceed with this action?")

                            .setPositiveButton("Yes"){
                                    _,_ ->
                                conn.collection("tbl_post").document(record_id).delete()
                                    .addOnSuccessListener { vlayout.removeView(template) }
                            }
                            .setNegativeButton("No", null).show()

                    }
                    vlayout.addView(template)
                }
                return true
            }

        })
        }
        imgtocreate.setOnClickListener {
            val intent = Intent(this, CreateEntryActivity::class.java)
            startActivity(intent) }
        imgtoprofile.setOnClickListener {
            val intent = Intent(this, DisplayProfile::class.java)
            startActivity(intent) }
        imgtohome.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent) }
    }
}