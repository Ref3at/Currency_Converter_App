package com.refaat.refaatcurrencyconverter.domain.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CurrencyItem(
    @PrimaryKey val currencyCode: String,
    val currencyName: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(currencyCode)
        parcel.writeString(currencyName)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<CurrencyItem> {
        override fun createFromParcel(parcel: Parcel): CurrencyItem = CurrencyItem(parcel)
        override fun newArray(size: Int): Array<CurrencyItem?> = arrayOfNulls(size)
    }
}
