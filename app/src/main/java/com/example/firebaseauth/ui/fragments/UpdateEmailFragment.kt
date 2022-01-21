package com.example.firebaseauth.ui.fragments

import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.example.firebaseauth.R
import com.example.firebaseauth.toast
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import kotlinx.android.synthetic.main.fragment_update_email.*
import kotlinx.android.synthetic.main.fragment_update_email.edit_text_password


class UpdateEmailFragment : Fragment() {

    private val currentUser = FirebaseAuth.getInstance().currentUser
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_update_email, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        layoutPassword.visibility = View.VISIBLE
        layoutUpdateEmail.visibility = View.GONE

        button_authenticate.setOnClickListener {
            val password = edit_text_password.text.toString().trim()

            if (password.isEmpty()){
                edit_text_password.error = "Password is Required"
                edit_text_password.requestFocus()
                return@setOnClickListener
            }

            currentUser?.let { user->
                val credential = EmailAuthProvider.getCredential(user.email!!,password)
                progressbar.visibility = View.VISIBLE
                user.reauthenticate(credential)
                    .addOnCompleteListener { task->
                        progressbar.visibility = View.GONE
                        when {
                            task.isSuccessful -> {
                                layoutPassword.visibility = View.GONE
                                layoutUpdateEmail.visibility = View.VISIBLE
                            }
                            task.exception is FirebaseAuthInvalidCredentialsException -> {
                                edit_text_password.error = "Invalid Password"
                                edit_text_password.requestFocus()
                            }
                            else -> {
                                context?.toast(task.exception?.message!!)
                            }
                        }
                    }
            }

        }

        button_update.setOnClickListener {
            val email = edit_text_email.text.toString().trim()

            if (email.isEmpty()){
                edit_text_email.error = "Email Required"
                edit_text_email.requestFocus()
                return@setOnClickListener
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                edit_text_email.error = "Valid Email Required"
                edit_text_email.requestFocus()
                return@setOnClickListener
            }

            currentUser?.let { user->
                progressbar.visibility = View.VISIBLE
                user.updateEmail(email)
                    .addOnCompleteListener { task->
                        progressbar.visibility = View.GONE
                        if (task.isSuccessful){
                            val action = UpdateEmailFragmentDirections.actionEmailUpdated()
                            Navigation.findNavController(it).navigate(action)
                        }
                        else{
                            context?.toast(task.exception?.message!!)
                        }
                    }
            }

        }

    }


}