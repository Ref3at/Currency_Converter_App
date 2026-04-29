package com.refaat.refaatcurrencyconverter.ui.currencySelection

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import com.google.android.material.snackbar.Snackbar
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
    private val viewModel: CurrencySelectionViewModel by viewModels()
    private val adapter = AdapterCountryCurrencyList { handleSelectedItem(it) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCurrencySelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        title = "Select Currency"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        binding.recyclerView.adapter = adapter

        binding.searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                adapter.filter.filter(query)
                return true
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)
                return true
            }
        })

        viewModel.currenciesLiveData.observe(this) {
            when (it) {
                is Resource.Loading -> {
                    binding.statusImage.setImageResource(R.drawable.loading_animation)
                    binding.statusImage.visibility = View.VISIBLE
                }
                is Resource.Error -> {
                    binding.statusImage.setImageResource(R.drawable.ic_connection_error)
                    binding.statusImage.visibility = View.VISIBLE
                    Snackbar.make(binding.root, it.message ?: "Error loading currencies", Snackbar.LENGTH_LONG).show()
                }
                is Resource.Success -> {
                    binding.statusImage.visibility = View.GONE
                }
            }
            it.data?.let { list -> adapter.updateTheList(list) }
        }

        adapter.noResultQuery.observe(this) { query ->
            if (query.isNullOrEmpty()) {
                binding.txtNoCurrency.visibility = View.GONE
                binding.recyclerView.visibility = View.VISIBLE
            } else {
                binding.recyclerView.visibility = View.GONE
                binding.txtNoCurrency.visibility = View.VISIBLE
                binding.txtNoCurrency.text = "No currency available for \"$query\""
            }
        }
    }

    private fun handleSelectedItem(item: CurrencyItem) {
        setResult(RESULT_OK, Intent().apply {
            putExtra(SELECTION_KEY, intent.getStringExtra(SELECTION_KEY))
            putExtra(SELECTED_CURRENCY, item)
        })
        finish()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) finish()
        return super.onOptionsItemSelected(item)
    }
}
