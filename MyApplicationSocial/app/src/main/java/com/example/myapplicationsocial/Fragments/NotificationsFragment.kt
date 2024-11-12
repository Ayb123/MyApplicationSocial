package com.example.myapplicationsocial.Fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplicationsocial.Adapter.followingAdapter
import com.example.myapplicationsocial.Chat_activity
import com.example.myapplicationsocial.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_notifications.*

class NotificationsFragment : Fragment(), followingAdapter.OnItemClickListener {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Récupérer et afficher la liste des utilisateurs suivis
        getFollowingUsers()
    }

    // Récupérer la liste des utilisateurs suivis depuis la base de données
    private fun getFollowingUsers() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val followingRef = FirebaseDatabase.getInstance().reference
            .child("Follow").child(currentUser?.uid.toString()).child("Following")

        followingRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val followingList = ArrayList<String>()
                for (snapshot in dataSnapshot.children) {
                    val followingUserId = snapshot.key
                    followingUserId?.let {
                        followingList.add(it)
                    }
                }

                // Vérifier si le fragment est attaché à l'activité avant d'appeler la méthode pour afficher les utilisateurs suivis
                if (isAdded) {
                    displayFollowingUsers(followingList)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Gérer l'annulation de la récupération des données
                // Afficher un message d'erreur ou effectuer une autre action appropriée
            }
        })
    }

    // Afficher les utilisateurs suivis dans le RecyclerView
    private fun displayFollowingUsers(followingList: List<String>) {
        // Créer un adaptateur pour le RecyclerView
        val adapter = followingAdapter(followingList)
        adapter.setOnItemClickListener(this)

        // Configurer le RecyclerView avec l'adaptateur et le gestionnaire de disposition
        val layoutManager = LinearLayoutManager(requireContext())
        recycler_view_notification.layoutManager = layoutManager
        recycler_view_notification.adapter = adapter
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notifications, container, false)
    }

    // Méthode appelée lorsqu'un utilisateur est cliqué dans le RecyclerView
    override fun onItemClick(userId: String) {
        // L'utilisateur a été cliqué, vous pouvez maintenant passer à la page de chat
        // Vous pouvez utiliser une intention (Intent) pour passer à la page de chat en transmettant l'ID de l'utilisateur cliqué
        val intent = Intent(requireContext(), Chat_activity::class.java)
        intent.putExtra("receiverId", userId)
        startActivity(intent)
    }
}
