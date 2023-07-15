package com.challenge.catgallery.viewmodel

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.challenge.catgallery.R

class CatListFragment : Fragment() {
    private lateinit var catViewModel: CatViewModel
    private lateinit var catAdapter: CatAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View? {
        val view = inflater.inflate(R.layout.fragment_cat_list, container, false)

        val catsRecyclerView = view.findViewById<RecyclerView>(R.id.catsRecyclerView)

        catViewModel = ViewModelProvider(requireActivity())[CatViewModel::class.java]
        catAdapter = CatAdapter(emptyList())

        catsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        catsRecyclerView.adapter = catAdapter


        catViewModel.cats.observe(viewLifecycleOwner) { cats ->
            catAdapter.cats = cats
            catAdapter.notifyDataSetChanged()
        }

        return view
    }
}
