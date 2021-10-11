package ru.maxim.unsplash.ui.main.item_delegates

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import ru.maxim.unsplash.R
import ru.maxim.unsplash.databinding.ItemLoadingErrorBinding
import ru.maxim.unsplash.ui.main.item_delegates.LoadingErrorItemDelegate.LoadingErrorViewHolder
import ru.maxim.unsplash.ui.main.items.BaseMainListItem
import ru.maxim.unsplash.ui.main.items.LoadingErrorItem

class LoadingErrorItemDelegate(private val onRefresh: () -> Unit) :
    BaseMainItemDelegate<LoadingErrorItem, LoadingErrorViewHolder>() {

    override fun isForViewType(
        item: BaseMainListItem,
        items: MutableList<BaseMainListItem>,
        position: Int
    ): Boolean = item is LoadingErrorItem

    override fun onCreateViewHolder(parent: ViewGroup): LoadingErrorViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return LoadingErrorViewHolder(inflater.inflate(R.layout.item_loading_error, parent, false))
    }

    override fun onBindViewHolder(
        item: LoadingErrorItem,
        holder: LoadingErrorViewHolder,
        payloads: MutableList<Any>
    ) = holder.bind(item)

    inner class LoadingErrorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding by viewBinding(ItemLoadingErrorBinding::bind)

        fun bind(item: LoadingErrorItem) {
            with(binding) {
                errorMessage =
                    itemView.context.getString(item.errorMessage ?: R.string.common_loading_error)
                itemLoadingErrorRefreshBtn.setOnClickListener { onRefresh() }
            }
        }
    }
}