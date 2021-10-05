package ru.maxim.unsplash.ui.main.item_delegates

import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates4.AbsListItemAdapterDelegate

abstract class BaseItemDelegate<T : Any, VH : RecyclerView.ViewHolder>
    : AbsListItemAdapterDelegate<T, Any, VH>()