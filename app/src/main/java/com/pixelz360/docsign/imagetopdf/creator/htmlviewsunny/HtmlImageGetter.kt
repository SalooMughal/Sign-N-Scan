package com.shaun.htmlviewsunny

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.text.Html
import android.widget.TextView
import com.bumptech.glide.Glide
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class HtmlImageGetter(
    private val res: Resources,
    private val htmlTextView: TextView,
    private val width: Int,
    private val height: Int,
    private val boundsLeft: Int,
    private val boundsTop: Int,
    private val context: Context
) : Html.ImageGetter {

    override fun getDrawable(url: String): Drawable {
        val holder = BitmapDrawablePlaceHolder(res, null)

        GlobalScope.launch {
            runCatching {
//                val bitmap = Picasso.get().load(url)
//                    .get()
//                val bitmap =  Glide.with(context)
//                    .load(url)
//                    .into(profilPicture)
//
//                val drawable = BitmapDrawable(res, bitmap)


//                val id: Int = context.getResources().getIdentifier(url, "drawable",  context.getPackageName())
//                val drawable: Drawable = context.getResources().getDrawable(id)

                val drawable = Drawable.createFromStream(context.contentResolver.openInputStream(Uri.parse(url)), null)


                var widthCanvas = getScreenWidth() - 100
                val aspectRatio: Float =
                    (drawable!!.intrinsicWidth.toFloat()) / (drawable.intrinsicHeight.toFloat())
                var heightCanvas = (widthCanvas / aspectRatio).toInt()

                if(width!=-1 && height!=-1){
                    widthCanvas=width
                    heightCanvas=height
                }

                var boundsLeftCanvas=10
                var boundsTopCanvas=20
                if(boundsLeft!=-1 && boundsTop!=-1){
                    boundsLeftCanvas=boundsLeft
                    boundsTopCanvas=boundsTop
                }


                drawable!!.setBounds(boundsLeftCanvas, boundsTopCanvas, widthCanvas, heightCanvas)

                holder.setDrawable(drawable)


                holder.setBounds(boundsLeftCanvas, boundsTopCanvas, widthCanvas, heightCanvas)

                withContext(Dispatchers.Main) {
                    htmlTextView.text = htmlTextView.text
                }
            }
        }

        return holder
    }

    internal class BitmapDrawablePlaceHolder(res: Resources, bitmap: Bitmap?) : BitmapDrawable(res, bitmap) {
        private var drawable: Drawable? = null

        override fun draw(canvas: Canvas) {
            drawable?.run { draw(canvas) }
        }

        fun setDrawable(drawable: Drawable) {
            this.drawable = drawable
        }
    }

    private fun getScreenWidth() =
        Resources.getSystem().displayMetrics.widthPixels


}