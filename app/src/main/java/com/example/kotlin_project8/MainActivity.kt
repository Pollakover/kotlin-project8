package com.example.kotlin_project8

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import java.net.URL
import kotlinx.coroutines.*
import java.io.File
import java.io.FileOutputStream


class MainActivity : AppCompatActivity() {

    // потоки Network и Disk
    val networkDispatcher = newSingleThreadContext("Network")
    val diskDispatcher = newSingleThreadContext("Disk")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val download_button = findViewById<Button>(R.id.download_button)
        val image_link = findViewById<EditText>(R.id.image_link)

        // обработка нажатия
        download_button.setOnClickListener {
            val imageUrl = image_link.text.toString()
            if (imageUrl.isNotBlank()) {
                downloadAndSaveImage(imageUrl)
            } else {
                image_link.error = "Введите ссылку"
            }
        }
    }

    // загрузить и сохраненить изображение
    fun downloadAndSaveImage(imageUrl: String) {
        CoroutineScope(Dispatchers.Main).launch {
            // загрузить изображение в потоке Network
            val bitmap = withContext(networkDispatcher) {
                downloadImage(imageUrl)
            }

            // сохранить изображение на устройство в потоке Disk
            withContext(diskDispatcher) {
                if (bitmap != null) {
                    saveImage(bitmap)
                }
            }
        }
    }

    // загрузить изображение
    fun downloadImage(url: String): Bitmap? {
        val inputStream = URL(url).openStream()
        return BitmapFactory.decodeStream(inputStream)
    }

    // сохранить изоражение
    fun saveImage(bitmap: Bitmap) {
        val contentResolver = applicationContext.contentResolver
        val downloadsUri = MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)

        val imageDetails = ContentValues().apply {
            put(MediaStore.Downloads.DISPLAY_NAME, "downloaded_image.png")
            put(MediaStore.Downloads.MIME_TYPE, "image/png")
        }

        val imageUri = contentResolver.insert(downloadsUri, imageDetails)

        if (imageUri != null) {
            contentResolver.openOutputStream(imageUri).use { outputStream ->
                if (outputStream != null) {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                }
            }
            runOnUiThread {
                Toast.makeText(this, "Изображение сохранено: $imageUri", Toast.LENGTH_LONG).show()
            }
        }
    }
}

class ForTests(val context: Context) {

    // потоки Network и Disk
    val networkDispatcher = newSingleThreadContext("Network")
    val diskDispatcher = newSingleThreadContext("Disk")

    // Загрузить изображение
    fun downloadImage(url: String): Bitmap? {
        val inputStream = URL(url).openStream()
        return BitmapFactory.decodeStream(inputStream)
    }

    // Сохранить изображение
    fun saveImage(bitmap: Bitmap): File {
        // Используем filesDir из переданного context
        val file = File(context.filesDir, "downloaded_image.png")
        FileOutputStream(file).use { fos ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
        }
        return file
    }

    // загрузить и сохраненить изображение
    fun downloadAndSaveImage(imageUrl: String) {
        CoroutineScope(Dispatchers.Main).launch {
            // загрузить изображение в потоке Network
            val bitmap = withContext(networkDispatcher) {
                downloadImage(imageUrl)
            }

            // сохранить изображение на устройство в потоке Disk
            withContext(diskDispatcher) {
                if (bitmap != null) {
                    saveImage(bitmap)
                }
            }
        }
    }
}



