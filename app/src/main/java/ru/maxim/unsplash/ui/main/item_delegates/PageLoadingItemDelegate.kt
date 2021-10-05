package ru.maxim.unsplash.ui.main.item_delegates

import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.recyclerview.widget.RecyclerView
import ru.maxim.unsplash.ui.main.item_delegates.PageLoadingItemDelegate.PageLoadingViewHolder
import ru.maxim.unsplash.ui.main.items.BaseMainListItem
import ru.maxim.unsplash.ui.main.items.PageLoaderItem

class PageLoadingItemDelegate : BaseMainItemDelegate<PageLoaderItem, PageLoadingViewHolder>() {

    override fun isForViewType(
        item: BaseMainListItem,
        items: MutableList<BaseMainListItem>,
        position: Int
    ): Boolean =
        item is PageLoaderItem

    override fun onCreateViewHolder(parent: ViewGroup): PageLoadingViewHolder {
        val layout = LinearLayout(parent.context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.CENTER_HORIZONTAL
            }
        }
        val loader = ProgressBar(layout.context).apply {
            isIndeterminate = true
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }
        layout.addView(loader)
        return PageLoadingViewHolder(layout)
    }

    override fun onBindViewHolder(
        item: PageLoaderItem,
        holder: PageLoadingViewHolder,
        payloads: MutableList<Any>
    ) {
    }

    inner class PageLoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}