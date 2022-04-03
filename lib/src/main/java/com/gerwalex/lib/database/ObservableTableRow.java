package com.gerwalex.lib.database;

import static com.gerwalex.lib.main.App.linefeed;

import android.content.ContentValues;
import android.database.Cursor;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.databinding.BaseObservable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Ignore;

import java.sql.Date;
import java.util.Objects;

public class ObservableTableRow extends BaseObservable {
    @Ignore
    public static final String NAME = "NAME";
    @Ignore
    public static final long NOID = RecyclerView.NO_ID;
    /**
     * Abbild der jeweiligen Zeile der Datenbank. Werden nicht direkt geaendert.
     */
    @Ignore
    private final ContentValues currentContent = new ContentValues();
    @Ignore
    public boolean isInserted;

    /**
     * Konstruktor mit Cursor.
     */
    @Ignore
    public ObservableTableRow(Cursor c) {
        fillContent(c);
        isInserted = containsKey("_id");
    }

    @Ignore
    protected ObservableTableRow() {
    }

    /**
     * Prueft, ob ein Wert geaendert wurde oder bereits in der DB vorhanden ist.
     *
     * @param column Spaltenname
     * @return true, wenn ein Wert ungleich null enthalten ist
     */
    public final boolean containsKey(String column) {
        return currentContent.containsKey(column);
    }

    public void delete() {
        throw new IllegalStateException("Delete not implented");
    }

