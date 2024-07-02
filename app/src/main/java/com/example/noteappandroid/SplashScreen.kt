package com.example.noteappandroid

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.noteappandroid.adapter.CircleIndicatorAdapter
import com.example.noteappandroid.databinding.ActivitySplashScreenBinding
import com.example.noteappandroid.entity.PhotoSplash

class SplashScreen : AppCompatActivity() {
    private lateinit var binding: ActivitySplashScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val indicators = binding.circleIndicator3
        val listItemPhoto = mutableListOf<PhotoSplash>()
        listItemPhoto.add(
            PhotoSplash(
                R.raw.splash2,
                resources.getString(R.string.splash_text1)
            )
        )
        listItemPhoto.add(
            PhotoSplash(
                R.raw.splash4,
                resources.getString(R.string.splash_text2)
            )
        )
        listItemPhoto.add(
            PhotoSplash(
                R.raw.splash5,
                resources.getString(R.string.splash_text3)
            )
        )
        listItemPhoto.add(
            PhotoSplash(
                R.raw.splash6,
                resources.getString(R.string.splash_text4)
            )
        )
        val adapterPhotoSplash = CircleIndicatorAdapter(listItemPhoto)
        binding.viewPagerSplash.adapter = adapterPhotoSplash
        indicators.setViewPager(binding.viewPagerSplash)

        Handler(Looper.getMainLooper()).postDelayed(
            {
                val i = Intent(this, MainActivity::class.java)
                startActivity(i)
            }, 3000
        )
    }
}