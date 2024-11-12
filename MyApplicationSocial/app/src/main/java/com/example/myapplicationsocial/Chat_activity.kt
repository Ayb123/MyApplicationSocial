package com.example.myapplicationsocial

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplicationsocial.Model.Message
import com.example.myapplicationsocial.Adapter.MessageAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class Chat_activity : AppCompatActivity() {
    private lateinit var userRef: DatabaseReference
    private lateinit var fabSendMessage: FloatingActionButton
    private lateinit var editMessage: EditText
    private lateinit var rvChatList: RecyclerView
    private lateinit var messageAdapter: MessageAdapter
    private val messageList: MutableList<Message> = mutableListOf() // Liste des messages

    private var senderId: String? = null
    private lateinit var userId: String
    private lateinit var messagesRef: DatabaseReference
    private var receiverId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        // Récupérer les références des vues
        rvChatList = findViewById(R.id.rv_chat)
        editMessage = findViewById(R.id.editMessage)
        fabSendMessage = findViewById(R.id.fabsendMessage)

        // Créer l'adaptateur et le lier au RecyclerView
        messageAdapter = MessageAdapter(messageList)
        rvChatList.adapter = messageAdapter
        rvChatList.layoutManager = LinearLayoutManager(this)

        // Récupérer l'ID de l'utilisateur courant et de l'utilisateur avec lequel on chatte
        senderId = FirebaseAuth.getInstance().currentUser!!.uid
        receiverId = intent.getStringExtra("userId")

        // Référence à la base de données des messages
        messagesRef = FirebaseDatabase.getInstance().reference.child("Messages")

        // Référence à la base de données des utilisateurs
        userRef = FirebaseDatabase.getInstance().reference.child("Users")

        // Récupérer l'ID de l'utilisateur courant depuis la base de données
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val currentUserId = currentUser.uid
            userRef.child(currentUserId).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        userId = dataSnapshot.key.toString()
                    } else {
                        // L'utilisateur n'existe pas ou aucune correspondance trouvée
                        Toast.makeText(this@Chat_activity, "ID de l'utilisateur introuvable", Toast.LENGTH_SHORT).show()
                        finish() // Terminer l'activité ou effectuer une autre action appropriée
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Gérer les erreurs lors de la récupération de l'ID de l'utilisateur depuis la base de données
                    Toast.makeText(this@Chat_activity, "Erreur lors de la récupération de l'ID de l'utilisateur", Toast.LENGTH_SHORT).show()
                    finish() // Terminer l'activité ou effectuer une autre action appropriée
                }
            })
        } else {
            // L'utilisateur n'est pas connecté
            Toast.makeText(this@Chat_activity, "Utilisateur non connecté", Toast.LENGTH_SHORT).show()
            finish() // Terminer l'activité ou effectuer une autre action appropriée
        }

        // Écouter les changements dans les messages de la base de données
        messagesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                messageList.clear() // Effacer la liste actuelle des messages

                // Parcourir les messages de la base de données
                for (messageSnapshot in dataSnapshot.children) {
                    val message = messageSnapshot.getValue(Message::class.java)
                    if (message != null && message.receiverId == receiverId && message.senderId == senderId) {
                        messageList.add(message) // Ajouter les messages correspondant à la liste
                    }
                }

                messageAdapter.notifyDataSetChanged() // Mettre à jour l'adaptateur avec les nouveaux messages
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@Chat_activity, "Erreur lors de la récupération des messages", Toast.LENGTH_SHORT).show()
            }
        })

        // Gérer le clic sur le bouton d'envoi de message
        fabSendMessage.setOnClickListener {
            val message = editMessage.text.toString()
            if (message.isNotEmpty()) {
                sendMessage(message) // Envoyer le message
                editMessage.setText("") // Effacer le champ de saisie du message
            }
        }
    }

    // Fonction pour envoyer un message à la base de données
    private fun sendMessage(message: String) {
        val messageKey = messagesRef.push().key
        val newMessage = Message(senderId ?: "", receiverId ?: "", message)

        if (messageKey != null) {
            messagesRef.child(messageKey).setValue(newMessage)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Message envoyé avec succès", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Erreur lors de l'envoi du message", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}
