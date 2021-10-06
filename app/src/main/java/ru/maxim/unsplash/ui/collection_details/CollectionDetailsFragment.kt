package ru.maxim.unsplash.ui.collection_details

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import ru.maxim.unsplash.R
import ru.maxim.unsplash.databinding.FragmentCollectionDetailsBinding

class CollectionDetailsFragment : Fragment(R.layout.fragment_collection_details) {
    private val binding by viewBinding(FragmentCollectionDetailsBinding::bind)
    private val model: CollectionDetailsViewModel by viewModels()
    private val args: CollectionDetailsFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        model.collectionId = args.collectionId
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}