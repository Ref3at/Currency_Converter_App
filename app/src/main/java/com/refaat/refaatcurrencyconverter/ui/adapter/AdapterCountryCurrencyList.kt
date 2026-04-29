package com.refaat.refaatcurrencyconverter.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.refaat.refaatcurrencyconverter.R
import com.refaat.refaatcurrencyconverter.common.getTheFlagURL
import com.refaat.refaatcurrencyconverter.databinding.ItemCountryCurrencyBinding
import com.refaat.refaatcurrencyconverter.domain.model.CurrencyItem

class AdapterCountryCurrencyList(private val onSelected: (CurrencyItem) -> Unit) :
    RecyclerView.Adapter<AdapterCountryCurrencyList.CurrencyViewHolder>(), Filterable {

    private val allItems: MutableList<CurrencyItem> = mutableListOf()
    private var filteredItems: MutableList<CurrencyItem> = mutableListOf()
    val noResultQuery: MutableLiveData<String?> = MutableLiveData()

    class CurrencyViewHolder(val binding: ItemCountryCurrencyBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrencyViewHolder {
        val binding = ItemCountryCurrencyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CurrencyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CurrencyViewHolder, position: Int) {
        val item = filteredItems[position]
        with(holder.binding) {
            txtCountryName.text = item.currencyName
            txtCurrencyNameSymbol.text = item.currencyCode
            txtCurrencyId.text = item.currencyCode

            Glide.with(holder.itemView.context)
                .load(getTheFlagURL(item.currencyCode))
                .placeholder(R.drawable.img_flag_placeholder)
                .error(R.drawable.img_flag_placeholder)
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .into(imgFlag)
        }
        holder.itemView.setOnClickListener { onSelected(item) }
    }

    override fun getItemCount(): Int = filteredItems.size

    fun updateTheList(list: List<CurrencyItem>) {
        allItems.clear()
        allItems.addAll(list)
        filteredItems.clear()
        filteredItems.addAll(list)
        notifyDataSetChanged()
    }

    override fun getFilter(): Filter = currencyFilter

    private val currencyFilter = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val query = constraint?.toString()?.lowercase().orEmpty()
            val result = if (query.isEmpty()) {
                allItems.toMutableList()
            } else {
                allItems.filter {
                    it.currencyCode.lowercase().contains(query) ||
                        it.currencyName.lowercase().contains(query)
                }.toMutableList()
            }
            return FilterResults().apply { values = result }
        }

        @Suppress("UNCHECKED_CAST")
        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            val list = results?.values as? MutableList<CurrencyItem> ?: mutableListOf()
            noResultQuery.value = if (list.isEmpty()) constraint?.toString() else null
            filteredItems = list
            notifyDataSetChanged()
        }
    }
}
