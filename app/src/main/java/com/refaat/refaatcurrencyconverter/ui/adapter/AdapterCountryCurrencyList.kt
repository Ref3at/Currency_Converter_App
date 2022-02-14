package com.refaat.refaatcurrencyconverter.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.refaat.refaatcurrencyconverter.R
import com.refaat.refaatcurrencyconverter.common.getTheFlagURL
import com.refaat.refaatcurrencyconverter.databinding.ItemCountryCurrencyBinding
import com.refaat.refaatcurrencyconverter.domain.model.CurrencyItem


class AdapterCountryCurrencyList :
    RecyclerView.Adapter<AdapterCountryCurrencyList.CountryCurrencyViewHolder>(), Filterable {

    private val allCurrencyItemList: MutableList<CurrencyItem> = ArrayList()
    private var filteredCurrencyItemList: MutableList<CurrencyItem> = ArrayList()
    val noResultQuery: MutableLiveData<String?> = MutableLiveData()

    class CountryCurrencyViewHolder(val binding: ItemCountryCurrencyBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CountryCurrencyViewHolder {
        val binding =
            ItemCountryCurrencyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CountryCurrencyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CountryCurrencyViewHolder, position: Int) {
        with(holder) {
            with(filteredCurrencyItemList[position]) {
                binding.txtCountryName.text = this.name
                binding.txtCurrencyNameSymbol.text = this.currencyName + " (${this.currencySymbol})"
                binding.txtCurrencyId.text = this.currencyId

                Glide.with(holder.itemView.context)
                    .load(getTheFlagURL(this.id))
                    .placeholder(R.drawable.img_flag_placeholder)
                    .error(R.drawable.img_flag_placeholder)
                    .diskCacheStrategy(DiskCacheStrategy.DATA)
                    .into(binding.imgFlag)
            }
        }

        holder.itemView.setOnClickListener {
            (holder.itemView.context as ICurrencySelection).selectedItem(filteredCurrencyItemList[position])
        }
    }

    override fun getItemCount(): Int {
        return filteredCurrencyItemList.size
    }


    fun updateTheList(currencyItemList: List<CurrencyItem>) {
        this.allCurrencyItemList.clear()
        this.allCurrencyItemList.addAll(currencyItemList)

        this.filteredCurrencyItemList.clear()
        this.filteredCurrencyItemList.addAll(currencyItemList)
        notifyDataSetChanged()
    }

    override fun getFilter(): Filter {
        return currencyFilter
    }


    private val currencyFilter = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val filteredList = mutableListOf<CurrencyItem>()
            if (constraint == null || constraint.isEmpty()) {
                filteredList.addAll(allCurrencyItemList)
            } else {
                for (currencyItem in allCurrencyItemList) {
                    if (currencyItem.name?.lowercase()
                            ?.contains(
                                constraint.toString().lowercase()
                            ) == true || currencyItem.currencyName?.lowercase()
                            ?.contains(constraint.toString().lowercase()) == true

                        || currencyItem.currencyId?.lowercase()
                            ?.contains(constraint.toString().lowercase()) == true
                    ) {
                        filteredList.add(currencyItem)
                    }
                }
            }
            val results = FilterResults()
            results.values = filteredList
            return results
        }

        override fun publishResults(constraint: CharSequence?, filterResults: FilterResults?) {

            if (filterResults == null || (filterResults.values as ArrayList<CurrencyItem>).size == 0) {
                noResultQuery.value = constraint.toString()
            } else {
                noResultQuery.value = null
            }

            filteredCurrencyItemList.clear()
            filteredCurrencyItemList.addAll(filterResults?.values as ArrayList<CurrencyItem>)
            notifyDataSetChanged()
        }

    }

}

interface ICurrencySelection {
    fun selectedItem(selectedCurrencyItem: CurrencyItem)
}
