package com.kpstv.yts.extensions.common

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.ListPreloader
import com.bumptech.glide.RequestBuilder
import com.kpstv.yts.AppInterface
import com.kpstv.yts.R
import com.kpstv.yts.data.models.MovieShort
import com.kpstv.yts.extensions.MovieBase
import com.kpstv.yts.extensions.hide
import com.kpstv.yts.extensions.load
import com.kpstv.yts.extensions.utils.AppUtils
import com.kpstv.yts.extensions.utils.GlideApp
import com.kpstv.yts.ui.activities.FinalActivity
import kotlinx.android.synthetic.main.item_common_banner.view.*
import kotlinx.android.synthetic.main.item_suggestion.view.*

class CustomAdapter(
    private val context: Context,
    private val list: ArrayList<MovieShort>,
    private val base: MovieBase
) :
    RecyclerView.Adapter<CustomAdapter.CustomHolder>() {

    lateinit var setOnLongListener: (MovieShort, View) -> Unit

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomHolder {
        return CustomHolder(
            LayoutInflater.from(
                parent.context
            ).inflate(R.layout.item_suggestion, parent, false)
        )
    }

    fun getModels() = list

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: CustomHolder, i: Int) {
        val movie = list[i]

        var imageUri = movie.bannerUrl
        if (!imageUri.contains(AppInterface.YTS_BASE_URL)) {
            imageUri = AppUtils.refactorYTSUrl(imageUri)
        }

        holder.mainImage.load(
            uri = imageUri,
            onSuccess = { bitmap ->
                holder.mainImage.setImageBitmap(bitmap)
                holder.itemView.shimmerFrame.hide()
            }
        )

        holder.mainText.text = movie.title

        holder.mainCard.setOnClickListener {
            val intent = Intent(context, FinalActivity::class.java)
            when (base) {
                MovieBase.YTS -> {
                    intent.putExtra(AppInterface.MOVIE_ID, movie.movieId)
                    context.startActivity(intent)
                }
                MovieBase.TMDB -> {

                    /** We are passing movie_id as string for TMDB Movie so that in
                     * Final View Model we can use the second route to get Movie Details*/

                    intent.putExtra(AppInterface.MOVIE_ID, "${movie.movieId}")
                    context.startActivity(intent)
                }
            }
        }

        if (::setOnLongListener.isInitialized) {
            holder.mainCard.setOnLongClickListener {
                setOnLongListener.invoke(movie, it)
                return@setOnLongClickListener true
            }
        }
    }

    override fun getItemCount() = list.size

    class CustomHolder(view: View) : RecyclerView.ViewHolder(view) {
        val mainCard = view.mainCard
        val mainText = view.mainText
        val mainImage = view.mainImage
    }
}