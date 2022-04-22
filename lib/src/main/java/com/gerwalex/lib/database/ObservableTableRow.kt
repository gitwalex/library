package com.gerwalex.lib.database

import android.content.ContentValues
import android.database.Cursor
import androidx.annotation.CallSuper
import androidx.databinding.BaseObservable
import androidx.room.Ignore
import com.gerwalex.lib.main.App
import java.sql.Date
import java.util.*

open class ObservableTableRow : BaseObservable {

    /**
     * Abbild der jeweiligen Zeile der Datenbank. Werden nicht direkt geaendert.
     */
    @Ignore
    private val currentContent = ContentValues()

    @Ignore
    var isInserted = false

    /**
     * @return Liefert eine Kopie der akteullen  Werte zurueck.
     */
    val content: ContentValues
        get() = ContentValues(currentContent)

    /**
     * Konstruktor mit Cursor.
     */
    @Ignore
    constructor(c: Cursor) {
        fillContent(c)
        isInserted = containsKey("_id")
    }

    @Ignore
    protected constructor()

    /**
     * Prueft, ob ein Wert geaendert wurde oder bereits in der DB vorhanden ist.
     *
     * @param column Spaltenname
     * @return true, wenn ein Wert ungleich null enthalten ist
     */
    fun containsKey(column: String): Boolean {
        return currentContent.containsKey(column)
    }

    open suspend fun delete() {
        throw IllegalStateException("Delete not implented")
    }

    /**
     * @param other zu vergleichendes Object.
     * @return true,  wenn das vergleichende Object ein Geschaeftsobjekte ist, beide auf die gleiche
     * Tabelle zeigen (DBDefiniton)und die gleiche Anzahl Werte mit den gleichen Inhalten in den
     * ContentValues vorhanden sind.
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other == null || javaClass != other.javaClass) {
            return false
        }
        val that = other as ObservableTableRow
        return currentContent == that.currentContent
    }

    /**
     * Fuellt currentContent aus einem Cursor. Cursor darf nicht leer sein. Hat der Cursor mehrere
     * Zeilen, wird nur die erste uebernommen. Es werden nur Werte ungleich null uebernommen.
     *
     *
     * Alle vorigen Werte werden verworfen.
     *
     * @param c Cursor
     * @throws IllegalArgumentException wenn Cursor leer ist
     */
    fun fillContent(c: Cursor) {
        require(!(c.isBeforeFirst && !c.moveToFirst())) { "Cursor ist leer!" }
        for (i in 0 until c.columnCount) {
            val colName = c.getColumnName(i)
            val type = c.getType(i)
            when (type) {
                Cursor.FIELD_TYPE_BLOB ->
                    currentContent.put(colName, c.getBlob(i))
                Cursor.FIELD_TYPE_FLOAT -> currentContent.put(colName, c.getFloat(i))
                Cursor.FIELD_TYPE_INTEGER -> currentContent.put(colName, c.getLong(i))
                Cursor.FIELD_TYPE_STRING -> currentContent.put(colName, c.getString(i))
                Cursor.FIELD_TYPE_NULL -> putNull(colName)
                else -> {
                    val value = c.getString(i)
                    if (value != null) {
                        currentContent.put(colName, value)
                    }
                }
            }
        }
    }

    /**
     * Liefert den Wert aus dem Geschaeftsobjekt zu Spalte  als Boolean.
     *
     * @param column Spalte
     * @return Den aktuellen Wert der Spalte (true ooder false)
     */
    fun getAsBoolean(column: String): Boolean {
        return getAsInt(column) != 0
    }

    fun getAsByteArray(column: String): ByteArray {
        return currentContent.getAsByteArray(column)
    }

    /**
     * Konvertiert ein Datum zurueck.
     *
     * @param column name der Spalte
     * @return Date-Objekt oder null
     */
    fun getAsDate(column: String): Date {
        return MyConverter.toDate(getAsString(column))
    }