    /**
     * @param o zu vergleichendes Object.
     * @return true,  wenn das vergleichende Object ein Geschaeftsobjekte ist, beide auf die gleiche
     * Tabelle zeigen (DBDefiniton)und die gleiche Anzahl Werte mit den gleichen Inhalten in den
     * ContentValues vorhanden sind.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ObservableTableRow that = (ObservableTableRow) o;
        return currentContent.equals(that.currentContent);
    }

    /**
     * Fuellt currentContent aus einem Cursor. Cursor darf nicht leer sein. Hat der Cursor mehrere
     * Zeilen, wird nur die erste uebernommen. Es werden nur Werte ungleich null uebernommen.
     * <p/>
     * Alle vorigen Werte werden verworfen.
     *
     * @param c Cursor
     * @throws IllegalArgumentException wenn Cursor leer ist
     */
    public final void fillContent(@NonNull Cursor c) {
        if (c.isBeforeFirst() && !c.moveToFirst()) {
            throw new IllegalArgumentException("Cursor ist leer!");
        }
        for (int i = 0; i < c.getColumnCount(); i++) {
            String colName = c.getColumnName(i);
            int type = c.getType(i);
            switch (type) {
                case Cursor.FIELD_TYPE_BLOB:
                    byte[] blob = c.getBlob(i);
                    currentContent.put(colName, blob);
                    break;
                case Cursor.FIELD_TYPE_FLOAT:
                    currentContent.put(colName, c.getFloat(i));
                    break;
                case Cursor.FIELD_TYPE_INTEGER:
                    currentContent.put(colName, c.getLong(i));
                    break;
                case Cursor.FIELD_TYPE_STRING:
                    currentContent.put(colName, c.getString(i));
                    break;
                case Cursor.FIELD_TYPE_NULL:
                    putNull(colName);
                    break;
                default:
                    String value = c.getString(i);
                    if (value != null) {
                        currentContent.put(colName, value);
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
    public final boolean getAsBoolean(@NonNull String column) {
        return getAsInt(column) != 0;
    }

    public final byte[] getAsByteArray(@NonNull String column) {
        return currentContent.getAsByteArray(column);
    }

    /**
     * Konvertiert ein Datum zurueck.
     *
     * @param column name der Spalte
     * @return Date-Objekt oder null
     */
    public final Date getAsDate(@NonNull String column) {
        return MyConverter.toDate(getAsString(column));
    }

    public final Double getAsDouble(String column) {
        return currentContent.getAsDouble(column);
    }

    /**
     * Liefert den aktuellsten Wert aus dem Geschaeftsobjekt zu Spalte  als Long.
     *
     * @param column name der Spalte
     * @return Den aktuellen Wert der Spalte oder null, wenn nicht vorhanden
     */
    public final Float getAsFloat(@NonNull String column) {
        return currentContent.getAsFloat(column);
    }

    /**
     * Liefert den aktuellsten Wert aus dem Geschaeftsobjekt zu Spalte als Integer.
     *
     * @param column name der Spalte
     * @return Den aktuellen Wert der Spalte oder null, wenn nicht vorhanden
     */
    public final int getAsInt(@NonNull String column) {
        return isNull(column) ? 0 : currentContent.getAsInteger(column);
    }

    public final long getAsLong(@NonNull String column) {
        return isNull(column) ? 0 : currentContent.getAsLong(column);
    }

    /**
     * Prueft, ob column null ist
     *
     * @param column column
     * @return null, wenn column null ist. sonst einen Long-wert
     */
    public final Long getAsLongOrNull(String column) {
        return isNull(column) ? null : getAsLong(column);
    }

    /**
     * Liefert den aktuellsten Wert aus dem Geschaeftsobjekt zu Spalte  als String.
     *
     * @param column name der Spalte
     * @return Den aktuellen Wert der Spalte oder null, wenn nicht vorhanden
     */
    public final String getAsString(@NonNull String column) {
        return currentContent.getAsString(column);
    }

    /**
     * @return Liefert eine Kopie der akteullen  Werte zurueck.
     */
    public final ContentValues getContent() {
        return new ContentValues(currentContent);
    }

    @Override
    public int hashCode() {
        return Objects.hash(currentContent);
    }

    public long insert() {
        throw new IllegalStateException("Insert not implented");
    }

    /**
     * Prueft, ob ein Wert vorhanden ist.
     *
     * @param column Spaltenname
     * @return true, wenn vorhanden. Sonst false
     */
    public final boolean isNull(@NonNull String column) {
        return currentContent.get(column) == null;
    }

    /**
     * Aendert oder Fuegt Daten ein.
     *
     * @param column name der Spalte, die eingefuegt werden soll.
     * @param value  Wert, der eingefuegt werden soll.
     */
    public final boolean put(@NonNull String column, boolean value) {
        return put(column, value ? 1 : 0);
    }

    @CallSuper
    public final boolean put(@NonNull String column, int value) {
        Integer oldval = getAsInt(column);
        currentContent.put(column, value);
        return Objects.equals(oldval, value);
    }

    @CallSuper
    public final boolean put(@NonNull String column, long value) {
        Long oldval = getAsLong(column);
        currentContent.put(column, value);
        return Objects.equals(oldval, value);
    }

    public final boolean put(@NonNull String column, float value) {
        Float oldval = getAsFloat(column);
        currentContent.put(column, value);
        return Objects.equals(oldval, value);
    }

    @CallSuper
    public final boolean put(@NonNull String column, String value) {
        String oldval = getAsString(column);
        currentContent.put(column, value);
        return Objects.equals(oldval, value);
    }

    public final boolean put(@NonNull String column, double value) {
        Double oldval = getAsDouble(column);
        currentContent.put(column, value);
        return Objects.equals(oldval, value);
    }

    public final boolean put(@NonNull String column, Date date) {
        Date oldval = getAsDate(column);
        if (date != null) {
            currentContent.put(column, MyConverter.toString(date));
        } else {
            currentContent.putNull(column);
        }
        return Objects.equals(oldval, date);
    }

    /**
     * Aendert oder Fuegt Daten ein.
     *
     * @param column name der Spalte, die eingefuegt werden soll.
     * @param value  BlobWert, der eingefuegt werden soll.
     */
    public final void put(@NonNull String column, byte[] value) {
        if (value != null) {
            currentContent.put(column, value);
        } else {
            currentContent.putNull(column);
        }
    }

    public final void putNull(@NonNull String column) {
        currentContent.putNull(column);
    }

    /**
     * @see Object#toString()
     */
    @Override
    public String toString() {
        return getClass().getSimpleName() + "Werte: " + currentContent + linefeed;
    }

    public void update() {
        throw new IllegalStateException("Update not implented");
    }
}