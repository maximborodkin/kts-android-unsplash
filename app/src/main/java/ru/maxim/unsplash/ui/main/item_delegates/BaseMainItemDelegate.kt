package ru.maxim.unsplash.ui.main.item_delegates

import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates4.AbsListItemAdapterDelegate
import ru.maxim.unsplash.ui.main.items.BaseMainListItem

sealed class BaseMainItemDelegate<T : BaseMainListItem, VH : RecyclerView.ViewHolder>
    : AbsListItemAdapterDelegate<T, BaseMainListItem, VH>()