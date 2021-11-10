package ru.maxim.unsplash.ui.dialogs.add_to_collection

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.maxim.unsplash.R
import ru.maxim.unsplash.persistence.PreferencesManager
import ru.maxim.unsplash.ui.dialogs.add_to_collection.AddToCollectionViewModel.AddToCollectionState.Error
import ru.maxim.unsplash.ui.dialogs.add_to_collection.AddToCollectionViewModel.AddToCollectionState.Success
import ru.maxim.unsplash.ui.feed.FeedActionsListener
import ru.maxim.unsplash.ui.profile.UserCollectionsFeedFragment
import ru.maxim.unsplash.ui.profile.UserCollectionsFeedFragment.Companion.userUsernameKey
import ru.maxim.unsplash.util.longToast
import ru.maxim.unsplash.util.toast

class AddToCollectionDialogFragment : DialogFragment(), FeedActionsListener {
    private val preferencesManager: PreferencesManager by inject()
    private val model: AddToCollectionViewModel by viewModel()
    private val username = preferencesManager.currentUserUsername
    private val photoId: String by lazy {
        val args by navArgs<AddToCollectionDialogFragmentArgs>()
        args.photoId
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val collectionsFragment = UserCollectionsFeedFragment().apply {
            arguments = bundleOf(userUsernameKey to username)
        }

        return FrameLayout(requireContext()).apply {
            layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)
            id = View.generateViewId()

            childFragmentManager.beginTransaction().add(id, collectionsFragment).commit()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                model.addToCollectionState.collect { state ->
                    when(state) {
                        Success -> {
                            context?.toast(R.string.added_to_collection)
                            dismiss()
                        }
                        is Error -> {
                            context?.longToast(state.messageRes)
                        }
                        else -> { /* Ignore the Empty state */ }
                    }
                }
            }
        }
    }

    override fun onCollectionClick(
        collectionId: String,
        transitionExtras: Array<Pair<View, String>>
    ) {
        model.addToCollection(photoId, collectionId)
    }
}