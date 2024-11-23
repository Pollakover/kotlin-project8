package com.example.kotlin_project8

import android.content.Context
import android.os.Environment
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.Rule
import org.junit.Test
import java.io.File

class MainActivityTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    // проверка на видимость кнопки и поля ввода
    @Test
    fun testViewVisibility() {
        onView(withId(R.id.download_button)).check(matches(isDisplayed()))

        onView(withId(R.id.image_link)).check(matches(isDisplayed()))
    }

    // проверка копирования ссылки в при нажатии на кнопку
    @Test
    fun testLinkCopied() {
        val testUrl = "https://i.imgur.com/SgqtC1R.png"
        onView(withId(R.id.image_link)).perform(typeText(testUrl))

        onView(withId(R.id.download_button)).perform(click())

        onView(withId(R.id.image_link)).check(matches(withText(testUrl)))
    }

    // при пустой ссылке не должен создаваться файл
    @Test
    fun testEmptyUrl() {
        onView(withId(R.id.image_link)).perform(typeText(""))

        onView(withId(R.id.download_button)).perform(click())

        val documentsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val file = File(documentsDir, "downloaded_image_1.png")
        assertFalse(file.exists())
    }

    // проверка скачивания изображения
    @Test
    fun testSaveImage() {
        val validUrl = "https://i.imgur.com/SgqtC1R.png"
        onView(withId(R.id.image_link)).perform(typeText(validUrl))

        onView(withId(R.id.download_button)).perform(click())

        val documentsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val file = File(documentsDir, "downloaded_image.png")

        // Ждем, пока файл будет записан
        Thread.sleep(5000)

        // Проверяем, что файл был создан
        assertTrue(file.exists())
    }


    // если ссылка не содержит изображение, то приложение должно продолжать нормально работать
    @Test
    fun testInvalidUrl() {
        val invalidUrl = "https://www.satisfactorytools.com/1.0/production"
        onView(withId(R.id.image_link)).perform(typeText(invalidUrl))
        onView(withId(R.id.download_button)).perform(click())

        // Генерируем уникальное имя файла
        val uniqueFileName = "downloaded_image_3.png"
        val documentsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val file = File(documentsDir, uniqueFileName)

        // Ожидаем завершения попытки сохранения
        Thread.sleep(2000)

        // Проверяем, что файл не был создан
        assertFalse(file.exists())
    }



}
