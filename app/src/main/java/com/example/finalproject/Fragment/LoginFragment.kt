package com.example.finalproject.Fragment

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.example.finalproject.MainScreenActivity

import com.example.finalproject.R
import com.example.finalproject.ResetPasswordActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_login.*

class LoginFragment : Fragment() {

    lateinit var Login : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Reset.setOnClickListener{
            val intent = Intent(this@LoginFragment.context, ResetPasswordActivity::class.java)
            startActivity(intent)
        }


        Login = Login_button
        Login.setOnClickListener {
            val Email = Register_Email.text.toString()
            val Password = Reset_password.text.toString()
            if(Email.isEmpty()||Password.isEmpty()){
                Toast.makeText(context,"Please input Email and Password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            FirebaseAuth.getInstance().signInWithEmailAndPassword(Email, Password)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        Log.d(ContentValues.TAG, "login successful")
                        val intent = Intent(this@LoginFragment.context,MainScreenActivity::class.java)
                        startActivity(intent)

                    } else {

                        Log.w(ContentValues.TAG, "Failure", it.exception)
                        Toast.makeText(context, "Authentication failed", Toast.LENGTH_SHORT)
                            .show()
                    }


                }
        }

    }


}
