package ru.maxim.unsplash.ui.feed.items

import androidx.annotation.StringRes

data class PageLoadingErrorItem(@StringRes val errorMessage: Int) : BaseFeedListItem()