package com.alan.alancars

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.alan.alancars.databinding.ActivityRegisterBinding
import com.alan.alancars.framework.startActivity
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()

        setupListeners()
    }

    private fun setupListeners() {
        binding.btnRegister.setOnClickListener {
            if (formValid()){
                registerUser()
            }
        }
        binding.tvAlreadyAccount.setOnClickListener {
            startActivity<LoginActivity>()
        }
    }

    private fun formValid(): Boolean {
        var ok = true

        arrayOf(binding.txtInputEmail, binding.txtInputPassword, binding.txtInputPasswordRepeat)
            .forEach {
                if (it.text?.trim()?.isBlank() == true){
                    it.error = "Please insert"
                    ok = false
                }
            }

        return ok && binding.txtInputPassword.text.toString() == binding.txtInputPasswordRepeat.text.toString()
    }

    private fun registerUser() {
        auth.createUserWithEmailAndPassword(
            binding.txtInputEmail.text.toString().trim(),
            binding.txtInputPassword.text.toString().trim()
        )
            .addOnCompleteListener(this) { it ->
                if (it.isSuccessful) {
                    Intent(this, HomeMainActivity::class.java).also {
                        it.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                        startActivity(it)
                        finish()
                    }
                } else {
                    Toast.makeText(this, it.exception?.message.toString(), Toast.LENGTH_SHORT)
                        .show()
                }
            }
    }
}