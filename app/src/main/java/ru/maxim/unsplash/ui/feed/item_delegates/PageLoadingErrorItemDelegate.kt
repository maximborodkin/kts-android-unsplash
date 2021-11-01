package ru.maxim.unsplash.ui.feed.item_delegates

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import ru.maxim.unsplash.R
import ru.maxim.unsplash.databinding.ItemPageLoadingErrorBinding
import ru.maxim.unsplash.ui.feed.item_delegates.PageLoadingErrorItemDelegate.PageLoadingErrorViewHolder
import ru.maxim.unsplash.ui.feed.items.BaseFeedListItem
import ru.maxim.unsplash.ui.feed.items.PageLoadingErrorItem

class PageLoadingErrorItemDelegate(private val onRetry: () -> Unit) :
    BaseMainItemDelegate<PageLoadingErrorItem, PageLoadingErrorViewHolder>() {

    override fun isForViewType(
        item: BaseFeedListItem,
        items: MutableList<BaseFeedListItem>,
        position: Int
    ): Boolean = item is PageLoadingErrorItem

    override fun onCreateViewHolder(parent: ViewGroup): PageLoadingErrorViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return PageLoadingErrorViewHolder(
            inflater.inflate(R.layout.item_page_loading_error, parent, false)
        )
    }

    override fun onBindViewHolder(
        item: PageLoadingErrorItem,
        holder: PageLoadingErrorViewHolder,
        payloads: MutableList<Any>
    ) = holder.bind(item)

    inner class PageLoadingErrorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding by viewBinding(ItemPageLoadingErrorBinding::bind)

        fun bind(item: PageLoadingErrorItem) {
            with(binding) {
                errorMessage =
                    itemView.context.getString(item.errorMessage ?: R.string.common_loading_error)
                itemPageLoadingErrorRetryBtn.setOnClickListener { onRetry() }
            }
        }
    }
}