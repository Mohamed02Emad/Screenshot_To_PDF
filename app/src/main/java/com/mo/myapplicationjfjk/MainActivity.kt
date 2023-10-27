package com.mo.myapplicationjfjk

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import com.mo.myapplicationjfjk.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private var screenshotBitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        checkForPermissions()
        setOnClicks()
    }

    private fun setOnClicks() {
        binding.apply {

            btn.setOnClickListener {
                takeScreenShot()
            }

            btn2.setOnClickListener {
                savePdf()
            }

        }
    }

    private fun checkForPermissions() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                1
            )
        }
    }

    private fun takeScreenShot() {
        hideViewsForScreenShot()
        screenshotBitmap = captureScreenShot(window)
        showViewsForScreenShot()
        binding.ivScreenShot.setImageBitmap(screenshotBitmap)
    }

    private fun savePdf() {
        screenshotBitmap?.let {
            val pdf = createPdfFromBitmap(it)
            savePdfToExternalStorage(pdf, "test Pdf", this)
        } ?: Toast.makeText(this, "take a screenshot", Toast.LENGTH_SHORT).show()
    }

    private fun hideViewsForScreenShot() {
        binding.btn.isGone = true
        binding.btn2.isGone = true
    }

    private fun showViewsForScreenShot() {
        binding.btn.isGone = false
        binding.btn2.isGone = false
    }

}
