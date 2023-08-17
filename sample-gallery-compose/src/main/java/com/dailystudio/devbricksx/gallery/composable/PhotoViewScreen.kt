package com.dailystudio.devbricksx.gallery.composable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.compose.rememberAsyncImagePainter
import com.dailystudio.devbricksx.development.Logger
import com.dailystudio.devbricksx.gallery.api.data.ProfileImages
import com.dailystudio.devbricksx.gallery.core.R
import com.dailystudio.devbricksx.gallery.data.Download
import com.dailystudio.devbricksx.gallery.db.PhotoItem
import com.dailystudio.devbricksx.gallery.db.UserItem
import com.dailystudio.devbricksx.gallery.model.DownloadViewModelExt
import com.dailystudio.devbricksx.gallery.model.UserItemViewModel
import com.dailystudio.devbricksx.gallery.model.UserItemViewModelExt
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch

@Composable
fun PhotoViewScreen(item: PhotoItem?) {
    val photo = item ?: return
    Logger.debug("photo.userName: ${photo.uid}")

    val userItemViewModel = viewModel<UserItemViewModelExt>()
    val downloadViewModel = viewModel<DownloadViewModelExt>()

    userItemViewModel.pullUser(photo.uid)

    val user by remember {
        userItemViewModel.userByName(photo.uid)
    }.collectAsStateWithLifecycle(UserItem("", "", ""))

    val download by downloadViewModel.imageById(photo.id)
        .collectAsStateWithLifecycle(Download(photo.id, photo.downloadUrl, 0))

    ImageWithThumb(
        photo.thumbnailUrl,
        photo.downloadUrl,
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop,
        contentDescription = null,
    )
    Box(
        modifier = Modifier
            .systemBarsPadding()
            .fillMaxSize()
    ) {
        Card(
            shape = RoundedCornerShape(8.dp),
            elevation = CardDefaults.cardElevation(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .align(Alignment.BottomCenter)
        ) {
            Row(
                Modifier.padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                AsyncImage(
                    user.photoUrl,
                    modifier = Modifier
                        .padding(8.dp)
                        .size(48.dp)
                        .clip(CircleShape),
                    contentDescription = null)
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = user.displayName ?: "",
                        modifier = Modifier.padding(horizontal = 8.dp),
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.Black
                    )
                    Text(
                        text = stringResource(R.string.source_unsplash),
                        modifier = Modifier.padding(horizontal = 8.dp),
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Gray
                    )
                }
                Box {
                    if (download != null) {
                        val prg = (download?.progress ?:0) / 100f
                        CircularProgressIndicator(
                            progress = prg,
                            modifier = Modifier
                                .padding(2.dp)
                                .size(32.dp)
                        )
                    } else {
                        Icon(
                            painterResource(R.drawable.ic_action_download),
                            contentDescription = null,
                            modifier = Modifier
                                .padding(2.dp)
                                .size(32.dp)
                                .clickable {
                                    downloadViewModel.downloadImage(photo.id, photo.downloadUrl)
                                }
                        )
                    }

                }

            }
        }
    }

}

@Composable
fun ImageWithThumb(
    thumbnail: String,
    highQuality: String,
    modifier: Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    contentDescription: String? = null,
) {
    SubcomposeAsyncImage(
        model = highQuality,
        modifier = modifier,
        contentScale = contentScale,
        contentDescription = contentDescription) {

        val state = painter.state
        if (state is AsyncImagePainter.State.Loading || state is AsyncImagePainter.State.Error) {
            SubcomposeAsyncImageContent(
                painter = rememberAsyncImagePainter(thumbnail),
            )
        } else {
            SubcomposeAsyncImageContent()
        }
    }
}