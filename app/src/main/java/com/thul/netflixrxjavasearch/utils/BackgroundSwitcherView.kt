package com.thul.netflixrxjavasearch.utils

import android.animation.Animator
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Handler
import android.util.AttributeSet
import android.view.animation.Animation
import android.widget.ImageSwitcher
import android.widget.ImageView
import com.squareup.picasso.Picasso
import com.squareup.picasso.Picasso.LoadedFrom
import com.squareup.picasso.Target

class BackgroundSwitcherView : ImageSwitcher {
    private val NORMAL_ORDER = intArrayOf(0, 1)
    private var bgImageGap = 0
    private var bgImageWidth = 0
    private var bgImageInLeftAnimation: Animation? = null
    private var bgImageOutLeftAnimation: Animation? = null
    private var bgImageInRightAnimation: Animation? = null
    private var bgImageOutRightAnimation: Animation? = null
    private val movementDuration = 500
    private val widthBackgroundImageGapPercent = 12
    private var currentAnimationDirection: AnimationDirection? = null

    constructor(
        context: Context,
        attrs: AttributeSet?
    ) : super(context, attrs) {
        inflateAndInit(context)
    }

    constructor(context: Context) : super(context) {
        inflateAndInit(context)
    }

    private fun inflateAndInit(context: Context) {
        isChildrenDrawingOrderEnabled = true
        val displayMetrics = context.resources.displayMetrics
        bgImageGap = displayMetrics.widthPixels / 100 * widthBackgroundImageGapPercent
        bgImageWidth = displayMetrics.widthPixels + bgImageGap * 2
        setFactory {
            val myView = ImageView(context)
            myView.scaleType = ImageView.ScaleType.CENTER_CROP
            myView.layoutParams = LayoutParams(
                bgImageWidth,
                LayoutParams.MATCH_PARENT
            )
            myView.translationX = -bgImageGap.toFloat()
            myView
        }
        bgImageInLeftAnimation = AnimUtils.createBgImageInAnimation(bgImageGap, 0, movementDuration)
        bgImageOutLeftAnimation =
            AnimUtils.createBgImageOutAnimation(0, -bgImageGap, movementDuration)
        bgImageInRightAnimation =
            AnimUtils.createBgImageInAnimation(-bgImageGap, 0, movementDuration)
        bgImageOutRightAnimation =
            AnimUtils.createBgImageOutAnimation(0, bgImageGap, movementDuration)
    }

    override fun getChildDrawingOrder(childCount: Int, i: Int): Int {
        return NORMAL_ORDER[i]
    }

    @Synchronized
    private fun setImageBitmapWithAnimation(
        newBitmap: Bitmap,
        animationDirection: AnimationDirection
    ) {
        if (animationDirection == AnimationDirection.LEFT) {
            this.inAnimation = bgImageInLeftAnimation
            this.outAnimation = bgImageOutLeftAnimation
            setImageBitmap(newBitmap)
        } else if (animationDirection == AnimationDirection.RIGHT) {
            this.inAnimation = bgImageInRightAnimation
            this.outAnimation = bgImageOutRightAnimation
            setImageBitmap(newBitmap)
        }
    }

    fun updateCurrentBackground(imageUrl: String?) {
        currentAnimationDirection = AnimationDirection.RIGHT
        val image = this.nextView as ImageView
        image.setImageDrawable(null)
        showNext()
        if (imageUrl == null) return
        Picasso.get().load(imageUrl)
            .noFade().noPlaceholder()
            .into(object : Target {
                override fun onBitmapLoaded(bitmap: Bitmap, from: LoadedFrom) {
                    setImageBitmapWithAnimation(bitmap, currentAnimationDirection!!)
                }

                override fun onBitmapFailed(
                    e: Exception,
                    errorDrawable: Drawable
                ) {
                    println("@#@#@#@#@" + e.message)
                }

                override fun onPrepareLoad(placeHolderDrawable: Drawable) {}
            })
    }

    private fun setImageBitmap(bitmap: Bitmap) {
        val image = this.nextView as ImageView
        image.setImageDrawable(null)
        val duration = 0
        animate().alpha(0.0f).setDuration(duration.toLong())
            .setListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {}
                override fun onAnimationEnd(animation: Animator) {
                    image.setImageBitmap(bitmap)
                    Handler().postDelayed({
                        animate().alpha(0.4f).duration = duration.toLong()
                    }, 200)
                }

                override fun onAnimationCancel(animation: Animator) {}
                override fun onAnimationRepeat(animation: Animator) {}
            })
        showNext()
    }

    fun clearImage() {
        val image = this.nextView as ImageView
        image.setImageDrawable(null)
        showNext()
    }

    enum class AnimationDirection {
        LEFT, RIGHT
    }
}