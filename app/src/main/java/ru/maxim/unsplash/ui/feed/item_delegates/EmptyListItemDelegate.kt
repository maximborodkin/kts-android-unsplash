package ru.maxim.unsplash.ui.feed.item_delegates

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import ru.maxim.unsplash.R
import ru.maxim.unsplash.databinding.ItemEmptyListBinding
import ru.maxim.unsplash.ui.feed.item_delegates.EmptyListItemDelegate.EmptyListViewHolder
import ru.maxim.unsplash.ui.feed.items.BaseFeedListItem
import ru.maxim.unsplash.ui.feed.items.EmptyListItem

class EmptyListItemDelegate(private val onRefresh: () -> Unit) :
    BaseMainItemDelegate<EmptyListItem, EmptyListViewHolder>() {

    override fun isForViewType(
        item: BaseFeedListItem,
        items: MutableList<BaseFeedListItem>,
        position: Int
    ): Boolean = item is EmptyListItem

    override fun onCreateViewHolder(parent: ViewGroup): EmptyListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return EmptyListViewHolder(inflater.inflate(R.layout.item_empty_list, parent, false))
    }

    override fun onBindViewHolder(
        item: EmptyListItem,
        holder: EmptyListViewHolder,
        payloads: MutableList<Any>
    ) = holder.bind()

    inner class EmptyListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding by viewBinding(ItemEmptyListBinding::bind)

        fun bind() {
            binding.itemEmptyListRefreshBtn.setOnClickListener { onRefresh() }
        }
    }
}