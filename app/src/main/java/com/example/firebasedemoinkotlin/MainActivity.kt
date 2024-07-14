package com.example.firebasedemoinkotlin

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import java.util.ArrayList

class MainActivity : AppCompatActivity() {

    private lateinit var name: EditText
    private lateinit var email: EditText
    private lateinit var register: Button

    val db = FirebaseFirestore.getInstance()
    val allDataList = ArrayList<UserModel>()

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ReadDataAdapter

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        name = findViewById(R.id.et_name)
        email = findViewById(R.id.et_email)
        register = findViewById(R.id.register_btn)
        recyclerView = findViewById(R.id.recyclerview_one)

        // Initialize RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        //here using lambda expression we can also pass name-> name or userId->Userid in UserModel
        adapter = ReadDataAdapter(allDataList) { userId-> deleteUser(userId) }
        recyclerView.adapter = adapter

        register.setOnClickListener {
            val name1 = name.text.toString().trim()
            val email1 = email.text.toString().trim()

            val userId = db.collection("user").document().id

            if (name1.isEmpty()) {
                name.error = "Name is required"
                return@setOnClickListener
            }
            if (email1.isEmpty()) {
                email.error = "Email is required"
                return@setOnClickListener
            }
            val data = UserModel(name1, email1, userId)
            db.collection("user").document(userId).set(data).addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(this, "Data added", Toast.LENGTH_LONG).show()
                    name.text.clear()
                    email.text.clear()
                    getAllData()  // Refresh data after adding new entry
                } else {
                    Toast.makeText(this, "Error..!!", Toast.LENGTH_LONG).show()
                }
            }
        }

        getAllData()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun getAllData() {
        db.collection("user").addSnapshotListener { value: QuerySnapshot?, error: FirebaseFirestoreException? ->
            if (value != null && !value.isEmpty) {
                allDataList.clear()  // Clear the list to avoid duplicates
                for (document in value.documents) {
                    val user = document.toObject(UserModel::class.java)
                    if (user != null) {
                        allDataList.add(user)
                    }
                }
                adapter.notifyDataSetChanged()
            }
        }
    }

    private fun deleteUser(userId: String) {
        db.collection("user").document(userId).delete().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Delete Done", Toast.LENGTH_LONG).show()
                getAllData()  // Refresh data after deletion
            } else {
                Toast.makeText(this, "Failed to delete..!!", Toast.LENGTH_LONG).show()
            }
        }
    }
}
