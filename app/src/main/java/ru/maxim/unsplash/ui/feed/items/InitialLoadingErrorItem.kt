package ru.maxim.unsplash.ui.feed.items

import androidx.annotation.StringRes

data class InitialLoadingErrorItem(@StringRes val errorMessage: Int) : BaseFeedListItem()