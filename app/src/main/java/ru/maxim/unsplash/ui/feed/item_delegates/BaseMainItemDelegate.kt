package ru.maxim.unsplash.ui.feed.item_delegates

import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates4.AbsListItemAdapterDelegate
import ru.maxim.unsplash.ui.feed.items.BaseFeedListItem

sealed class BaseMainItemDelegate<T : BaseFeedListItem, VH : RecyclerView.ViewHolder>
    : AbsListItemAdapterDelegate<T, BaseFeedListItem, VH>()