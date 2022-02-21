package com.refaat.refaatcurrencyconverter.ui.currencySelection

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import com.refaat.refaatcurrencyconverter.R
import com.refaat.refaatcurrencyconverter.common.Resource
import com.refaat.refaatcurrencyconverter.common.SELECTED_CURRENCY
import com.refaat.refaatcurrencyconverter.common.SELECTION_KEY
import com.refaat.refaatcurrencyconverter.databinding.ActivityCurrencySelectionBinding
import com.refaat.refaatcurrencyconverter.domain.model.CurrencyItem
import com.refaat.refaatcurrencyconverter.ui.adapter.AdapterCountryCurrencyList
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ActivityCurrencySelection : AppCompatActivity() {

    private lateinit var binding: ActivityCurrencySelectionBinding
    private val adapter =
        AdapterCountryCurrencyList { selectedItem: CurrencyItem -> handleSelectedItem(selectedItem) }
    private val viewModel: CurrencySelectionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCurrencySelectionBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        title = "Select the Currency"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        viewModel.currenciesLiveData.observe(this, Observer {
            when (it) {
                is Resource.Loading -> {
                    binding.statusImage.setImageResource(R.drawable.loading_animation)
                    binding.statusImage.visibility = View.VISIBLE
                }
                is Resource.Error -> {
                    binding.statusImage.setImageResource(R.drawable.ic_connection_error)
                    binding.statusImage.visibility = View.VISIBLE
                    Toast.makeText(
                        this,
                        it.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
                is Resource.Success -> {
                    binding.statusImage.visibility = View.GONE
                }
            }

            if (it.data != null) {
                adapter.updateTheList(it.data)
            }
        })

        adapter.noResultQuery.observe(this, Observer {
            if (it.isNullOrEmpty()) {
                binding.txtNoCurrency.visibility = View.GONE
                binding.recyclerView.visibility = View.VISIBLE
            } else {
                binding.recyclerView.visibility = View.GONE
                binding.txtNoCurrency.visibility = View.VISIBLE
                binding.txtNoCurrency.text = "No currency available for \"${it}\""

            }
        })

        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            )
        )
        binding.recyclerView.adapter = adapter

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {

                adapter.filter.filter(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)
                return true

            }

        })
    }

    private fun handleSelectedItem(selectedCurrencyItem: CurrencyItem) {

        val returnIntent = Intent()
        returnIntent.putExtra(SELECTION_KEY, intent.getStringExtra(SELECTION_KEY))
        returnIntent.putExtra(SELECTED_CURRENCY, selectedCurrencyItem)
        setResult(RESULT_OK, returnIntent)
        finish()
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }


}