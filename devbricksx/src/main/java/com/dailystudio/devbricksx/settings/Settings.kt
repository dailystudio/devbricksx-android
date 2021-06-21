package com.dailystudio.devbricksx.settings

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.dailystudio.devbricksx.development.Logger

object Settings: MutableLiveData<SettingChange>() {

    override fun observe(owner: LifecycleOwner, observer: Observer<in SettingChange>) {
        val timestampOnObserve = System.currentTimeMillis()

        super.observe(owner, { t ->
            if (t.timestamp >= timestampOnObserve) {
                observer.onChanged(t)
            } else {
                Logger.warn("skip out-of-date changes: ${t.name} (observe on: $timestampOnObserve, change on: ${t.timestamp})")
            }
        })
    }

}