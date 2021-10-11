package ru.maxim.unsplash.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import ru.maxim.unsplash.repository.paging_source.MainPagingSource

class MainViewModel : ViewModel() {
    private val pagingSource = MainPagingSource()
    private val _photos: MutableLiveData<PagingData<Any>> = Pager(
        PagingConfig(pageSize = 15, initialLoadSize = 20, prefetchDistance = 2),
        pagingSourceFactory = { pagingSource }
    )
        .liveData
        .cachedIn(viewModelScope)
        .let { it as MutableLiveData<PagingData<Any>> }

    val photos: LiveData<PagingData<Any>> = _photos
}