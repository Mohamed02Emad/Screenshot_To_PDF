package com.mo.myapplicationjfjk

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import androidx.documentfile.provider.DocumentFile
import com.mo.myapplicationjfjk.databinding.ActivityMainBinding
import java.io.File
import java.io.FileInputStream

class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private var screenshotBitmap: Bitmap? = null

    private val storagePermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            takeScreenShot()
        }
    }

    val OPEN_DIRECTORY_REQUEST =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                if (data != null) {
                    val uri = data.data
                    val fileName = "testPdf.pdf"
                    Log.d(TAG, fileName )
                    copyPdfToLocal(uri!!, fileName , this@MainActivity)
                }
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setOnClicks()
    }
    private fun setOnClicks() {
        binding.apply {
            btn.setOnClickListener {
                askForStoragePermissions()
            }
            btn2.setOnClickListener {
                savePdf()
            }
        }
    }
    private fun askForStoragePermissions() =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            storagePermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
        } else {
            storagePermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
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

            val savedPDFFile = savePDFToAppCacheFiles(pdf, "testPdf", this)

            savedPDFFile?.let { cachedPDF ->
                savePdfToLocal()
            }

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
    private fun showToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }

    fun savePdfToLocal() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
        OPEN_DIRECTORY_REQUEST.launch(intent)
    }


}