    fun getAsDouble(column: String): Double {
        return currentContent.getAsDouble(column)
    }

    /**
     * Liefert den aktuellsten Wert aus dem Geschaeftsobjekt zu Spalte  als Long.
     *
     * @param column name der Spalte
     * @return Den aktuellen Wert der Spalte oder null, wenn nicht vorhanden
     */
    fun getAsFloat(column: String): Float {
        return currentContent.getAsFloat(column)
    }

    /**
     * Liefert den aktuellsten Wert aus dem Geschaeftsobjekt zu Spalte als Integer.
     *
     * @param column name der Spalte
     * @return Den aktuellen Wert der Spalte oder null, wenn nicht vorhanden
     */
    fun getAsInt(column: String): Int {
        return if (isNull(column)) 0 else currentContent.getAsInteger(column)
    }

    fun getAsLong(column: String): Long {
        return if (isNull(column)) 0 else currentContent.getAsLong(column)
    }

    /**
     * Prueft, ob column null ist
     *
     * @param column column
     * @return null, wenn column null ist. sonst einen Long-wert
     */
    fun getAsLongOrNull(column: String): Long? {
        return if (isNull(column)) null else getAsLong(column)
    }

    /**
     * Liefert den aktuellsten Wert aus dem Geschaeftsobjekt zu Spalte  als String.
     *
     * @param column name der Spalte
     * @return Den aktuellen Wert der Spalte oder null, wenn nicht vorhanden
     */
    fun getAsString(column: String): String? {
        return currentContent.getAsString(column)
    }

    override fun hashCode(): Int {
        return Objects.hash(currentContent)
    }

    open suspend fun insert(): Long {
        throw IllegalStateException("Insert not implented")
    }

    /**
     * Prueft, ob ein Wert vorhanden ist.
     *
     * @param column Spaltenname
     * @return true, wenn vorhanden. Sonst false
     */
    fun isNull(column: String): Boolean {
        return currentContent[column] == null
    }

    /**
     * Aendert oder Fuegt Daten ein.
     *
     * @param column name der Spalte, die eingefuegt werden soll.
     * @param value  Wert, der eingefuegt werden soll.
     */
    fun put(column: String, value: Boolean): Boolean {
        return put(column, if (value) 1 else 0)
    }

    @CallSuper
    fun put(column: String, value: Int): Boolean {
        val oldval = getAsInt(column)
        currentContent.put(column, value)
        return oldval == value
    }

    @CallSuper
    fun put(column: String, value: Long): Boolean {
        val oldval = getAsLong(column)
        currentContent.put(column, value)
        return oldval == value
    }

    fun put(column: String, value: Float): Boolean {
        val oldval = getAsFloat(column)
        currentContent.put(column, value)
        return oldval == value
    }

    fun put(column: String, value: String): Boolean {
        val oldval = getAsString(column)
        currentContent.put(column, value)
        return oldval == value
    }

    fun put(column: String, value: Double): Boolean {
        val oldval = getAsDouble(column)
        currentContent.put(column, value)
        return oldval == value
    }

    fun put(column: String, date: Date): Boolean {
        val oldval = getAsDate(column)
        currentContent.put(column, MyConverter.toString(date))
        return oldval == date
    }

    /**
     * Aendert oder Fuegt Daten ein.
     *
     * @param column name der Spalte, die eingefuegt werden soll.
     * @param value  BlobWert, der eingefuegt werden soll.
     */
    fun put(column: String, value: ByteArray) {
        currentContent.put(column, value)
    }

    fun putNull(column: String) {
        currentContent.putNull(column)
    }

    /**
     * @see Object.toString
     */
    override fun toString(): String {
        return javaClass.simpleName + "Werte: " + currentContent + App.linefeed
    }

    open suspend fun update() {
        throw IllegalStateException("Update not implented")
    }

}