package com.dailystudio.devbricksx.gallery.model

import android.app.Application
import com.dailystudio.devbricksx.gallery.api.UnsplashApi
import com.dailystudio.devbricksx.gallery.db.UserItem

class UserItemViewModelExt(application: Application): UserItemViewModel(application)  {

    suspend fun pullUser(userName: String): UserItem? {
        var userItem = userItemRepository.getUserItem(userName)
        if (userItem == null) {
            val user = UnsplashApi.getUser(userName)

            user?.let {
                userItem = UserItem.fromUnsplashUser(it)
            }?.also {
                userItemRepository.insert(userItem)
            }

        }

        return userItem
    }

}