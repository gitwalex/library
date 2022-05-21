package com.gerwalex.lib.database

import android.annotation.SuppressLint
import androidx.room.Ignore
import androidx.room.TypeConverter
import com.google.android.material.slider.LabelFormatter
import java.math.BigDecimal
import java.sql.Date
import java.text.DateFormat
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*

object MyConverter {

    @Ignore
    val NACHKOMMA = 1000000L

    @SuppressLint("ConstantLocale")
    private val cf = NumberFormat.getCurrencyInstance(Locale.getDefault()) as DecimalFormat
    private val di = DateFormat.getDateInstance(DateFormat.DEFAULT)

    @SuppressLint("ConstantLocale")
    private val units = Math.pow(10.0, Currency
        .getInstance(Locale.getDefault())
        .defaultFractionDigits.toDouble())
    var currencyLabelFormatter = LabelFormatter { obj: Float -> convertCurrency(obj) }
    var percentLabelFormatter = LabelFormatter { obj: Float -> convertPercent(obj.toDouble()) }

    /**
     * Liefert den letzten Teil des Catnamen
     */
    @JvmStatic
    fun convertCatname(longcatname: String?): String? {
        if (longcatname != null) {
            val value = longcatname
                .split(":")
                .toTypedArray()
            return value[value.size - 1]
        }
        return null
    }

    /**
     * Convertiert einen Geldbetrag in das Anzeigeformat der Anzeigewährung
     *
     * @param amount Betrag
     * @return Anzeigeformat
     */
    @JvmStatic
    @TypeConverter
    fun convertCurrency(amount: Float): String {
        return cf.format(amount / units)
    }

    /**
     * Convertiert einen Geldbetrag in das Anzeigeformat der Anzeigewährung
     *
     * @param amount Betrag
     * @return Anzeigeformat
     */
    @JvmStatic
    @TypeConverter
    fun convertCurrency(amount: Long?): String? {
        return if (amount != null) NumberFormat
            .getCurrencyInstance()
            .format(amount) else null
    }

    /**
     * Convertiert einen Geldbetrag in das Anzeigeformat der Anzeigewährung
     *
     * @param amount Betrag
     * @return Anzeigeformat
     */
    @JvmStatic
    @TypeConverter
    fun convertCurrency(amount: BigDecimal?): String? {
        return if (amount != null) NumberFormat
            .getCurrencyInstance()
            .format(amount) else null
    }

    /**
     * Convertiert ein Date
     */
    @JvmStatic
    fun convertDate(date: Date?): String? {
        return if (date == null) null else di.format(date)
    }

    @JvmStatic
    @SuppressLint("DefaultLocale")
    fun convertPercent(value: Double): String {
        return String.format("%.2f%%", value * 100)
    }

    /**
     * Konvertiert einen Wert nach Long.
     *
     * @param value            zu konvertierender Wert. Nachkommastellen werden durch den Dezimalpunkt abgetrennt
     * @param nachkommastellen Anzahl der Nachkommastellen, z.B. bei Euro = 2
     * @return value als Long. Konvertierung wie folgt:
     */
    @JvmStatic
    fun convertToLong(value: String?, nachkommastellen: Int): Long {
        if (value != null) {
            val split = value
                .split("\\.")
                .toTypedArray()
            if (split.size > 2) {
                throw NumberFormatException("$value kann nicht konvertiert werden. Mehr als ein Dezimalpunkt")
            }
            var `val` = (split[0].toLong() * Math.pow(10.0, nachkommastellen.toDouble())).toLong()
            if (split.size == 2) {
                val nk = (split[1] + "0000000000000").substring(0, nachkommastellen)
                if (`val` < 0) {
                    `val` -= nk.toLong()
                } else {
                    `val` += nk.toLong()
                }
            }
            return `val`
        }
        return 0
    }

    /**
     * Konvertiert Datum im Format 'yyyy-MM-dd' in ein Date
     */
    @JvmStatic
    @TypeConverter
    fun toDate(date: String?): Date? {
        return if (date == null) null else Date.valueOf(date)
    }

    /**
     * Konvertiert Date in einen String im Format 'yyyy-MM-dd'
     */
    @JvmStatic
    @TypeConverter
    fun toString(date: Date?): String? {
        return date?.toString()
    }
}