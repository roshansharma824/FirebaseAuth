package com.example.firebaseauth.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import com.example.firebaseauth.R
import com.example.firebaseauth.toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_reset_password.*
import kotlinx.android.synthetic.main.activity_reset_password.text_email

class ResetPasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        button_reset_password.setOnClickListener {
            val email = text_email.text.toString().trim()

            if (email.isEmpty()){
                text_email.error = "Email Required"
                text_email.requestFocus()
                return@setOnClickListener
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                text_email.error = "Valid Email Required"
                text_email.requestFocus()
                return@setOnClickListener
            }
            progressbar.visibility = View.VISIBLE

            FirebaseAuth.getInstance()
                .sendPasswordResetEmail(email)
                .addOnCompleteListener { task->
                    progressbar.visibility = View.GONE
                    if (task.isSuccessful){
                        this.toast("Check your email")
                        startActivity(Intent(this@ResetPasswordActivity, LoginActivity::class.java))
                    }
                    else{
                        this.toast(task.exception?.message!!)
                    }
                }
        }
    }
}