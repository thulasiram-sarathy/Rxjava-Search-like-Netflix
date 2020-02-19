package com.thul.netflixrxjavasearch.viewmodel

import android.annotation.SuppressLint

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.an.github.rest.RestApi
import com.thul.netflixrxjavasearch.AppConstants.Companion.TMDB_KEY
import com.thul.netflixrxjavasearch.response.SearchResponse
import com.thul.netflixrxjavasearch.utils.RxSingleSchedulers
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers


class SearchViewModel() : ViewModel() {

    var disposable = CompositeDisposable()

    private val restApi:RestApi by lazy {
        RestApi.create()
    }
    private val apiData: MutableLiveData<SearchResponse> = MutableLiveData()
    private val getMovieLiveData: MutableLiveData<List<SearchResponse.MovieApiResponse>> = MutableLiveData()
    private val apiError = MutableLiveData<Throwable>()
    private val loading = MutableLiveData<Boolean>()

    fun apiData(): MutableLiveData<SearchResponse> {
        return apiData
    }

    fun apiMovieData(): MutableLiveData<List<SearchResponse.MovieApiResponse>> {
        return getMovieLiveData
    }

    fun apiError(): MutableLiveData<Throwable> {
        return apiError
    }

    fun loading(): MutableLiveData<Boolean> {
        return loading
    }

    @SuppressLint("CheckResult")
    fun fetchMoviesList(query: String){
            val observable = restApi.getMoviesList(query,TMDB_KEY,  1)
            observable?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.compose(RxSingleSchedulers.DEFAULT.applySchedulers<SearchResponse>())
                ?.subscribe({ loginResponse ->
                    apiData.postValue(loginResponse)

                }, { error ->
                   // handle loading during error
                }
                )

    }

    @SuppressLint("CheckResult")
    fun fetchMovie(){
        val observable = Observable.just(apiData.value?.results)
        observable.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { loginResponse ->
                getMovieLiveData.postValue(loginResponse)
            }
    }


    override fun onCleared() {
        super.onCleared()
        if (disposable != null) {
            disposable.clear()
        }

    }



}


