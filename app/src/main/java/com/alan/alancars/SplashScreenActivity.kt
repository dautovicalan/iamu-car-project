package com.alan.alancars

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.alan.alancars.databinding.ActivitySplashScreenBinding
import com.alan.alancars.framework.callDelayed
import com.alan.alancars.framework.*
import com.google.firebase.auth.FirebaseAuth


private const val DELAY = 3000L

class SplashScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashScreenBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        startAnimations()
        redirect()
    }

    private fun startAnimations() {
        binding.tvSplash.applyAnimation(R.anim.blink)
        binding.ivSplash.applyAnimation(R.anim.rotate)
    }

    private fun redirect() {

        if (auth.currentUser != null) {
            callDelayed(DELAY) {
                Intent(this, HomeMainActivity::class.java).also {
                    it.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(it)
                    finish()
                }
            }
        } else {
            callDelayed(DELAY) {
                Intent(this, LoginActivity::class.java).also {
                    it.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(it)
                    finish()
                }
            }
        }
    }

}