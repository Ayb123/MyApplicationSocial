package com.example.myapplicationsocial




import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.example.SignInActivity
import com.example.myapplicationsocial.Model.User
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCanceledListener
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_account_settings.*
import kotlinx.android.synthetic.main.fragment_profile.view.*
import java.nio.file.attribute.GroupPrincipal

class AccountSettingsActivity : AppCompatActivity()
{

    private lateinit var firebaseUser: FirebaseUser

    private var checker =""
    private var myUrl=""
    private var imageUri:Uri?=null
    private var storageProfilePicRef: StorageReference?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_settings)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        storageProfilePicRef = FirebaseStorage.getInstance().reference.child("Profile Pictures")

        delete_account_btn.setOnClickListener {
            val user = FirebaseAuth.getInstance().currentUser
            val userRef = FirebaseDatabase.getInstance().reference.child("Users").child(user?.uid ?: "")

            // Supprimer l'utilisateur actuel de l'authentification Firebase
            user?.delete()?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Supprimer l'utilisateur de la base de données Firebase
                    userRef.removeValue().addOnCompleteListener { dbTask ->
                        if (dbTask.isSuccessful) {
                            Toast.makeText(
                                this@AccountSettingsActivity,
                                "Account deleted successfully.",
                                Toast.LENGTH_SHORT
                            ).show()

                            // Rediriger vers l'activité de connexion
                            val intent = Intent(this@AccountSettingsActivity, SignInActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(
                                this@AccountSettingsActivity,
                                "Failed to delete account. Please try again.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } else {
                    Toast.makeText(
                        this@AccountSettingsActivity,
                        "Failed to delete account. Please try again.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }


        logout_btn.setOnClickListener(){
            FirebaseAuth.getInstance().signOut()
            val intent = Intent (this@AccountSettingsActivity, SignInActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }

        change_image_text_btn.setOnClickListener {
            checker="clicked"

            CropImage.activity().setAspectRatio(1,1)
                .start(this@AccountSettingsActivity)
        }

        save_infor_profile_btn.setOnClickListener{
            if(checker=="clicked")
            {
                uploadImageAndUpdateInfo()
            }
            else
            {
                updateUserInfoOnly()
            }
        }

        userInfo()
    }

    private fun uploadImageAndUpdateInfo()
    {

        when {
            imageUri == null-> {Toast.makeText(this,"Please select Image.",Toast.LENGTH_LONG).show()}
            TextUtils.isEmpty(full_name_profile_frag.text.toString())-> {Toast.makeText(this,"Please write full name .",Toast.LENGTH_LONG).show()}
            username_profile_frag.text.toString() == "" -> {Toast.makeText(this,"Please write Username.",Toast.LENGTH_LONG).show()}
            else->{

                val progressDialog=ProgressDialog(this)
                progressDialog.setTitle("Account Settings")
                progressDialog.setMessage("Please wait, we are Updating your profile...")
                progressDialog.show()

                val fileRef=storageProfilePicRef!!.child(firebaseUser!!.uid +".jpg")

                var uploadTask:StorageTask<*>
                uploadTask=fileRef.putFile(imageUri!!)
                uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot,Task<Uri>> { task ->
                    if(!task.isSuccessful)
                    {
                        task.exception?.let {
                            throw it
                            progressDialog.dismiss()
                        }
                    }
                    return@Continuation fileRef.downloadUrl
                }).addOnCompleteListener ( OnCompleteListener<Uri> {task->
                    if(task.isSuccessful)
                    {
                        val downloadUrl=task.result
                        myUrl = downloadUrl.toString()
                        val ref= FirebaseDatabase.getInstance().reference.child("Users")

                        val userMap = HashMap<String, Any>()
                        userMap["fullname"] = full_name_profile_frag.text.toString().toLowerCase()
                        userMap["username"] = username_profile_frag.text.toString().toLowerCase()
                        userMap["image"] = myUrl
                        ref.child(firebaseUser.uid).updateChildren(userMap)
                        Toast.makeText(
                            this,
                            "Account Information has been updated successfully.",
                            Toast.LENGTH_LONG
                        ).show()
                        val intent = Intent (this@AccountSettingsActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                        progressDialog.dismiss()
                    }
                    else
                    {
                        progressDialog.dismiss()
                    }
                } )

            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE&&resultCode==Activity.RESULT_OK&& data!=null)
        {
            val result=CropImage.getActivityResult(data)
            imageUri=result.uri
            profile_image_view_profile_flag.setImageURI(imageUri)

        }
    }

    private fun updateUserInfoOnly() {
        when {
            full_name_profile_frag.text.toString()=="" -> {
                Toast.makeText(
                    this,
                    "Please write full name .",
                    Toast.LENGTH_LONG
                ).show()
            }
            username_profile_frag.text.toString()=="" -> {
                Toast.makeText(
                    this,
                    "Please write Username.",
                    Toast.LENGTH_LONG
                ).show()
            }
            else -> {
                val usersRef= FirebaseDatabase.getInstance().reference.child("Users")

                val userMap = HashMap<String, Any>()
                userMap["fullname"] = full_name_profile_frag.text.toString().toLowerCase()
                userMap["username"] = username_profile_frag.text.toString().toLowerCase()
                userMap["bio"] = bio_profile_frag.text.toString().toLowerCase()
                usersRef.child(firebaseUser.uid).updateChildren(userMap)
                Toast.makeText(
                    this,
                    "Account Information has been updated successfully.",
                    Toast.LENGTH_LONG
                ).show()
                val intent = Intent (this@AccountSettingsActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

    }

    private fun userInfo(){
        val usersRef= FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser.uid)
        usersRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists())
                {
                    val user =snapshot.getValue<User>(User::class.java)
                    Picasso.get().load(user!!.getImage()).placeholder(R.drawable.profile).into(profile_image_view_profile_flag)
                    username_profile_frag.setText(user!!.getUsername())
                    full_name_profile_frag.setText(user!!.getFullname())
                    bio_profile_frag.setText(user!!.getBio())
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
}