package com.challenge.catgallery.viewmodel

import android.app.DownloadManager
import android.content.Intent
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
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.challenge.catgallery.R
import com.challenge.catgallery.MainActivity

class FullImageFragment : Fragment() {
    private lateinit var catImageView: ImageView
    private lateinit var downloadButton: Button
    private lateinit var backToMainButton: Button
    private lateinit var backToRecyclerViewButton: Button
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
        backToMainButton = view.findViewById(R.id.backToMainButton)
        backToRecyclerViewButton = view.findViewById(R.id.backToRecyclerViewButton)

        imageUrl = arguments?.getString(ARG_IMAGE_URL)
        imageUrl?.let {
            Glide.with(requireContext())
                .load(imageUrl)
                .into(catImageView)
        }

        downloadButton.setOnClickListener {
            downloadImage()
        }

        backToMainButton.setOnClickListener {
            backToMainButton.setOnClickListener {
                val intent = Intent(requireContext(), MainActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
            }
        }

        backToRecyclerViewButton.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        val isDownloadButtonVisible = true
        downloadButton.isVisible = isDownloadButtonVisible

        val isBackToMainButtonVisible = true
        backToMainButton.isVisible = isBackToMainButtonVisible

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
                ContextCompat.getSystemService(context, DownloadManager::class.java)
            downloadManager?.enqueue(request)

            Toast.makeText(context, "Descargando imagen...", Toast.LENGTH_SHORT).show()
        }
    }
}
