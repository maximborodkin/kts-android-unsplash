package ru.maxim.unsplash.ui.collection_details

import android.app.Application
import androidx.lifecycle.AndroidViewModel

class CollectionDetailsViewModel(application: Application) : AndroidViewModel(application) {
    var collectionId: String? = null
}