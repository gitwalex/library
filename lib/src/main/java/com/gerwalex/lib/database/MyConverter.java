package com.gerwalex.lib.database;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Ignore;
import androidx.room.TypeConverter;

import com.google.android.material.slider.LabelFormatter;

import java.math.BigDecimal;
import java.sql.Date;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

public class MyConverter {
    @Ignore
    public static final long NACHKOMMA = 1000000L;
    @SuppressLint("ConstantLocale")
    private static final DecimalFormat cf = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.getDefault());
    private static final DateFormat di = DateFormat.getDateInstance(DateFormat.DEFAULT);
    @SuppressLint("ConstantLocale")
    private static final double units = Math.pow(10, Currency
            .getInstance(Locale.getDefault())
            .getDefaultFractionDigits());
    public static LabelFormatter currencyLabelFormatter = MyConverter::convertCurrency;
    public static LabelFormatter percentLabelFormatter = MyConverter::convertPercent;

    /**
     * Liefert den letzten Teil des Catnamen
     */
    public static String convertCatname(String longcatname) {
        if (longcatname != null) {
            String[] value = longcatname.split(":");
            return value[value.length - 1];
        }
        return null;
    }

    /**
     * Convertiert einen Geldbetrag in das Anzeigeformat der Anzeigewährung
     *
     * @param amount Betrag
     * @return Anzeigeformat
     */
    @NonNull
    @TypeConverter
    public static String convertCurrency(float amount) {
        return cf.format(amount / units);
    }

    /**
     * Convertiert einen Geldbetrag in das Anzeigeformat der Anzeigewährung
     *
     * @param amount Betrag
     * @return Anzeigeformat
     */
    @TypeConverter
    public static String convertCurrency(Long amount) {
        return amount != null ? cf.format(amount / units) : null;
    }

    /**
     * Convertiert einen Geldbetrag in das Anzeigeformat der Anzeigewährung
     *
     * @param amount Betrag
     * @return Anzeigeformat
     */
    @TypeConverter
    public static String convertLongCurrency(Long amount) {
        return amount != null ? NumberFormat
                .getCurrencyInstance()
                .format(amount / units) : null;
    }

    public static String convertCurrency(BigDecimal amount) {
        return amount != null ? NumberFormat
                .getCurrencyInstance()
                .format(amount) : null;
    }

    /**
     * Convertiert ein Date
     */
    public static String convertDate(@Nullable Date date) {
        return date == null ? null : di.format(date);
    }

    @NonNull
    @SuppressLint("DefaultLocale")
    public static String convertPercent(double value) {
        return String.format("%.2f%%", value * 100);
    }

    /**
     * Konvertiert einen Wert nach Long.
     *
     * @param value            zu konvertierender Wert. Nachkommastellen werden durch den Dezimalpunkt abgetrennt
     * @param nachkommastellen Anzahl der Nachkommastellen, z.B. bei Euro = 2
     * @return value als Long. Konvertierung wie folgt:
     */
    public static long convertToLong(String value, int nachkommastellen) {
        if (value != null) {
            String[] split = value.split("\\.");
            if (split.length > 2) {
                throw new NumberFormatException(
                        value + " kann nicht konvertiert " + "werden. Mehr als ein Dezimalpunkt");
            }
            long val = (long) (Long.parseLong(split[0]) * Math.pow(10, nachkommastellen));
            if (split.length == 2) {
                String nk = (split[1] + "0000000000000").substring(0, nachkommastellen);
                if (val < 0) {
                    val -= Long.parseLong(nk);
                } else {
                    val += Long.parseLong(nk);
                }
            }
            return val;
        }
        return 0;
    }

    /**
     * Konvertiert Datum im Format 'yyyy-MM-dd' in ein Date
     */
    @TypeConverter
    public static Date toDate(String date) {
        return date == null ? null : Date.valueOf(date);
    }

    /**
     * Konvertiert Date in einen String im Format 'yyyy-MM-dd'
     */
    @TypeConverter
    public static String toString(Date date) {
        return date == null ? null : date.toString();
    }
}