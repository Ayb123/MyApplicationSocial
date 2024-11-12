package com.example.myapplicationsocial.Fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.myapplicationsocial.AccountSettingsActivity
import com.example.myapplicationsocial.Model.User
import com.example.myapplicationsocial.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_profile.view.*
import com.google.firebase.auth.FirebaseUser as FirebaseUser1

class ProfileFragment : Fragment() {
    private lateinit var profileId:String
    private lateinit var firebaseUser: FirebaseUser1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!

        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)
        if (pref != null) {
            this.profileId = pref.getString("profileId", "none").toString()
        }

        // Modifier le texte du bouton en fonction de l'utilisateur
        if (profileId == firebaseUser.uid) {
            view.edit_account_settings.text = "Edite Profile"
        } else if (profileId != firebaseUser.uid) {
            checkFollowAndFollowingButtonStatus()
        }

        // Gérer le clic sur le bouton de modification du compte
        view.edit_account_settings.setOnClickListener {
            val getButtonText = view.edit_account_settings.text.toString()
            when {
                getButtonText == "Edite Profile" -> startActivity(Intent(context, AccountSettingsActivity::class.java))

                getButtonText == "Follow" -> {
                    // Suivre l'utilisateur
                    firebaseUser?.uid.let { it1 ->
                        FirebaseDatabase.getInstance().reference
                            .child("Follow").child(it1.toString())
                            .child("Following").child(profileId)
                            .setValue(true)
                    }
                    firebaseUser?.uid.let { it1 ->
                        FirebaseDatabase.getInstance().reference
                            .child("Follow").child(profileId)
                            .child("Followers").child(it1.toString())
                            .setValue(true)
                    }
                }
                getButtonText == "Following" -> {
                    // Arrêter de suivre l'utilisateur
                    firebaseUser?.uid.let { it1 ->
                        FirebaseDatabase.getInstance().reference
                            .child("Follow").child(it1.toString())
                            .child("Following").child(profileId)
                            .removeValue()
                    }
                    firebaseUser?.uid.let { it1 ->
                        FirebaseDatabase.getInstance().reference
                            .child("Follow").child(profileId)
                            .child("Followers").child(it1.toString())
                            .removeValue()
                    }
                }
            }
        }

        // Obtenir les informations sur les abonnés et les abonnements
        getFollowers()
        getFollowings()

        // Obtenir les informations de l'utilisateur et les afficher
        userInfo()

        return view
    }

    // Vérifier l'état du bouton de suivi/suivi
    private fun checkFollowAndFollowingButtonStatus() {
        val followingRef = firebaseUser.uid.let { it1 ->
            FirebaseDatabase.getInstance().reference
                .child("Follow").child(it1.toString())
                .child("Following")
        }

        if (followingRef != null) {
            followingRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    // Vérifier si l'utilisateur est suivi ou non
                    if (snapshot.child(profileId).exists()) {
                        view?.edit_account_settings?.text = "Following"
                    } else {
                        view?.edit_account_settings?.text = "Follow"
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Gérer l'annulation de la récupération des données
                    // Afficher un message d'erreur ou effectuer une autre action appropriée
                }
            })
        }
    }

    // Obtenir le nombre d'abonnés
    private fun getFollowers() {
        val followersRef = FirebaseDatabase.getInstance().reference
            .child("Follow").child(profileId)
            .child("Followers")

        followersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    view?.total_followers?.text = snapshot.childrenCount.toString()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Gérer l'annulation de la récupération des données
                // Afficher un message d'erreur ou effectuer une autre action appropriée
            }
        })
    }

    // Obtenir le nombre d'abonnements
    private fun getFollowings() {
        val followersRef = FirebaseDatabase.getInstance().reference
            .child("Follow").child(profileId)
            .child("Following")

        followersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    view?.total_following?.text = snapshot.childrenCount.toString()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Gérer l'annulation de la récupération des données
                // Afficher un message d'erreur ou effectuer une autre action appropriée
            }
        })
    }

    // Obtenir les informations de l'utilisateur et les afficher
    private fun userInfo() {
        val usersRef = FirebaseDatabase.getInstance().reference.child("Users").child(profileId)

        usersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user = snapshot.getValue<User>(User::class.java)
                    Picasso.get().load(user!!.getImage()).placeholder(R.drawable.profile).into(view?.pro_image_profile_fag)
                    view?.profile_fragment_username?.text = user!!.getUsername()
                    view?.full_name_profile_frag?.text = user!!.getFullname()
                    view?.bio_profile_frag?.text = user!!.getBio()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Gérer l'annulation de la récupération des données
                // Afficher un message d'erreur ou effectuer une autre action appropriée
            }
        })
    }

    override fun onStop() {
        super.onStop()
        // Enregistrer l'ID de profil de l'utilisateur dans les préférences partagées lors de l'arrêt du fragment
        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId", firebaseUser.uid)
        pref?.apply()
    }

    override fun onPause() {
        super.onPause()
        // Enregistrer l'ID de profil de l'utilisateur dans les préférences partagées lors de la mise en pause du fragment
        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId", firebaseUser.uid)
        pref?.apply()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Enregistrer l'ID de profil de l'utilisateur dans les préférences partagées lors de la destruction du fragment
        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId", firebaseUser.uid)
        pref?.apply()
    }
}
