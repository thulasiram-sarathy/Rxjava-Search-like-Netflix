package com.thul.netflixrxjavasearch.ui.epoxy

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.airbnb.epoxy.CallbackProp
import com.airbnb.epoxy.ModelProp
import com.airbnb.epoxy.ModelView
import com.thul.netflixrxjavasearch.R
import com.thul.netflixrxjavasearch.utils.loadMovieImage


@ModelView(autoLayout = ModelView.Size.MATCH_WIDTH_WRAP_HEIGHT,fullSpan = false)
class HomeView @JvmOverloads constructor(context: Context,
                                          attributes: AttributeSet?=null,
                                          defStyle:Int = 0)
    :CardView(context,attributes,defStyle)
{

    private val posterImage: ImageView
    private val movieName: TextView
    private val movieRating: RatingBar
    private val movielayouyView: CardView

    init {
        View.inflate(context, R.layout.movie_item,this)

        posterImage = findViewById(R.id.posterImageView)
        movieName = findViewById(R.id.movieName)
        movieRating = findViewById(R.id.movieRating)
        movielayouyView = findViewById(R.id.movieCardLayout)
    }

    @ModelProp
    fun setImage(url:String?)
    {
        posterImage.loadMovieImage(url)

    }

    @ModelProp
    fun setMoviename(movie:String?)
    {
        movieName.text = movie

    }
    @ModelProp
    fun setMovieRating(rating:Float)
    {
        movieRating.rating = rating

    }

    @CallbackProp
    fun setMovieOnClickListener(listener: OnClickListener?) {
        movielayouyView.setOnClickListener(listener)
    }


}

