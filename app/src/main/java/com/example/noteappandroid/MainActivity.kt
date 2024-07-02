package com.example.noteappandroid

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import android.window.OnBackInvokedDispatcher
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private var doubleBackToExitPressedOnce = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if (Build.VERSION.SDK_INT >= 33) {
            onBackInvokedDispatcher.registerOnBackInvokedCallback(
                OnBackInvokedDispatcher.PRIORITY_DEFAULT
            ) {
                if (doubleBackToExitPressedOnce) {
                    super.onBackPressed()
                    finishAffinity()
                } else {
                    this.doubleBackToExitPressedOnce = true
                    Toast.makeText(
                        this,
                        resources.getString(R.string.back_press_again_to_out_app),
                        Toast.LENGTH_SHORT
                    ).show()

                    Handler().postDelayed({
                        doubleBackToExitPressedOnce = false
                    }, 2000)
                }
            }
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        navController = findNavController(R.id.navHostFragmentContainerView)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

//    @SuppressLint("MissingSuperCall")
//    @Deprecated(
//        "This method has been deprecated in favor of using the\n      {@link OnBackPressedDispatcher} via {@link #getOnBackPressedDispatcher()}.\n      The OnBackPressedDispatcher controls how back button events are dispatched\n      to one or more {@link OnBackPressedCallback} objects.",
//        ReplaceWith("super.onBackPressed()", "androidx.appcompat.app.AppCompatActivity")
//    )

//    override fun onBackPressed() {
//        if (doubleBackToExitPressedOnce) {
//            super.onBackPressed()
//            finishAffinity()
//        } else {
//            this.doubleBackToExitPressedOnce = true
//            Toast.makeText(
//                this,
//                resources.getString(R.string.back_press_again_to_out_app),
//                Toast.LENGTH_SHORT
//            ).show()
//
//            Handler().postDelayed({
//                doubleBackToExitPressedOnce = false
//            }, 2000)
//        }
//    }

}