package com.example.myapplicationsocial

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.example.SignInActivity
import com.example.myapplicationsocial.MainActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_sign_up.*
import com.google.firebase.auth.FirebaseAuth as FirebaseAuth1

class SignUpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        // Rediriger vers l'activité de connexion lors du clic sur le bouton "Sign In"
        signin_link_btn.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
        }

        // Appeler la fonction CreateAccount lors du clic sur le bouton "Sign Up"
        signup_btn.setOnClickListener {
            CreateAccount()
        }
    }

    private fun CreateAccount() {
        val fullName = fullname_signup.text.toString() // Récupérer le nom complet saisi
        val userName = username_signup.text.toString() // Récupérer le nom d'utilisateur saisi
        val email = email_signup.text.toString() // Récupérer l'e-mail saisi
        val password = password_signup.text.toString() // Récupérer le mot de passe saisi

        when {
            TextUtils.isEmpty(fullName) -> Toast.makeText(this, "full name is required.", Toast.LENGTH_LONG).show() // Afficher un message d'erreur si le nom complet est vide
            TextUtils.isEmpty(userName) -> Toast.makeText(this, "user name is required.", Toast.LENGTH_LONG).show() // Afficher un message d'erreur si le nom d'utilisateur est vide
            TextUtils.isEmpty(email) -> Toast.makeText(this, "email is required.", Toast.LENGTH_LONG).show() // Afficher un message d'erreur si l'e-mail est vide
            TextUtils.isEmpty(password) -> Toast.makeText(this, "password is required.", Toast.LENGTH_LONG).show() // Afficher un message d'erreur si le mot de passe est vide
            else -> {
                val progressDialog = ProgressDialog(this@SignUpActivity) // Créer une boîte de dialogue de progression
                progressDialog.setTitle("Signup") // Définir le titre de la boîte de dialogue
                progressDialog.setMessage("Please wait, this may take a while...") // Définir le message de la boîte de dialogue
                progressDialog.setCanceledOnTouchOutside(false) // Empêcher la fermeture de la boîte de dialogue lors du toucher à l'extérieur
                progressDialog.show() // Afficher la boîte de dialogue de progression

                val mAuth: FirebaseAuth1 = FirebaseAuth1.getInstance() // Obtenir l'instance de l'authentification Firebase

                mAuth.createUserWithEmailAndPassword(email, password) // Créer un compte utilisateur avec l'e-mail et le mot de passe saisis
                    .addOnCompleteListener() { task ->
                        if (task.isSuccessful) {
                            // Enregistrer les informations de l'utilisateur dans la base de données
                            saveUserInfo(fullName, userName, email, progressDialog)
                        } else {
                            val message = task.exception!!.toString()

                            // Afficher un message d'erreur en cas d'échec de création du compte
                            Toast.makeText(this, "Error: $message", Toast.LENGTH_LONG).show()

                            // Se déconnecter de l'utilisateur actuel
                            mAuth.signOut()
                            progressDialog.dismiss()
                        }
                    }
            }
        }
    }

    private fun saveUserInfo(fullName: String, userName: String, email: String, progressDialog: ProgressDialog) {
        val currentUserID = FirebaseAuth1.getInstance().currentUser!!.uid // Obtenir l'ID de l'utilisateur actuel
        val usersRef: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Users") // Référence à la table "Users" dans la base de données Firebase
        val userMap = HashMap<String, Any>() // Créer une map pour stocker les informations de l'utilisateur
        userMap["uid"] = currentUserID // Ajouter l'ID de l'utilisateur à la map
        userMap["fullname"] = fullName.toLowerCase() // Ajouter le nom complet de l'utilisateur (en minuscules) à la map
        userMap["username"] = userName.toLowerCase() // Ajouter le nom d'utilisateur de l'utilisateur (en minuscules) à la map
        userMap["email"] = email // Ajouter l'e-mail de l'utilisateur à la map
        userMap["bio"] = "hey je suis developpeur" // Ajouter une biographie par défaut à la map
        userMap["image"] = "https://firebasestorage.googleapis.com/v0/b/myapplicationsocial-bd59a.appspot.com/o/Default%20Images%2Fprofile.png?alt=media&token=31552268-aedb-45e1-bdc8-bd9cbcb79c73" // Ajouter une image de profil par défaut à la map

        usersRef.child(currentUserID).setValue(userMap) // Enregistrer les informations de l'utilisateur dans la base de données Firebase
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    progressDialog.dismiss() // Fermer la boîte de dialogue de progression
                    Toast.makeText(this, "Account has been created successfully.", Toast.LENGTH_LONG).show() // Afficher un message de succès

                    // Ajouter l'utilisateur actuel à la liste des utilisateurs suivis (Following)
                    FirebaseDatabase.getInstance().reference
                        .child("Follow").child(currentUserID)
                        .child("Following").child(currentUserID)
                        .setValue(true)

                    // Rediriger vers l'activité principale après la création du compte
                    val intent = Intent(this@SignUpActivity, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                } else {
                    val message = task.exception!!.toString()

                    // Afficher un message d'erreur en cas d'échec d'enregistrement des informations de l'utilisateur
                    Toast.makeText(this, "Error: $message", Toast.LENGTH_LONG).show()

                    FirebaseAuth1.getInstance().signOut()
                    progressDialog.dismiss()
                }
            }
    }
}
