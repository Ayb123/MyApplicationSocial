package com.example.myapplicationsocial.Adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplicationsocial.Chat_activity
import com.example.myapplicationsocial.Model.User
import com.example.myapplicationsocial.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

class followingAdapter(private val followingList: List<String>) : RecyclerView.Adapter<followingAdapter.ViewHolder>() {

    // Crée un nouveau ViewHolder en inflatant la vue correspondante à l'élément de la liste
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.following, parent, false)
        return ViewHolder(view)
    }

    // Interface pour définir un écouteur de clic d'élément de la liste
    interface OnItemClickListener {
        fun onItemClick(userId: String)
    }

    private var itemClickListener: OnItemClickListener? = null

    // Définit l'écouteur de clic d'élément de la liste
    fun setOnItemClickListener(listener: OnItemClickListener) {
        itemClickListener = listener
    }

    // Lie les données de l'utilisateur à l'élément de la liste
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val userId = followingList[position]

        // Récupère les informations de l'utilisateur à partir de la base de données Firebase
        val userRef = FirebaseDatabase.getInstance().reference.child("Users").child(userId)
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val user = dataSnapshot.getValue(User::class.java)
                    if (user != null) {
                        // Utilise les données de l'utilisateur pour les afficher dans l'élément de la liste
                        holder.bind(user)
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Gère les erreurs lors de la récupération des données de l'utilisateur
            }
        })

        // Définit le clic de l'élément de la liste en fonction de l'ID de l'utilisateur
        holder.itemView.setOnClickListener {
            val userId = followingList[position]
            itemClickListener?.onItemClick(userId)
        }
    }

    // Renvoie le nombre total d'éléments dans la liste
    override fun getItemCount(): Int {
        return followingList.size
    }

    // ViewHolder pour afficher chaque élément de la liste
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val userNameTextView: TextView = itemView.findViewById(R.id.usernameTextView)
        private val userProfileImageView: ImageView = itemView.findViewById(R.id.profileImageView)

        // Lie les données de l'utilisateur à l'élément de la liste
        fun bind(user: User) {
            userNameTextView.text = user.getUsername()

            // Charge l'image de profil de l'utilisateur à l'aide d'une bibliothèque comme Picasso ou Glide
            // Exemple avec Picasso :
            Picasso.get().load(user.getImage()).into(userProfileImageView)

            // Définit le clic de l'élément de la liste pour ouvrir l'activité de chat avec cet utilisateur
            itemView.setOnClickListener{
                Intent(itemView.context, Chat_activity::class.java).also {
                    it.putExtra("user", user.getUsername())
                    itemView.context.startActivity(it)
                }
            }
        }
    }
}
