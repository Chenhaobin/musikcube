package io.casey.musikcube.remote.ui.settings.viewmodel

import io.casey.musikcube.remote.framework.ViewModel
import io.casey.musikcube.remote.service.websocket.model.IDataProvider
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy

abstract class BaseRemoteViewModel: ViewModel<BaseRemoteViewModel.State>() {
    private var provider: IDataProvider? = null
    protected var disposables = CompositeDisposable()

    enum class State { Disconnected, Saving, Saved, Loading, Ready }

    fun attach(provider: IDataProvider) {
        this.provider = provider
        this.provider?.let {
            this.disposables.add(it.observeState().subscribeBy(
                onNext = {
                    if (it.first == IDataProvider.State.Connected) {
                        onConnected()
                    }
                },
                onError = {
                    state = State.Disconnected
                }
            ))
        }
    }

    var state: State = State.Disconnected
        set(value) {
            if (state != value) {
                field = value
                publish(state)
            }
        }

    override fun onPause() {
        super.onPause()
        this.provider = null
        this.disposables.dispose()
        this.disposables = CompositeDisposable()
    }

    protected abstract fun onConnected()
}