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


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


/**
 * A simple [Fragment] subclass.
 * Use the [ProfilFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment() {
    private lateinit var profileId:String
    private lateinit var firebaseUser: FirebaseUser1

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!

        val pref = context?.getSharedPreferences("PREFS",Context.MODE_PRIVATE)
        if(pref !=null)
        {
            this.profileId = pref.getString("profileId","none").toString()
        }
        if(profileId==firebaseUser.uid)
        {
            view.edit_account_settings.text = "Edite Profile"
        }
        else if(profileId!=firebaseUser.uid)
        {
            checkFollowAndFollowingButtonStatus()
        }
view.edit_account_settings.setOnClickListener {
    val getButtonText = view.edit_account_settings.text.toString()
    when{
        getButtonText == "Edite Profile"->startActivity(Intent(context,AccountSettingsActivity::class.java))

        getButtonText =="Follow"->{
            firebaseUser?.uid.let { it1->
                FirebaseDatabase.getInstance().reference
                    .child("Follow").child(it1.toString())
                    .child("Following").child(profileId)
                    .setValue(true)
            }
            firebaseUser?.uid.let { it1->
                FirebaseDatabase.getInstance().reference
                    .child("Follow").child(profileId)
                    .child("Followers").child(it1.toString())
                    .setValue(true)
            }
        }
        getButtonText =="Following"->{
            firebaseUser?.uid.let { it1->
                FirebaseDatabase.getInstance().reference
                    .child("Follow").child(it1.toString())
                    .child("Following").child(profileId)
                    .removeValue()
            }
            firebaseUser?.uid.let { it1->
                FirebaseDatabase.getInstance().reference
                    .child("Follow").child(profileId)
                    .child("Followers").child(it1.toString())
                    .removeValue()
            }
        }
    }
}
        getFollowers()
        getFollowings()
        userInfo()

        return view
    }

    private fun checkFollowAndFollowingButtonStatus() {
        val followingRef = firebaseUser.uid.let { it1 ->
            FirebaseDatabase.getInstance().reference
                .child("Follow").child(it1.toString())
                .child("Following")
        }
        if (followingRef != null) {
            followingRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.child(profileId).exists()) {
                        view?.edit_account_settings?.text = "Following"
                    } else {
                        view?.edit_account_settings?.text = "Follow"
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
        }
}
    private fun getFollowers()
        {
            val followersRef= FirebaseDatabase.getInstance().reference
                    .child("Follow").child(profileId)
                    .child("Followers")

            followersRef.addValueEventListener(object :ValueEventListener
            {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists())
                    {
                        view?.total_followers?.text = snapshot.childrenCount.toString()
                    }
                }
                override fun onCancelled(error: DatabaseError) {

                }
            })
    }
    private fun getFollowings()
    {
        val followersRef= FirebaseDatabase.getInstance().reference
                .child("Follow").child(profileId)
                .child("Following")

        followersRef.addValueEventListener(object :ValueEventListener
        {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists())
                {
                    view?.total_following?.text = snapshot.childrenCount.toString()
                }
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun userInfo(){
        val usersRef=FirebaseDatabase.getInstance().reference.child("Users").child(profileId)
        usersRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists())
                {
                    val user =snapshot.getValue<User>(User::class.java)
                    Picasso.get().load(user!!.getImage()).placeholder(R.drawable.profile).into(view?.pro_image_profile_fag)
                    view?.profile_fragment_username?.text=user!!.getUsername()
                    view?.full_name_profile_frag?.text=user!!.getFullname()
                    view?.bio_profile_frag?.text=user!!.getBio()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    override fun onStop() {
        super.onStop()
        val pref = context?.getSharedPreferences("PREFS",Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId",firebaseUser.uid)
        pref?.apply()
    }

    override fun onPause() {
        super.onPause()
        val pref = context?.getSharedPreferences("PREFS",Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId",firebaseUser.uid)
        pref?.apply()
    }

    override fun onDestroy() {
        super.onDestroy()
        val pref = context?.getSharedPreferences("PREFS",Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId",firebaseUser.uid)
        pref?.apply()
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ProfilFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}

private fun Any.setOnClickListener(view: Any) {

}
