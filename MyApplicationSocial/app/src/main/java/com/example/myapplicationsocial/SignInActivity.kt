package com.example

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import com.example.myapplicationsocial.MainActivity
import com.example.myapplicationsocial.SignUpActivity
import com.example.myapplicationsocial.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_sign_in.*
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignInActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        // Rediriger vers l'activité d'inscription lors du clic sur le bouton "Sign Up"
        signup_link_btn.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        // Appeler la fonction loginUser lors du clic sur le bouton "Login"
        login_btn.setOnClickListener {
            loginUser()
        }
    }

    private fun loginUser() {
        val email = email_login.text.toString() // Récupérer l'e-mail saisi
        val password = password_login.text.toString() // Récupérer le mot de passe saisi

        when {
            TextUtils.isEmpty(email) -> Toast.makeText(this, "email is required.", Toast.LENGTH_LONG).show() // Afficher un message d'erreur si l'e-mail est vide
            TextUtils.isEmpty(password) -> Toast.makeText(this, "password is required.", Toast.LENGTH_LONG).show() // Afficher un message d'erreur si le mot de passe est vide
            else -> {
                val progressDialog = ProgressDialog(this@SignInActivity) // Créer une boîte de dialogue de progression
                progressDialog.setTitle("Login") // Définir le titre de la boîte de dialogue
                progressDialog.setMessage("Please wait, this may take a while...") // Définir le message de la boîte de dialogue
                progressDialog.setCanceledOnTouchOutside(false) // Empêcher la fermeture de la boîte de dialogue lors du toucher à l'extérieur
                progressDialog.show() // Afficher la boîte de dialogue de progression

                val mAuth: FirebaseAuth = FirebaseAuth.getInstance() // Obtenir l'instance de l'authentification Firebase

                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        progressDialog.dismiss() // Fermer la boîte de dialogue de progression

                        // Rediriger vers l'activité principale en cas de succès de connexion
                        val intent = Intent(this@SignInActivity, MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finish()
                    } else {
                        val message = task.exception!!.toString()

                        // Afficher un message d'erreur en cas d'échec de connexion
                        Toast.makeText(this, "Error: $message", Toast.LENGTH_LONG).show()

                        // Se déconnecter de l'utilisateur actuel
                        FirebaseAuth.getInstance().signOut()
                        progressDialog.dismiss()
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()

        // Vérifier si un utilisateur est déjà connecté lors du démarrage de l'activité
        if (FirebaseAuth.getInstance().currentUser != null) {
            // Rediriger vers l'activité principale si un utilisateur est déjà connecté
            val intent = Intent(this@SignInActivity, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }
}
