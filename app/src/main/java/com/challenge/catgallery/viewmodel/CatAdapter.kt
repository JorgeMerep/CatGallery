package com.challenge.catgallery.viewmodel

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.challenge.catgallery.R
import com.challenge.catgallery.model.Cat

class CatAdapter(var cats: MutableList<Cat>, private val loadMoreCallback: () -> Unit) :
    RecyclerView.Adapter<CatAdapter.ViewHolder>() {

    private val visibleThreshold = 5
    private var loading = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_cat, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val cat = cats[position]
        val imageView = holder.itemView.findViewById<ImageView>(R.id.catImageView)

        Glide.with(imageView.context)
            .load(cat.url)
            .into(imageView)

        imageView.setOnClickListener {
            val context = imageView.context
            if (context is AppCompatActivity) {
                val fragment = FullImageFragment.newInstance(cat.url)
                val fragmentManager = context.supportFragmentManager
                fragmentManager.beginTransaction()
                    .replace(android.R.id.content, fragment)
                    .addToBackStack(null)
                    .commit()
            }
        }

        // Detecta el último elemento visible para cargar más datos
        if (position == cats.size - visibleThreshold && !loading) {
            loading = true
            loadMoreCallback.invoke() // Llama al callback para cargar más datos
        }
    }

    override fun getItemCount(): Int {
        return cats.size
    }

    fun updateCats(newCats: List<Cat>) {
        cats.clear()
        cats.addAll(newCats)
        notifyDataSetChanged()
    }

    fun addCats(newCats: List<Cat>) {
        val startPos = cats.size
        cats.addAll(newCats)
        notifyItemRangeInserted(startPos, newCats.size)
        loading = false
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}
