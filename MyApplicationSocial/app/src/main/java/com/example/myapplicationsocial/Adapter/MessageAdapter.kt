package com.example.myapplicationsocial.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplicationsocial.Model.Message
import com.example.myapplicationsocial.R
import com.google.firebase.auth.FirebaseAuth

class MessageAdapter(private val messageList: List<Message>) : RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    // Crée un nouveau ViewHolder en inflatant la vue correspondante au type de message
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return MessageViewHolder(view)
    }

    // Récupère le type de vue en fonction de l'expéditeur du message (current user ou autre utilisateur)
    val currentUser = FirebaseAuth.getInstance().currentUser
    override fun getItemViewType(position: Int): Int {
        val message = messageList[position]
        return if (message.senderId == currentUser?.uid) {
            R.layout.item_chat_right // Vue pour le message de l'utilisateur courant
        } else {
            R.layout.item_chat_left // Vue pour le message des autres utilisateurs
        }
    }

    // Lie les données du message à la vue du ViewHolder
    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messageList[position]
        holder.bind(message)
    }

    // Renvoie le nombre total de messages dans la liste
    override fun getItemCount(): Int {
        return messageList.size
    }

    // ViewHolder pour afficher chaque message dans le RecyclerView
    inner class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val messageTextView: TextView = itemView.findViewById(R.id.tv_Msg)

        // Lie les données du message à la vue du ViewHolder
        fun bind(message: Message) {
            messageTextView.text = message.content
        }
    }
}
