package com.refaat.refaatcurrencyconverter.ui.currencyConversion

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.snackbar.Snackbar
import com.refaat.refaatcurrencyconverter.R
import com.refaat.refaatcurrencyconverter.common.DECIMAL_PLACES
import com.refaat.refaatcurrencyconverter.common.SELECTED_CURRENCY
import com.refaat.refaatcurrencyconverter.common.SELECTION_KEY
import com.refaat.refaatcurrencyconverter.common.Resource
import com.refaat.refaatcurrencyconverter.common.TYPE
import com.refaat.refaatcurrencyconverter.common.getTheFlagURL
import com.refaat.refaatcurrencyconverter.common.sdfDayName
import com.refaat.refaatcurrencyconverter.common.sdf
import com.refaat.refaatcurrencyconverter.databinding.ActivityMainBinding
import com.refaat.refaatcurrencyconverter.domain.model.CurrencyItem
import com.refaat.refaatcurrencyconverter.domain.model.ExchangeRate
import com.refaat.refaatcurrencyconverter.ui.about.ActivityAbout
import com.refaat.refaatcurrencyconverter.ui.currencySelection.ActivityCurrencySelection
import dagger.hilt.android.AndroidEntryPoint
import java.math.BigDecimal
import java.math.RoundingMode

@AndroidEntryPoint
class ActivityConversion : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: CurrencyConversionViewModel by viewModels()

    private var isUpdatingFrom = false
    private var isUpdatingTo = false

    private val currencyPickerLauncher =
        registerForActivityResult(StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data ?: return@registerForActivityResult
                val selected: CurrencyItem = intent.getParcelableExtra(SELECTED_CURRENCY) ?: return@registerForActivityResult
                when (intent.getStringExtra(SELECTION_KEY)) {
                    TYPE.CURRENCY_FROM.name -> viewModel.fromCurrency.value = selected
                    TYPE.CURRENCY_TO.name -> viewModel.toCurrency.value = selected
                }
            }
        }

    private val fromWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable?) {
            if (isUpdatingFrom) return
            val amount = s?.toString()?.toDoubleOrNull() ?: 0.0
            updateToField(amount)
        }
    }

    private val toWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable?) {
            if (isUpdatingTo) return
            val amount = s?.toString()?.toDoubleOrNull() ?: 0.0
            updateFromField(amount)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.etxtFrom.addTextChangedListener(fromWatcher)
        binding.etxtTo.addTextChangedListener(toWatcher)

        binding.lytSelectCurrencyFrom.setOnClickListener {
            launchCurrencyPicker(TYPE.CURRENCY_FROM)
        }
        binding.lytSelectCurrencyTo.setOnClickListener {
            launchCurrencyPicker(TYPE.CURRENCY_TO)
        }
        binding.btnSwap.setOnClickListener {
            viewModel.swapCurrencies()
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.currenciesLiveData.observe(this) {
            when (it) {
                is Resource.Loading -> showLoading(true)
                is Resource.Error -> {
                    showLoading(false)
                    showError(it.message)
                }
                is Resource.Success -> showLoading(false)
            }
        }

        viewModel.fromCurrency.observe(this) {
            if (it != null) {
                fillCurrencyInfo(it, isCurrencyFrom = true)
                viewModel.getExchangeDate()
            }
        }

        viewModel.toCurrency.observe(this) {
            if (it != null) {
                fillCurrencyInfo(it, isCurrencyFrom = false)
                viewModel.getExchangeDate()
            }
        }

        viewModel.exchangeRate.observe(this) {
            when (it) {
                is Resource.Loading -> showLoading(true)
                is Resource.Error -> {
                    showLoading(false)
                    showError(it.message)
                }
                is Resource.Success -> {
                    showLoading(false)
                    val data = it.data ?: return@observe
                    binding.txtPairsValue.text = "1 ${data.currencyFrom} = ${data.todayRate} ${data.currencyTo}"
                    populateHistory(data)
                }
            }
        }

        viewModel.currentRate.observe(this) {
            val amount = binding.etxtFrom.text?.toString()?.toDoubleOrNull() ?: return@observe
            updateToField(amount)
        }

        viewModel.lastUpdated.observe(this) { date ->
            if (date != null) {
                binding.txtLastUpdated.text = "Rates updated: $date"
                binding.txtLastUpdated.visibility = View.VISIBLE
            }
        }
    }

    private fun fillCurrencyInfo(item: CurrencyItem, isCurrencyFrom: Boolean) {
        val flagView = if (isCurrencyFrom) binding.imgFlagFrom else binding.imgFlagTo
        val codeView = if (isCurrencyFrom) binding.txtCurrencyFrom else binding.txtCurrencyTo

        Glide.with(this)
            .load(getTheFlagURL(item.currencyCode))
            .placeholder(R.drawable.img_flag_placeholder)
            .error(R.drawable.img_flag_placeholder)
            .diskCacheStrategy(DiskCacheStrategy.DATA)
            .into(flagView)
        codeView.text = item.currencyCode
    }

    private fun updateToField(fromAmount: Double) {
        val rate = viewModel.currentRate.value ?: return
        isUpdatingTo = true
        val result = BigDecimal(fromAmount * rate).setScale(DECIMAL_PLACES, RoundingMode.HALF_EVEN)
        binding.etxtTo.setText(result.toPlainString())
        isUpdatingTo = false
    }

    private fun updateFromField(toAmount: Double) {
        val rate = viewModel.currentRate.value ?: return
        if (rate == 0.0) return
        isUpdatingFrom = true
        val result = BigDecimal(toAmount / rate).setScale(DECIMAL_PLACES, RoundingMode.HALF_EVEN)
        binding.etxtFrom.setText(result.toPlainString())
        isUpdatingFrom = false
    }

    private fun launchCurrencyPicker(type: TYPE) {
        currencyPickerLauncher.launch(
            Intent(this, ActivityCurrencySelection::class.java)
                .putExtra(SELECTION_KEY, type.name)
        )
    }

    private fun showLoading(show: Boolean) {
        binding.statusImage.setImageResource(if (show) R.drawable.loading_animation else R.drawable.ic_connection_error)
        binding.statusImage.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun showError(message: String?) {
        binding.statusImage.setImageResource(R.drawable.ic_connection_error)
        binding.statusImage.visibility = View.VISIBLE
        Snackbar.make(binding.root, message ?: "Something went wrong", Snackbar.LENGTH_LONG).show()
    }

    private fun populateHistory(exchangeRate: ExchangeRate) {
        binding.lytHistoryDataContainer.removeAllViews()
        exchangeRate.last7DaysRates.forEach { (dateStr, rate) ->
            val label = try {
                sdfDayName.format(sdf.parse(dateStr)!!)
            } catch (e: Exception) {
                dateStr
            }
            binding.lytHistoryDataContainer.addView(buildHistoryRow(label, exchangeRate.currencyFrom, rate, exchangeRate.currencyTo))
        }
    }

    private fun buildHistoryRow(date: String, from: String, rate: Double, to: String): View {
        val row = layoutInflater.inflate(R.layout.view_day_history, null, false)
        row.findViewById<TextView>(R.id.txtDate).text = date
        row.findViewById<TextView>(R.id.txtValue).text = "1 $from = $rate $to"
        return row
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_refresh -> {
                viewModel.getExchangeDate()
                true
            }
            R.id.action_about -> {
                startActivity(Intent(this, ActivityAbout::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
