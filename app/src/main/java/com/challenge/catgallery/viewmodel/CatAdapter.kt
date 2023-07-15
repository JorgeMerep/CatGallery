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

class CatAdapter(var cats: List<Cat>) : RecyclerView.Adapter<CatAdapter.ViewHolder>() {

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
            .placeholder(R.drawable.ic_clock) // Imagen de reloj mientras se carga la imagen
            .error(R.drawable.ic_error) // Imagen de error en caso de que no se pueda cargar la imagen
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
    }

    override fun getItemCount(): Int {
        return cats.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    }
}
