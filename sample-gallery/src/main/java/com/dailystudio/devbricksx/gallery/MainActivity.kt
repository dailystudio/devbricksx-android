package com.dailystudio.devbricksx.gallery

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.dailystudio.devbricksx.development.Logger
import com.dailystudio.devbricksx.gallery.api.UnsplashApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

//        testApi()
    }

    private fun testApi() {
        lifecycleScope.launch(Dispatchers.IO) {
            val ret = UnsplashApi().searchPhotos("Food")
            ret?.results?.map {
                Logger.debug("food photo: $it")
            }
        }
    }

}