package com.thul.netflixrxjavasearch.utils

import android.widget.ImageView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.epoxy.EpoxyController
import com.squareup.picasso.Picasso

fun ImageView.loadMovieImage(path:String?) {
    Picasso.get()
        .load("https://image.tmdb.org/t/p/w500$path")
        .into(this)

}

fun RecyclerView.setupGridManager(epoxyController: EpoxyController) {
    val spanCount = 2
    val manager = GridLayoutManager(this.context,spanCount)
    epoxyController.spanCount = spanCount
    manager.spanSizeLookup = epoxyController.spanSizeLookup
    this.layoutManager = manager
    this.adapter = epoxyController.adapter
}