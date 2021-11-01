package ru.maxim.unsplash.ui.feed.item_delegates

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import ru.maxim.unsplash.R
import ru.maxim.unsplash.databinding.ItemCacheShownBinding
import ru.maxim.unsplash.ui.feed.item_delegates.CacheShownItemDelegate.CacheShownViewHolder
import ru.maxim.unsplash.ui.feed.items.BaseFeedListItem
import ru.maxim.unsplash.ui.feed.items.CacheShownItem

class CacheShownItemDelegate(private val onRefresh: () -> Unit) :
    BaseMainItemDelegate<CacheShownItem, CacheShownViewHolder>() {

    override fun isForViewType(
        item: BaseFeedListItem,
        items: MutableList<BaseFeedListItem>,
        position: Int
    ): Boolean = item is CacheShownItem

    override fun onCreateViewHolder(parent: ViewGroup): CacheShownViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return CacheShownViewHolder(inflater.inflate(R.layout.item_cache_shown, parent, false))
    }

    override fun onBindViewHolder(
        item: CacheShownItem,
        holder: CacheShownViewHolder,
        payloads: MutableList<Any>
    ) {}

    inner class CacheShownViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding by viewBinding(ItemCacheShownBinding::bind)

        init {
            binding.itemCacheShownRefreshBtn.setOnClickListener { onRefresh() }
        }
    }
}