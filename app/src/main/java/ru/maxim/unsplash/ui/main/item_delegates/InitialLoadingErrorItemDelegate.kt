package ru.maxim.unsplash.ui.main.item_delegates

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import ru.maxim.unsplash.R
import ru.maxim.unsplash.databinding.ItemInitialLoadingErrorBinding
import ru.maxim.unsplash.ui.main.item_delegates.InitialLoadingErrorItemDelegate.InitialLoadingErrorViewHolder
import ru.maxim.unsplash.ui.main.items.BaseMainListItem
import ru.maxim.unsplash.ui.main.items.InitialLoadingErrorItem

class InitialLoadingErrorItemDelegate(private val onRefresh: () -> Unit) :
    BaseMainItemDelegate<InitialLoadingErrorItem, InitialLoadingErrorViewHolder>() {

    override fun isForViewType(
        item: BaseMainListItem,
        items: MutableList<BaseMainListItem>,
        position: Int
    ): Boolean = item is InitialLoadingErrorItem

    override fun onCreateViewHolder(parent: ViewGroup): InitialLoadingErrorViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return InitialLoadingErrorViewHolder(inflater.inflate(R.layout.item_initial_loading_error, parent, false))
    }

    override fun onBindViewHolder(
        item: InitialLoadingErrorItem,
        holder: InitialLoadingErrorViewHolder,
        payloads: MutableList<Any>
    ) = holder.bind(item)

    inner class InitialLoadingErrorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding by viewBinding(ItemInitialLoadingErrorBinding::bind)

        fun bind(item: InitialLoadingErrorItem) {
            with(binding) {
                errorMessage = itemView.context
                    .getString(item.errorMessage ?: R.string.common_loading_error)
                itemInitialLoadingErrorRefreshBtn.setOnClickListener { onRefresh() }
            }
        }
    }
}