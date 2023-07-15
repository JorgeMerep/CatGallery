package com.challenge.catgallery.viewmodel

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.challenge.catgallery.R

class FullImageFragment : Fragment() {
    private lateinit var catImageView: ImageView
    private lateinit var downloadButton: Button
    private var imageUrl: String? = null


    companion object {
        private const val ARG_IMAGE_URL = "image_url"

        fun newInstance(imageUrl: String): FullImageFragment {
            val fragment = FullImageFragment()
            val args = Bundle()
            args.putString(ARG_IMAGE_URL, imageUrl)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val view = inflater.inflate(R.layout.fragment_full_image, container, false)

        catImageView = view.findViewById(R.id.fullImageView)
        downloadButton = view.findViewById(R.id.downloadButton)

        imageUrl = arguments?.getString(ARG_IMAGE_URL)
        imageUrl?.let {
            Glide.with(requireContext())
                .load(imageUrl)
                .into(catImageView)
        }

        downloadButton.setOnClickListener {
            downloadImage()
        }

        return view
    }

    private fun downloadImage() {
        val context = requireContext()
        imageUrl?.let { url ->
            val request = DownloadManager.Request(Uri.parse(url))
                .setTitle("Descargar imagen")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES, "CatImages.jpg")

            val downloadManager =
                context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            downloadManager.enqueue(request)

            Toast.makeText(context, "Descargando imagen...", Toast.LENGTH_SHORT).show()
        }
    }
}
