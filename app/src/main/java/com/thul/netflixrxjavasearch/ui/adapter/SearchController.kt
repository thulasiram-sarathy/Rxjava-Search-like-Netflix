package com.thul.netflixrxjavasearch.ui.adapter


import com.airbnb.epoxy.TypedEpoxyController
import com.thul.netflixrxjavasearch.response.SearchResponse
import com.thul.netflixrxjavasearch.ui.epoxy.HomeViewModel_


class SearchController(private val listener:SearchClickListener) :TypedEpoxyController<List<SearchResponse.MovieApiResponse>>(){

    override fun buildModels(data: List<SearchResponse.MovieApiResponse>) {
        data.forEach {
            HomeViewModel_()
            .id(it.id)
            .image(it.poster_path)
            .moviename(it.title)
            .movieRating(it.voteAverage.div(2).toFloat())
            .movieOnClickListener { _, _, _, _ ->
                listener.onSearchMovieItemClicked(it)
            }
                .addTo(this)
        }

    }


}

interface SearchClickListener {
    fun onSearchMovieItemClicked(movie: SearchResponse.MovieApiResponse)

}