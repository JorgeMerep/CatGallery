package com.challenge.catgallery.viewmodel

import android.icu.util.ULocale
import android.icu.util.ULocale.*
import android.nfc.tech.MifareUltralight.PAGE_SIZE
import android.os.Bundle
import android.provider.MediaStore.Video.VideoColumns.CATEGORY
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.challenge.catgallery.R

class CatListFragment : Fragment() {
    private lateinit var catViewModel: CatViewModel
    private lateinit var catAdapter: CatAdapter

    private var isLoading = false
    private var isLastPage = false
    private var currentPage = 1
    private var totalPage = 10
    private var currentBreed = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View? {
        val view = inflater.inflate(R.layout.fragment_cat_list, container, false)

        val catsRecyclerView = view.findViewById<RecyclerView>(R.id.catsRecyclerView)

        catViewModel = ViewModelProvider(requireActivity())[CatViewModel::class.java]

        val layoutManager = LinearLayoutManager(requireContext())
        catsRecyclerView.layoutManager = layoutManager

        catAdapter = CatAdapter(mutableListOf()) {
            loadMoreCats()
        }

        catsRecyclerView.adapter = catAdapter

        catsRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if (!isLoading && !isLastPage) {
                    val visibleItemCount = layoutManager.childCount
                    val totalItemCount = layoutManager.itemCount
                    val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()

                    if (visibleItemCount + lastVisibleItemPosition >= totalItemCount &&
                        lastVisibleItemPosition >= totalItemCount - 1
                    ) {
                        catViewModel.loadMoreCatsIfNeeded(visibleItemCount, lastVisibleItemPosition)
                    }
                }
            }
        })

        // Observe cambios en la lista de gatos
        catViewModel.cats.observe(viewLifecycleOwner, Observer { cats ->
            if (cats != null) {
                if (currentPage == 1) {
                    catAdapter.updateCats(cats)
                } else {
                    catAdapter.addCats(cats)
                }
            }
        })

        // Observar isLoading y isLastPage
        catViewModel.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            this.isLoading = isLoading
        })

        catViewModel.isLastPage.observe(viewLifecycleOwner, Observer { isLastPage ->
            this.isLastPage = isLastPage
        })

        return view
    }


    private fun loadMoreCats() {
        catViewModel.searchCatsByBreed(currentBreed, PAGE_SIZE, currentPage + 1) {
            isLoading = false
        }
    }
}
