package com.example.finalproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_reset_password.*

class ResetPasswordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)


        Reset_Button.setOnClickListener{
            val email = Reset_email.text.toString()

            Log.d("Reset","email"+ email)
            if(email.isEmpty()){
                Toast.makeText(this,"Please enter valid email", Toast.LENGTH_SHORT).show()
            }else{
                FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                    .addOnCompleteListener{
                        if(it.isSuccessful){
                            Toast.makeText(this,"Reset email has been sent", Toast.LENGTH_SHORT).show()
                            UpdateUI()
                        }
                        else{
                            Toast.makeText(this,"No user found with this email", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }

    }
    private fun UpdateUI(){
        val intent = Intent(this,MainActivity::class.java)
        startActivity(intent)
    }

}