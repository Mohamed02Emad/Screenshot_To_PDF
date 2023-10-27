package com.mo.myapplicationjfjk

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.pdf.PdfDocument
import android.util.Log
import android.view.Window
import androidx.core.content.FileProvider
import java.io.File
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

fun savePdfToExternalStorage(pdfDocument: PdfDocument, fileName: String, context: Context) {
    val dir = File(context.cacheDir, "PDFs")
//     val dir = File(Environment.getExternalStorageDirectory(), "PDFs")
    if (!dir.exists()) {
        dir.mkdirs()
    }
    val file = File(dir, "$fileName.pdf")
    try {
    val fos = FileOutputStream(file)
    pdfDocument.writeTo(fos)
    fos.close()
    pdfDocument.close()
    downloadPdf(context, file)
    } catch (e: Exception) {
       // Log.d(TAG, "createPdfFromBitmap: 18 ${e.message}")
        e.printStackTrace()
        pdfDocument.close()
    }
}

fun sharePdf(context: Context, pdfFile: File) {
    if (!pdfFile.exists()) {
        return
    }

    val uri = FileProvider.getUriForFile(context, "com.mo.myapplicationjfjk.fileprovider", pdfFile)

    val shareIntent = Intent(Intent.ACTION_SEND)
    shareIntent.type = "application/pdf"
    shareIntent.putExtra(Intent.EXTRA_STREAM, uri)

    context.startActivity(Intent.createChooser(shareIntent, "Share PDF using"))
}

fun sharePdfWithDownloadOption(context: Context, pdfFile: File) {
    if (!pdfFile.exists()) {
        return
    }

    val uri = FileProvider.getUriForFile(context, "com.mo.myapplicationjfjk.fileprovider", pdfFile)

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

fun downloadPdf(context: Context, pdfFile: File) {
    val uri = FileProvider.getUriForFile(context, "com.mo.myapplicationjfjk.fileprovider", pdfFile)

    val downloadIntent = Intent(Intent.ACTION_CREATE_DOCUMENT)
    downloadIntent.addCategory(Intent.CATEGORY_OPENABLE)
    downloadIntent.putExtra(Intent.EXTRA_STREAM, uri)
    downloadIntent.type = "application/pdf"
    downloadIntent.putExtra(Intent.EXTRA_TITLE, pdfFile.name)


    val downloadChooser = Intent.createChooser(downloadIntent, "Download PDF")
    downloadChooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(downloadIntent))

    context.startActivity(downloadChooser)
}

//class CustomFileProvider : FileProvider() {
//    // You don't need to add any additional code to this class.
//}