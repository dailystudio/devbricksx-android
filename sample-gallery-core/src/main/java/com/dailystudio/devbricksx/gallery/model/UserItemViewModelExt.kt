package com.dailystudio.devbricksx.gallery.model

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.dailystudio.devbricksx.development.Logger
import com.dailystudio.devbricksx.gallery.api.UnsplashApi
import com.dailystudio.devbricksx.gallery.db.UserItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.cache
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch

class UserItemViewModelExt(application: Application): UserItemViewModel(application)  {

    override fun userByName(userName: String): Flow<UserItem> {
        return super.userByName(userName)
            .mapNotNull { it }
            .flowOn(Dispatchers.IO)
            .shareIn(viewModelScope, SharingStarted.Eagerly, 1)
    }

    fun pullUser(
        userName: String,
        coroutineScope: CoroutineScope = viewModelScope
    ) = coroutineScope.launch(Dispatchers.IO) {
        var userItem = userItemRepository.getUserItem(userName)
        Logger.debug("get user: $userItem")
        if (userItem == null) {
            val user = UnsplashApi.getUser(userName)
            user?.let {
                userItem = UserItem.fromUnsplashUser(it)
            }?.also {
                userItemRepository.insert(userItem)
            }
        }
    }

}