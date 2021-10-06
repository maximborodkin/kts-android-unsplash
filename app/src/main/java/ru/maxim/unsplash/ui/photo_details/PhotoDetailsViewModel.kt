package ru.maxim.unsplash.ui.photo_details

import android.app.Application
import androidx.lifecycle.AndroidViewModel

class PhotoDetailsViewModel(application: Application) : AndroidViewModel(application) {
    var photoId: String? = null
}