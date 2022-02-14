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
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.refaat.refaatcurrencyconverter.*
import com.refaat.refaatcurrencyconverter.common.*
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
    private val startForResultCurrencyFrom =
        registerForActivityResult(StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data
                // Handle the Intent
                when (intent?.getStringExtra(SELECTION_KEY)) {
                    TYPE.CURRENCY_FROM.name -> {
                        viewModel.fromCurrency.value = intent.getParcelableExtra(SELECTED_CURRENCY)
                    }
                    TYPE.CURRENCY_TO.name -> {
                        viewModel.toCurrency.value = intent.getParcelableExtra(SELECTED_CURRENCY)
                    }
                }
            }
        }
    private val eTextFromTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            if (p0.isNullOrEmpty()) {
                updateEditTextTo(0.0)
                return
            }
            updateEditTextTo(p0.toString().toDouble())
        }

        override fun afterTextChanged(p0: Editable?) {
            if (p0.isNullOrEmpty()) {
                updateEditTextTo(0.0)
                return
            }
            updateEditTextTo(p0.toString().toDouble())
        }
    }
    private val eTextToTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            if (p0.isNullOrEmpty()) {
                updateEditTextFrom(0.0)
                return
            }
            updateEditTextFrom(p0.toString().toDouble())
        }

        override fun afterTextChanged(p0: Editable?) {
            if (p0.isNullOrEmpty()) {
                updateEditTextFrom(0.0)
                return
            }
            updateEditTextFrom(p0.toString().toDouble())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        clicksConfig()

        viewModel.currenciesLiveData.observe(this, androidx.lifecycle.Observer {
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
        })
        viewModel.fromCurrency.observe(this, Observer {
            if (it != null) {
                fillCurrencyFromInfo(it)
                viewModel.getExchangeDate()
            }
        })
        viewModel.toCurrency.observe(this, Observer {
            if (it != null) {
                fillCurrencyToInfo(it)
                viewModel.getExchangeDate()
            }
        })
        viewModel.exchangeRate.observe(this, Observer {
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
                    binding.txtPairsValue.text =
                        "1 ${it.data?.currencyFrom} = ${it.data?.todayRate} ${it.data?.currencyTo}"
                    binding.statusImage.visibility = View.GONE
                    setUpHistorySection(it.data)
                }
            }
        })
        viewModel.currentRate.observe(this, Observer {
            if (binding.etxtFrom.text.toString().isNotEmpty()) {
                updateEditTextTo(binding.etxtFrom.text.toString().toDouble())
            }
        })
    }

    private fun fillCurrencyFromInfo(currencyItem: CurrencyItem) {
        Glide.with(this)
            .load(getTheFlagURL(currencyItem.id))
            .placeholder(R.drawable.img_flag_placeholder)
            .error(R.drawable.img_flag_placeholder)
            .diskCacheStrategy(DiskCacheStrategy.DATA)
            .into(binding.imgFlagFrom)
        binding.txtCurrencyFrom.text = currencyItem.currencyId
    }

    private fun fillCurrencyToInfo(currencyItem: CurrencyItem) {
        Glide.with(this)
            .load(getTheFlagURL(currencyItem.id))
            .placeholder(R.drawable.img_flag_placeholder)
            .error(R.drawable.img_flag_placeholder)
            .diskCacheStrategy(DiskCacheStrategy.DATA)
            .into(binding.imgFlagTo)
        binding.txtCurrencyTo.text = currencyItem.currencyId
    }

    private fun clicksConfig() {
        binding.lytSelectCurrencyFrom.setOnClickListener {
            startCurrencySelectionActivity(TYPE.CURRENCY_FROM)
        }
        binding.lytSelectCurrencyTo.setOnClickListener {
            startCurrencySelectionActivity(TYPE.CURRENCY_TO)
        }
        binding.etxtFrom.addTextChangedListener(eTextFromTextWatcher)
        binding.etxtTo.addTextChangedListener(eTextToTextWatcher)
    }

    fun updateEditTextFrom(value: Double) {
        binding.etxtFrom.removeTextChangedListener(eTextFromTextWatcher)
        val theValue =
            BigDecimal((value / viewModel.currentRate.value!!)).setScale(
                DECIMAL_PLACES,
                RoundingMode.HALF_EVEN
            )
        binding.etxtFrom.setText(theValue.toString())
        binding.etxtFrom.addTextChangedListener(eTextFromTextWatcher)
    }

    fun updateEditTextTo(value: Double) {
        binding.etxtTo.removeTextChangedListener(eTextToTextWatcher)
        val theValue =
            BigDecimal(value * viewModel.currentRate.value!!).setScale(
                DECIMAL_PLACES,
                RoundingMode.HALF_EVEN
            )
        binding.etxtTo.setText(theValue.toString())
        binding.etxtTo.addTextChangedListener(eTextToTextWatcher)

    }

    private fun startCurrencySelectionActivity(selectionType: TYPE) {
        startForResultCurrencyFrom.launch(
            Intent(this, ActivityCurrencySelection::class.java).putExtra(
                SELECTION_KEY, selectionType.name
            )
        )
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.action_about -> {
                startActivity(Intent(this, ActivityAbout::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setUpHistorySection(exchangeRate: ExchangeRate?) {
        //the history
        binding.lytHistoryDataContainer.removeAllViews()
        exchangeRate?.last7DaysRates?.forEach {

            binding.lytHistoryDataContainer.addView(
                getDayHistoryView(
                    sdfDayName.format(sdf.parse(it.first)),
                    exchangeRate.currencyFrom,
                    it.second,
                    exchangeRate.currencyTo
                )
            )
        }
    }

    private fun getDayHistoryView(date: String, from: String, value: Double, to: String): View? {
        val itemView: View =
            layoutInflater.inflate(R.layout.view_day_history, null, false)
        val txtDate: TextView = itemView.findViewById(R.id.txtDate)
        txtDate.text = date
        val txtValue: TextView = itemView.findViewById(R.id.txtValue)
        txtValue.text = "1 $from = $value $to"
        return itemView
    }

}