package com.alan.alancars

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.alan.alancars.databinding.ActivityLoginBinding
import com.alan.alancars.framework.startActivity
import com.google.firebase.auth.FirebaseAuth


class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()

        setupListeners()
    }

    private fun setupListeners() {
        binding.btnLogin.setOnClickListener {
            loginUser()
        }
        binding.tvNewUserPrompt.setOnClickListener {
            startActivity<RegisterActivity>()
        }
    }

    private fun loginUser() {
        if (formValid()){
            binding.loadingPanel.visibility = View.VISIBLE
            auth.signInWithEmailAndPassword(
                binding.txtInputEmail.text.toString(),
                binding.txtInputPassword.text.toString()
            )
                .addOnCompleteListener(this){ it ->
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
            binding.loadingPanel.visibility = View.INVISIBLE
        }
    }

    private fun formValid(): Boolean {
        var ok = true

        arrayOf(binding.txtInputEmail, binding.txtInputPassword)
            .forEach {
                if (it.text?.trim()?.isBlank() == true) {
                    ok = false
                    it.error = "Please insert"
                }
            }

        return ok
    }
}