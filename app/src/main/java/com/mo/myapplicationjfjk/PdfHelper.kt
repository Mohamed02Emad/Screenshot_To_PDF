package com.mo.myapplicationjfjk

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.util.Log
import android.view.Window
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.documentfile.provider.DocumentFile
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

val TAG = "PdfHelper "

fun captureScreenShot(window: Window): Bitmap {
    val rootView = window.decorView.rootView
    val screenBitmap = Bitmap.createBitmap(rootView.width, rootView.height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(screenBitmap)
    rootView.draw(canvas)
    return screenBitmap
}

fun createPdfFromBitmap(bitmap: Bitmap): PdfDocument {
    val pdfDocument = PdfDocument()
    val pageInfo = PdfDocument.PageInfo.Builder(bitmap.width, bitmap.height, 1).create()
    val page = pdfDocument.startPage(pageInfo)
    val canvas = page.canvas
    canvas.drawBitmap(bitmap, 0f, 0f, null)
    pdfDocument.finishPage(page)
    return pdfDocument
}

fun savePDFToAppCacheFiles(pdfDocument: PdfDocument, fileName: String, context: Context): File? {
    val dir = File(context.cacheDir, "PDFs")
    if (!dir.exists()) {
        dir.mkdirs()
    }
    val file = File(dir, "$fileName.pdf")
    return try {
        val fos = FileOutputStream(file)
        pdfDocument.writeTo(fos)
        fos.close()
        pdfDocument.close()
        Log.d(TAG, "savePDFToApplicationFiles: ${file.absolutePath}")
        file
    } catch (e: Exception) {
        e.printStackTrace()
        pdfDocument.close()
        null
    }
}

fun sharePdfWithDownloadOption(
    context: Context,
    pdfFile: File,
) {

    val uri = FileProvider.getUriForFile(context, "com.mo.myapplicationjfjk.fileprovider", pdfFile)

    Log.d(TAG, "sharePdfWithDownloadOption: ${pdfFile.absolutePath}")

    val shareIntent = Intent(Intent.ACTION_SEND)
    shareIntent.type = "application/pdf"
    shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
    shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

    val downloadIntent = Intent(Intent.ACTION_CREATE_DOCUMENT)
    downloadIntent.addCategory(Intent.CATEGORY_OPENABLE)
    downloadIntent.type = "application/pdf"
    downloadIntent.putExtra(Intent.EXTRA_TITLE, pdfFile.name)

    val chooserIntent = Intent.createChooser(shareIntent, "Share PDF using")

    val targetIntents = arrayOf(downloadIntent)
    chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetIntents)

    context.startActivity(chooserIntent)
}

fun sharePdf(context: Context, pdfFile: File) {
    if (pdfFile.exists().not()) {
        return
    }

    val uri = FileProvider.getUriForFile(context, "com.mo.myapplicationjfjk.fileprovider", pdfFile)

    val shareIntent = Intent(Intent.ACTION_SEND)
    shareIntent.type = "application/pdf"
    shareIntent.putExtra(Intent.EXTRA_STREAM, uri)

    context.startActivity(Intent.createChooser(shareIntent, "Share PDF using"))
}



@SuppressLint("Recycle")
fun copyPdfToLocal(destinationUri: Uri, fileName: String, context: Context) {
    val sourceFilePath = File(context.cacheDir, "PDFs").absolutePath + File.separator + fileName

    try {
        val sourceFile = File(sourceFilePath)
        val sourceStream = FileInputStream(sourceFile)

        val destinationDocumentFile = DocumentFile.fromTreeUri(context, destinationUri)
        val destinationFile = destinationDocumentFile!!.createFile("application/pdf", fileName)

        val destinationStream = context.contentResolver.openOutputStream(destinationFile!!.uri)
        val buffer = ByteArray(8192)
        var read: Int

        while (sourceStream.read(buffer).also { read = it } != -1) {
            destinationStream!!.write(buffer, 0, read)
        }
        sourceStream.close()
        destinationStream!!.close()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}