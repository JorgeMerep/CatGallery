package com.challenge.catgallery

import android.content.Context
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.challenge.catgallery.network.ApiClient
import com.challenge.catgallery.viewmodel.CatListFragment
import com.challenge.catgallery.viewmodel.CatViewModel


class MainActivity : AppCompatActivity() {

    private lateinit var catViewModel: CatViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ApiClient.initialize(this)

        val breedEditText = findViewById<EditText>(R.id.breedEditText)
        val searchButton = findViewById<Button>(R.id.searchButton)

        catViewModel = ViewModelProvider(this)[CatViewModel::class.java]

        searchButton.setOnClickListener {
            val breed = breedEditText.text.toString()
            catViewModel.searchCatsByBreed(breed, 10)
            hiddenKeyboard(this, searchButton)
        }


        val fragment = CatListFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    private fun hiddenKeyboard(context: Context, button: Button) {
        val keyboard = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        keyboard.hideSoftInputFromWindow(button.windowToken, 0)
    }
}
