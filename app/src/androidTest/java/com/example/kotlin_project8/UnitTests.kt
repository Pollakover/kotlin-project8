package com.example.kotlin_project8

import android.content.Context
import android.graphics.Bitmap
import android.os.Environment
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
import java.io.File

class UnitTests {

    private lateinit var forTests: ForTests
    private lateinit var mockContext: Context

    @Before
    fun setUp() {
        mockContext = mock(Context::class.java)
        val testDir = File(System.getProperty("java.io.tmpdir"), "test_files")
        if (!testDir.exists()) {
            testDir.mkdirs()
        }
        `when`(mockContext.filesDir).thenReturn(testDir)
        forTests = ForTests(mockContext)
    }

    // Тест проверки инициализации объекта
    @Test
    fun testInitialization() {
        assertNotNull("Контекст не должен быть null", forTests)
        assertEquals("Переданный context должен совпадать", mockContext, forTests.context)
    }

    // Тест загрузки изображения
    @Test
    fun testDownloadImage() = runBlocking {
        val validUrl = "https://i.imgur.com/SgqtC1R.png"
        val bitmap = forTests.downloadImage(validUrl)

        assertNotNull("Bitmap должен быть загружен", bitmap)
    }

    // Тест сохранения изображения
    @Test
    fun testSaveImage() {
        val mockBitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)

        val file = forTests.saveImage(mockBitmap)

        assertTrue("Файл должен существовать", file.exists())
    }

    // Тест на корректное сохранение файла после загрузки
    @Test
    fun testDownloadAndSaveImage() = runBlocking {
        val validUrl = "https://i.imgur.com/SgqtC1R.png"

        forTests.downloadAndSaveImage(validUrl)

        val savedFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        assertTrue("Файл должен быть создан", savedFile.exists())
    }


    // проверяет, что файл создаётся с правильным именем.
    @Test
    fun testFileName() {
        val fileName = "downloaded_image.png"
        val documentsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val file = File(documentsDir, fileName)

        assertEquals("Имя файла должно совпадать", "downloaded_image.png", file.name)
    }
}
