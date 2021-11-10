package ru.maxim.unsplash.ui.feed.item_delegates

import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.LinearLayout
import android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
import android.widget.ProgressBar
import androidx.recyclerview.widget.RecyclerView
import ru.maxim.unsplash.ui.feed.item_delegates.InitialLoadingItemDelegate.InitialLoadingViewHolder
import ru.maxim.unsplash.ui.feed.items.BaseFeedListItem
import ru.maxim.unsplash.ui.feed.items.InitialLoadingItem

class InitialLoadingItemDelegate :
    BaseMainItemDelegate<InitialLoadingItem, InitialLoadingViewHolder>() {

    override fun isForViewType(
        item: BaseFeedListItem,
        items: MutableList<BaseFeedListItem>,
        position: Int
    ): Boolean = item is InitialLoadingItem

    override fun onCreateViewHolder(parent: ViewGroup): InitialLoadingViewHolder {
        val layout = LinearLayout(parent.context).apply {
            layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT).apply {
                gravity = Gravity.CENTER
            }
        }
        val loader = ProgressBar(layout.context).apply {
            isIndeterminate = true
            layoutParams = LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
        }
        layout.addView(loader)
        return InitialLoadingViewHolder(layout)
    }

    override fun onBindViewHolder(
        item: InitialLoadingItem,
        holder: InitialLoadingViewHolder,
        payloads: MutableList<Any>
    ) {}

    inner class InitialLoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}