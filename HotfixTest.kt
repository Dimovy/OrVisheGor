package com.travels.searchtravels

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.core.app.ActivityScenario.launch
import androidx.test.ext.junit.runners.AndroidJUnit4

import com.google.api.services.vision.v1.model.LatLng
import com.preview.planner.prefs.AppPreferences
import com.travels.searchtravels.api.OnVisionApiListener
import com.travels.searchtravels.activity.MainActivity
import com.travels.searchtravels.api.VisionApi

import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL


@RunWith(AndroidJUnit4::class)
class HotfixTest {
      private fun getImage(src: String?): Bitmap? {
        return try {
            val url = URL(src)
            val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input: InputStream = connection.inputStream
            BitmapFactory.decodeStream(input)
        } catch (e: IOException) {
            null
        }
    }

    fun startActivity() {
        val scenario = launch(MainActivity::class.java)
        scenario.moveToState(Lifecycle.State.CREATED)
        scenario.moveToState(Lifecycle.State.RESUMED)
    }

    @Test
    fun imagesTest() {
        val images = arrayOf<Array<String>>(
            arrayOf("http://toyota-rus2.narod.ru/files/voyage4/16-10-30_voyage-1790-2/19755958.jpg", "sea"),
            arrayOf("https://rasija.ru/wp-content/uploads/2020/04/s1200-7.jpg", "mountain"),
            arrayOf("https://img.tourister.ru/files/1/1/0/2/7/7/9/0/original.jpg", "beach"), 
            arrayOf("https://funart.pro/uploads/posts/2019-11/1573388653_nojshvanshtajn-zamok-germanija-7.jpg", "other"),
            arrayOf("https://www.nastol.com.ua/download.php?img=201708/2560x1440/nastol.com.ua-242863.jpg", "ocean"),
            arrayOf("https://img3.goodfon.ru/original/3200x2125/1/40/engelberg-shveycariya-gory.jpg", "snow"),
        )
        for (place in images) {
            val bitmap = getImage(place[0])
            VisionApi.findLocation(
                bitmap,
                AppPreferences.getToken(getApplicationContext()),
                object : OnVisionApiListener {
                    override fun onErrorPlace(category: String) {
                        Assert.assertEquals(category, place[1])
                    }

                    override fun onError() {
                        Assert.fail("Неверная категория, ожидаемый ответ: ${place[1]}")
                    }
                })
        }
    }

}