package com.thul.netflixrxjavasearch.ui.activity

import android.annotation.SuppressLint
import android.app.SearchManager
import android.content.Context
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.appcompat.widget.SearchView
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.rxjava2.android.samples.utils.getQueryTextChangeObservable
import com.thul.netflixrxjavasearch.R
import com.thul.netflixrxjavasearch.response.SearchResponse
import com.thul.netflixrxjavasearch.ui.adapter.SearchClickListener
import com.thul.netflixrxjavasearch.ui.adapter.SearchController
import com.thul.netflixrxjavasearch.utils.setupGridManager
import com.thul.netflixrxjavasearch.viewmodel.SearchViewModelFactory
import com.thul.netflixrxjavasearch.viewmodel.SearchViewModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_search.*
import java.util.concurrent.TimeUnit

class SearchActivity : AppCompatActivity() {
    private lateinit var searchView: SearchView
    private lateinit var searchViewModel: SearchViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        searchMovie.setImeOptions(EditorInfo.IME_ACTION_SEARCH)
        searchMovie.setIconifiedByDefault(false)

        val searchEditText: EditText =
            searchMovie.findViewById(androidx.appcompat.R.id.search_src_text)
        searchEditText.setTextColor(resources.getColor(android.R.color.white))
        searchEditText.setHintTextColor(resources.getColor(android.R.color.white))
        val myCustomFont: Typeface? =
            ResourcesCompat.getFont(applicationContext, R.font.noto_sans_bold)
        searchEditText.setTypeface(myCustomFont)

        searchView = findViewById(R.id.searchMovie)
        searchViewModel = ViewModelProviders.of(this, SearchViewModelFactory()).get(
            SearchViewModel::class.java)

        setUpSearchObservable()

        searchViewModel.apiData().observe(this , Observer {
            searchViewModel.fetchMovie()
        })


        val searchListController = SearchController(object : SearchClickListener {
            override fun onSearchMovieItemClicked(movie: SearchResponse.MovieApiResponse) {

            }

        })

        searchRecyclerview.adapter =searchListController.adapter
        searchRecyclerview.setItemSpacingDp(4)

        searchViewModel.apiMovieData().observe(this , Observer {
            searchListController.setData(it)
        })

        searchRecyclerview.setupGridManager(searchListController)

    }

    @SuppressLint("CheckResult")
    fun setUpSearchObservable() {
        searchView.getQueryTextChangeObservable()
            .debounce(300, TimeUnit.MILLISECONDS)
            .filter { it.length > 1 }
            .distinctUntilChanged()
            .switchMap { query -> Observable.just(query)

                .doOnError {
                    // handle error
                }
                .onErrorReturn { "" }
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { result ->
                searchViewModel.fetchMoviesList(result)

            }
    }


}
