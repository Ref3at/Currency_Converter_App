package com.refaat.refaatcurrencyconverter.domain.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

@Entity
data class CurrencyItem (
    @PrimaryKey (autoGenerate = true)val uid: Int,
    @SerializedName("alpha3")
    @Expose
    var alpha3: String? = null,
    @SerializedName("currencyId")
    @Expose
    var currencyId: String? = null,
    @SerializedName("currencyName")
    @Expose
    var currencyName: String? = null,
    @SerializedName("currencySymbol")
    @Expose
    var currencySymbol: String? = null,
    @SerializedName("id")
    @Expose
    var id: String? = null,
    @SerializedName("name")
    @Expose
    var name: String? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(uid)
        parcel.writeString(alpha3)
        parcel.writeString(currencyId)
        parcel.writeString(currencyName)
        parcel.writeString(currencySymbol)
        parcel.writeString(id)
        parcel.writeString(name)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CurrencyItem> {
        override fun createFromParcel(parcel: Parcel): CurrencyItem {
            return CurrencyItem(parcel)
        }

        override fun newArray(size: Int): Array<CurrencyItem?> {
            return arrayOfNulls(size)
        }
    }
}