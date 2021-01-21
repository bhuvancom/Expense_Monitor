package com.redcat.expensemonitor.ui

import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.redcat.expensemonitor.R
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        //val anim = AnimationUtils.loadAnimation(applicationContext, R.anim.abc)
        //iv.startAnimation(anim)
        iv.performClick()
        object : Thread() {
            override fun run() {
                try {
                    sleep(1000)
          //          anim.reset()
                    startMainActivity()
                } catch (error: InterruptedException) {
                    startMainActivity()
                    error.printStackTrace()
                }
            }
        }.start()
    }

    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finishAffinity()
    }
}