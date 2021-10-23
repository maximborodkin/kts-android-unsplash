package ru.maxim.unsplash.di

import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ru.maxim.unsplash.ui.collection_details.CollectionDetailsViewModel
import ru.maxim.unsplash.ui.collection_details.CollectionDetailsViewModel.CollectionDetailsViewModelFactory
import ru.maxim.unsplash.ui.login.LoginViewModel
import ru.maxim.unsplash.ui.login.LoginViewModel.LoginViewModelFactory
import ru.maxim.unsplash.ui.main.MainFragment
import ru.maxim.unsplash.ui.main.MainViewModel
import ru.maxim.unsplash.ui.main.MainViewModel.MainViewModelFactory
import ru.maxim.unsplash.ui.photo_details.PhotoDetailsViewModel
import ru.maxim.unsplash.ui.photo_details.PhotoDetailsViewModel.PhotoDetailsViewModelFactory


val presentationModule = module(createdAtStart = true) {

    // ViewModel
    viewModel {
        LoginViewModelFactory(
            application = androidApplication(),
            authService = get(),
            authorizationService = get(),
            networkUtils = get()
        ).create(LoginViewModel::class.java)
    }

    viewModel { parameters ->
        MainViewModelFactory(
            application = androidApplication(),
            photoRepository = get(),
            collectionRepository = get(),
            listMode = MainFragment.ListMode.Editorial
        ).create(MainViewModel::class.java)
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
            photoRepository = get(),
            collectionId = parameters.get()
        ).create(CollectionDetailsViewModel::class.java)
    }
}