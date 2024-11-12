package com.example.myapplicationsocial.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplicationsocial.Adapter.PostAdapter
import com.example.myapplicationsocial.Model.Post
import com.example.myapplicationsocial.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HomeFragment : Fragment()
{
    private var postAdapter : PostAdapter?=null
    private var postList: MutableList<Post>?=null
    private var followingList:MutableList<Post>?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_home, container, false)

        var recyclerView: RecyclerView? = null
        recyclerView = view.findViewById(R.id.recycler_view_home)
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.reverseLayout=true
        linearLayoutManager.stackFromEnd=true
        recyclerView.layoutManager=linearLayoutManager


        postList=ArrayList()
        postAdapter=context?.let { PostAdapter(it,postList as ArrayList<Post>) }
        recyclerView.adapter = postAdapter

        checkFollowing()

        return view
    }

    private fun checkFollowing()
    {
        followingList=ArrayList()
        val followingRef= FirebaseDatabase.getInstance().reference
                .child("Follow").child(FirebaseAuth.getInstance().currentUser!!.uid)
                .child("Following")
        followingRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(snapshot: DataSnapshot)
            {
                if(snapshot.exists())
                {
                    (followingList as ArrayList<String>).clear()
                    for (pO in snapshot.children)
                    {
                        pO.key?.let { (followingList as ArrayList<String>).add(it) }
                    }
                    retrivePosts()
                }
            }

            override fun onCancelled(error: DatabaseError)
            {

            }
        })
    }

    private fun retrivePosts() {
        val postsRef= FirebaseDatabase.getInstance().reference.child("Posts")
        postsRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(snapshot: DataSnapshot) {
                postList?.clear()
                for(pO in snapshot.children)
                {
                    val post=pO.getValue(Post ::class.java)
                    for(id in (followingList as ArrayList<String>))
                    {
                        if(post!!.getPublisher()==id)
                        {
                            postList!!.add(post)
                        }
                        postAdapter!!.notifyDataSetChanged()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

    }
}