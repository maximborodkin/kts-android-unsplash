package ru.maxim.unsplash.di

import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ru.maxim.unsplash.ui.collection_details.CollectionDetailsViewModel
import ru.maxim.unsplash.ui.collection_details.CollectionDetailsViewModel.CollectionDetailsViewModelFactory
import ru.maxim.unsplash.ui.dialogs.add_to_collection.AddToCollectionViewModel
import ru.maxim.unsplash.ui.dialogs.add_to_collection.AddToCollectionViewModel.AddToCollectionViewModelFactory
import ru.maxim.unsplash.ui.feed.FeedViewModel
import ru.maxim.unsplash.ui.feed.FeedViewModel.FeedViewModelFactory
import ru.maxim.unsplash.ui.login.LoginViewModel
import ru.maxim.unsplash.ui.login.LoginViewModel.LoginViewModelFactory
import ru.maxim.unsplash.ui.photo_details.PhotoDetailsViewModel
import ru.maxim.unsplash.ui.photo_details.PhotoDetailsViewModel.PhotoDetailsViewModelFactory
import ru.maxim.unsplash.ui.profile.ProfileViewModel
import ru.maxim.unsplash.ui.profile.ProfileViewModel.ProfileViewModelFactory


val presentationModule = module(createdAtStart = true) {

    // ViewModel
    viewModel {
        LoginViewModelFactory(
            application = androidApplication(),
            authService = get(),
            userRepository = get(),
            authorizationService = get(),
            networkUtils = get()
        ).create(LoginViewModel::class.java)
    }

    viewModel { parameters ->
        FeedViewModelFactory(
            application = androidApplication(),
            photoRepository = get(),
            getItemsPage = parameters.get()
        ).create(FeedViewModel::class.java)
    }

    viewModel { parameters ->
        PhotoDetailsViewModelFactory(
            application = androidApplication(),
            photoRepository = get(),
            photoId = parameters.get()
        ).create(PhotoDetailsViewModel::class.java)
    }

    viewModel { parameters ->
        CollectionDetailsViewModelFactory(
            application = androidApplication(),
            collectionRepository = get(),
            collectionId = parameters.get()
        ).create(CollectionDetailsViewModel::class.java)
    }

    viewModel { parameters ->
        ProfileViewModelFactory(
            application = androidApplication(),
            userRepository = get(),
            preferencesManager = get(),
            username = parameters.getOrNull()
        ).create(ProfileViewModel::class.java)
    }

    viewModel {
        AddToCollectionViewModelFactory(
            application = androidApplication(),
            collectionRepository = get(),
        ).create(AddToCollectionViewModel::class.java)
    }
}