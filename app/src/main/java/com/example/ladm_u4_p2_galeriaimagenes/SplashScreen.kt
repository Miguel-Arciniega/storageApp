package com.example.ladm_u4_p2_galeriaimagenes

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.ladm_u4_p2_galeriaimagenes.databinding.ActivitySplashScreenBinding

class SplashScreen : AppCompatActivity() {
    lateinit var binding : ActivitySplashScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.inicio.alpha = 0f
        binding.inicio.animate().setDuration(1500).alpha(1f).withEndAction {
            val i = Intent(this,Login::class.java)
            startActivity(i)
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        supportActionBar!!.hide()
    }

    override fun onStop() {
        super.onStop()
        supportActionBar!!.show()
    }
}