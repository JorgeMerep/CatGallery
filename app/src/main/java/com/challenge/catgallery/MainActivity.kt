package com.challenge.catgallery

import android.content.Context
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.challenge.catgallery.network.ApiClient
import com.challenge.catgallery.viewmodel.CatListFragment
import com.challenge.catgallery.viewmodel.CatViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var catViewModel: CatViewModel
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ApiClient.initialize(this)

        // Inicializa el ViewModel para la implementacion de la nueva funcionalidad
        catViewModel = ViewModelProvider(this)[CatViewModel::class.java]
        catViewModel.fetchBreeds()

        val breedEditText = findViewById<EditText>(R.id.breedEditText)
        val searchButton = findViewById<Button>(R.id.searchButton)
        val showBreedsButtonMain = findViewById<Button>(R.id.showBreedsButton)
        showBreedsButtonMain.setOnClickListener {
            showBreedsDialog()
        }


        // Inicializa el ProgressBar
        progressBar = findViewById(R.id.progressBar)

        catViewModel = ViewModelProvider(this)[CatViewModel::class.java]

        searchButton.setOnClickListener {
            val breed = breedEditText.text.toString()

            // Muestra el ProgressBar mientras se cargan los datos
            progressBar.visibility = ProgressBar.VISIBLE

            catViewModel.searchCatsByBreed(breed, 100, 10) {
                // Oculta el ProgressBar cuando se cargan los datos
                progressBar.visibility = ProgressBar.GONE
                hiddenKeyboard(this@MainActivity, searchButton)
            }
        }

        val fragment = CatListFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    //Nueva funcion que muestra el Dialog con todas las razas presentes en la API
    private fun showBreedsDialog() {
        val breeds = catViewModel.breeds.value

        val breedNames = breeds?.joinToString("\n") { it.name }

        val dialogBuilder = AlertDialog.Builder(this)
            .setTitle("All Breeds")
            .setMessage(breedNames)
            .setPositiveButton("OK", null)

        val dialog = dialogBuilder.create()
        dialog.show()
    }

    private fun hiddenKeyboard(context: Context, button: Button) {
        val keyboard = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        keyboard.hideSoftInputFromWindow(button.windowToken, 0)
    }
}
