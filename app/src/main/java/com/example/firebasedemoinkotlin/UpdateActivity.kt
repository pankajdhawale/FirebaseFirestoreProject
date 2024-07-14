package com.example.firebasedemoinkotlin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore

class UpdateActivity : AppCompatActivity() {
    private lateinit var key: String
    private val db = FirebaseFirestore.getInstance()

    private lateinit var name: EditText
    private lateinit var email: EditText
    private lateinit var btnUpdate: Button
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update)

        key = intent.getStringExtra("key").toString()
        Log.e("pankaj", "Received user id: $key")

        name = findViewById(R.id.et_name)
        email = findViewById(R.id.et_email)
        btnUpdate = findViewById(R.id.btn_update_data)
        progressBar = findViewById(R.id.progress_bar)

        btnUpdate.setOnClickListener {
            val name1 = name.text.toString().trim()
            val email1 = email.text.toString().trim()

            if (name1.isEmpty()) {
                name.error = "Name is required"
                return@setOnClickListener
            }
            if (email1.isEmpty()) {
                email.error = "Email is required"
                return@setOnClickListener
            }

            progressBar.visibility = View.VISIBLE
            db.collection("user").document(key).update("name", name1, "email", email1).addOnCompleteListener { task ->
                progressBar.visibility = View.GONE
                if (task.isSuccessful) {
                    Toast.makeText(this, "Data successfully updated", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this, "Error: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
        }

        getAllData(key)
    }

    private fun getAllData(key: String) {
        progressBar.visibility = View.VISIBLE
        db.collection("user").document(key).get().addOnSuccessListener { document ->
            progressBar.visibility = View.GONE
            if (document != null) {
                val personData = document.toObject(UserModel::class.java)
                if (personData != null) {
                    name.setText(personData.name)
                    email.setText(personData.email)
                }
            }
        }.addOnFailureListener { exception ->
            progressBar.visibility = View.GONE
            Toast.makeText(this, "Error: ${exception.message}", Toast.LENGTH_LONG).show()
        }
    }
}
