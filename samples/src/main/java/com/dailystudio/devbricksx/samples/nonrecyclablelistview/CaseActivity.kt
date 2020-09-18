package com.dailystudio.devbricksx.samples.nonrecyclablelistview

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
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
            generateImages()
        }
    }

    private fun generateImages() {
        val viewModel =
                ViewModelProvider(this).get(PixabayImageViewModel::class.java)

        var id = 0
        val images = arrayListOf(
                PixabayImage(id++,
                        "mountains-mountain-range-valleys",
                        "https://cdn.pixabay.com/photo/2020/09/09/18/39/mountains-5558476_1280.jpg",
                        "https://pixabay.com/photos/mountains-mountain-range-valleys-5558476/"
                ),
                PixabayImage(id++,
                        "car-vehicle-auto-automobile",
                        "https://cdn.pixabay.com/photo/2020/09/06/07/37/car-5548242_1280.jpg",
                        "https://pixabay.com/photos/car-vehicle-auto-automobile-5548242/"
                ),
                PixabayImage(id++,
                        "buildings-houses-street-city-rain",
                        "https://cdn.pixabay.com/photo/2020/08/30/09/28/buildings-5528981_1280.jpg",
                        "https://pixabay.com/photos/buildings-houses-street-city-rain-5528981/"
                ),
                PixabayImage(id++,
                        "cap-boy-smile-tomboy-emotions",
                        "https://cdn.pixabay.com/photo/2017/11/06/13/45/cap-2923682_1280.jpg",
                        "https://pixabay.com/photos/cap-boy-smile-tomboy-emotions-2923682/"
                ),
                PixabayImage(id++,
                        "rome-bridge-view-italy-landmark",
                        "https://cdn.pixabay.com/photo/2016/11/05/08/31/rome-1799670_1280.jpg",
                        "https://pixabay.com/photos/rome-bridge-view-italy-landmark-1799670/"
                ),
                PixabayImage(id++,
                        "squirrel-chipmunk-rodent-animal",
                        "https://cdn.pixabay.com/photo/2020/08/21/16/05/squirrel-5506514_1280.jpg",
                        "https://pixabay.com/photos/squirrel-chipmunk-rodent-animal-5506514/"
                ),
                PixabayImage(id++,
                        "sunset-the-boat-wave-water-hoi-an",
                        "https://cdn.pixabay.com/photo/2020/03/21/16/02/sunset-4954402_1280.jpg",
                        "https://pixabay.com/photos/sunset-the-boat-wave-water-hoi-an-4954402/"
                ),
                PixabayImage(id++,
                        "bicycle-vintage-street-shop-retro",
                        "https://cdn.pixabay.com/photo/2016/11/30/12/29/bicycle-1872682_1280.jpg",
                        "https://pixabay.com/photos/bicycle-vintage-street-shop-retro-1872682/"
                ),
                PixabayImage(id++,
                        "food-eastern-sweets-baklava-sweet",
                        "https://cdn.pixabay.com/photo/2020/05/11/15/06/food-5158702_1280.jpg",
                        "https://pixabay.com/photos/food-eastern-sweets-baklava-sweet-5158702/"
                ),
                PixabayImage(id++,
                        "woman-coffee-phone-portrait-lady",
                        "https://cdn.pixabay.com/photo/2019/06/02/17/33/woman-4246954_1280.jpg",
                        "https://pixabay.com/photos/woman-coffee-phone-portrait-lady-4246954/"
                ),
        )

        viewModel.insertOrUpdatePixabayImages(images)
    }

}
