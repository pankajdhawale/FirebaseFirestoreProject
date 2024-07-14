package com.example.firebasedemoinkotlin

import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class ReadDataAdapter(private val userList: List<UserModel>, private val deleteUser: (String) -> Unit) : RecyclerView.Adapter<ReadDataAdapter.UserViewHolder>() {

    val db = FirebaseFirestore.getInstance()

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.tv_name)
        val email: TextView = itemView.findViewById(R.id.tv_email)
        val btnUpdate: Button = itemView.findViewById(R.id.update_btn)
        val btnDelete: Button = itemView.findViewById(R.id.delete_btn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.single_item, parent, false)
        return UserViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val currentItem = userList[position]
        holder.name.text = currentItem.name
        holder.email.text = currentItem.email

        holder.btnUpdate.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, UpdateActivity::class.java).apply {
                putExtra("key", currentItem.userid)
            }
            context.startActivity(intent)
        }

        holder.btnDelete.setOnClickListener {
            val context = holder.itemView.context
            val builder = AlertDialog.Builder(context)
            builder.setMessage("Are you sure you want to delete this entry?")
                .setCancelable(false)
                .setPositiveButton("Yes") { dialogInterface, _ ->
                    deleteUser(currentItem.userid)
                    dialogInterface.dismiss()
                }
                .setNegativeButton("No") { dialogInterface, _ ->
                    dialogInterface.dismiss()
                }
            val alert = builder.create()
            alert.show()
        }
    }

    override fun getItemCount() = userList.size
}
