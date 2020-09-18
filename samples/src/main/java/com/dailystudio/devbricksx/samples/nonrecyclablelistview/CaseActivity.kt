package com.dailystudio.devbricksx.samples.nonrecyclablelistview

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.dailystudio.devbricksx.development.Logger
import com.dailystudio.devbricksx.samples.R
import com.dailystudio.devbricksx.samples.common.BaseCaseActivity
import com.dailystudio.devbricksx.samples.nonrecyclablelistview.model.PixabayImageViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CaseActivity : BaseCaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_case_non_recyclable_list_view)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        lifecycleScope.launch(Dispatchers.IO) {
            generatePixabayImages()
        }
    }

    private fun generatePixabayImages() {
        val viewModel =
                ViewModelProvider(this).get(PixabayImageViewModel::class.java)

        val images = arrayListOf(
                PixabayImage(0,
                        "mountains-mountain-range-valleys",
                        "https://cdn.pixabay.com/photo/2020/09/09/18/39/mountains-5558476_1280.jpg",
                        "https://pixabay.com/photos/mountains-mountain-range-valleys-5558476/"
                ),
                PixabayImage(1,
                        "car-vehicle-auto-automobile",
                        "https://cdn.pixabay.com/photo/2020/09/06/07/37/car-5548242_1280.jpg",
                        "https://pixabay.com/photos/car-vehicle-auto-automobile-5548242/"
                ),
                PixabayImage(2,
                        "buildings-houses-street-city-rain",
                        "https://cdn.pixabay.com/photo/2020/08/30/09/28/buildings-5528981_1280.jpg",
                        "https://pixabay.com/photos/buildings-houses-street-city-rain-5528981/"
                ),
                PixabayImage(3,
                        "rome-bridge-view-italy-landmark",
                        "https://cdn.pixabay.com/photo/2016/11/05/08/31/rome-1799670_1280.jpg",
                        "https://pixabay.com/photos/rome-bridge-view-italy-landmark-1799670/"
                ),
                PixabayImage(4,
                        "squirrel-chipmunk-rodent-animal",
                        "https://cdn.pixabay.com/photo/2020/08/21/16/05/squirrel-5506514_1280.jpg",
                        "https://pixabay.com/photos/squirrel-chipmunk-rodent-animal-5506514/"
                ),
                PixabayImage(5,
                        "sunset-the-boat-wave-water-hoi-an",
                        "https://cdn.pixabay.com/photo/2020/03/21/16/02/sunset-4954402_1280.jpg",
                        "https://pixabay.com/photos/sunset-the-boat-wave-water-hoi-an-4954402/"
                ),
        )

        viewModel.insertOrUpdatePixabayImages(images)
        Logger.debug("images generated: $images")
    }

}
