package com.example.myapplicationsocial.Adapter

import android.view.ViewGroup
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplicationsocial.Model.Post
import androidx.annotation.NonNull
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.recyclerview.widget.ListAdapter
import com.example.myapplicationsocial.Model.User
import com.example.myapplicationsocial.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_account_settings.*

class PostAdapter
    (private val mContext : Context,
     private val mPost:List<Post>) : RecyclerView.Adapter<PostAdapter.ViewHolder>()
{
    private var firebaseUser: FirebaseUser? = null
   inner class ViewHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView)
   {
       var profileImage:CircleImageView
       var postImage:ImageView
       var LikeButton:ImageView
       var CommentButton:ImageView
       var SaveButton:ImageView
       var userName:TextView
       var likes:TextView
       var publisher:TextView
       var description:TextView
       var comment:TextView

       init {
           profileImage = itemView.findViewById(R.id.user_profile_image_post)
           postImage = itemView.findViewById(R.id.post_image_home)
           LikeButton = itemView.findViewById(R.id.post_image_like_btn)
           CommentButton = itemView.findViewById(R.id.post_image_comment_btn)
           SaveButton = itemView.findViewById(R.id.post_save_comment_btn)
           userName = itemView.findViewById(R.id.user_name_post)
           likes = itemView.findViewById(R.id.likes)
           publisher = itemView.findViewById(R.id.publisher)
           description = itemView.findViewById(R.id.description)
           comment = itemView.findViewById(R.id.comments)
       }
   }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
    {
        val view = LayoutInflater.from(mContext).inflate(R.layout.posts_layout, parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int
    {
        return mPost.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int)
    {
        firebaseUser = FirebaseAuth.getInstance().currentUser
        val post=mPost[position]
        Picasso.get().load(post.getPostimage()).into(holder.postImage)
        publisherInfo(holder.profileImage,holder.userName,holder.publisher,post.getPublisher())

    }

    private fun publisherInfo(profileImage: CircleImageView, userName: TextView, publisher: TextView, publisherID: String)
    {
        val usersRef = FirebaseDatabase.getInstance().reference.child("Users").child(publisherID)
        usersRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(pO: DataSnapshot)
            {
                if(pO.exists())
                {
                    val user =pO.getValue<User>(User::class.java)
                    Picasso.get().load(user!!.getImage()).placeholder(R.drawable.profile).into(profileImage)
                    userName.text = user!!.getUsername()
                    publisher.text=user!!.getFullname()
                }
            }

            override fun onCancelled(error: DatabaseError)
            {

            }
        })
    }
}