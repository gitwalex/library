package com.gerwalex.lib.main;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Build;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.gerwalex.lib.R;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.net.ssl.HttpsURLConnection;

public class Utils {

    private final static int BUFFERSIZE = 8192;

    /**
     * @param target     Zielarchiv
     * @param filesToZip Liste der Files, die dem archiv hinzugefuegt werden sollen.
     * @throws IllegalArgumentException wenn target ein Directory ist.
     */
    public static void addToZip(@NonNull File target, @NonNull File... filesToZip) throws IOException {
        File dir = target.getAbsoluteFile();
        if (target.isDirectory()) {
            throw new IllegalArgumentException("Target darf kein Directory sein");
        }
        ZipOutputStream zos = null;
        try {
            FileOutputStream fout = new FileOutputStream(target);
            zos = new ZipOutputStream(new BufferedOutputStream(fout));
            addZipFiles(zos, filesToZip);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (zos != null) {
                zos.close();
            }
        }
    }

    /**
     * Fuegt dem ZipOutputStream alle Files der Filelist hinzu.
     *
     * @param out      ZipOutputStream
     * @param fileList Liste der Files. Ist ein File ein Directory, werden alle Fileses dieses Directoreis
     *                 rekursiv hnzugefuegt.
     */
    private static void addZipFiles(@NonNull ZipOutputStream out, @NonNull File... fileList) throws IOException {
        BufferedInputStream in;
        byte[] data = new byte[BUFFERSIZE];
        for (File file : fileList) {
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                addZipFiles(out, files);
            } else {
                in = new BufferedInputStream(new FileInputStream(file.getPath()), BUFFERSIZE);
                ZipEntry entry = new ZipEntry(file.getName());
                entry.setTime(file.lastModified()); // to keep modification time
                // after unzipping
                out.putNextEntry(entry);
                int length;
                while ((length = in.read(data)) > 0) {
                    out.write(data, 0, length);
                }
                in.close();
            }
        }
    }

    /**
     *
     */
    public static boolean between(int i, int minValueInclusive, int maxValueInclusive) {
        return (i >= minValueInclusive && i <= maxValueInclusive);
    }

    /**
     * Kopiert ein File. Die Pruefung, ob das Zielfile z.B. wegen Berechtigungen erstellt werden
     * kann obliegt der aufrufenden Klasse.
     *
     * @param src  File, welches kopiert werden soll
     * @param dest TargetFile
     * @throws IOException Bei Fehlern.
     */
    public static void copy(File src, File dest) throws IOException {
        try (FileChannel inChannel = new FileInputStream(src).getChannel();
             FileChannel outChannel = new FileOutputStream(dest).getChannel()) {
            inChannel.transferTo(0, inChannel.size(), outChannel);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void flipCard(View visibleView, View invisibleView) {
        Context context = visibleView.getContext();
        visibleView.setVisibility(View.VISIBLE);
        Animator flipOutAnimatorSet = AnimatorInflater.loadAnimator(context, R.animator.flip_out);
        flipOutAnimatorSet.setTarget(invisibleView);
        Animator flipInAnimatorset = AnimatorInflater.loadAnimator(context, R.animator.flip_in);
        flipInAnimatorset.setTarget(visibleView);
        flipInAnimatorset.start();
        flipOutAnimatorSet.start();
        flipInAnimatorset.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                invisibleView.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }

            @Override
            public void onAnimationStart(Animator animation) {
            }
        });
    }

    /**
     * Erstellt ein Calendarobjekt mit folgenden Eigenschaften: HOUR_OF_DAY = Anzahl der vollen
     * Stunden aus minutesAfterMidnight MINUTE = Anzahl der Minuten nach Abzug der vollen Stunden
     * aus minutesAfterMidnight SECOND = 0. Liegt der ermittelte Calendar vor der aktuellen Zeit wird ein Tag
     * aufaddiert.
     *
     * @param minutesAfterMidnight Anzahl der Minuten nach Mitternacht
     */
    public static Calendar getCalendar(int minutesAfterMidnight) {
        Calendar cal = GregorianCalendar.getInstance();
        int hour = minutesAfterMidnight / 60;
        int minute = minutesAfterMidnight % 60;
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, 0);
        if (cal.getTimeInMillis() < System.currentTimeMillis()) {
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }
        return cal;
    }

    @SuppressLint("MissingPermission")
    public static ConnectionType getConnectionType(Context context) {
        ConnectionType result = ConnectionType.NONE; // Returns connection type. 0: none; 1: mobile data; 2: wifi
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            int type = cm.getActiveNetworkInfo().getType();
            if (type == ConnectivityManager.TYPE_WIFI) {
                result = ConnectionType.WiFi;
            } else if (type == ConnectivityManager.TYPE_MOBILE) {
                result = ConnectionType.Mobile;
            } else if (type == ConnectivityManager.TYPE_VPN) {
                result = ConnectionType.VPN;
            }
        } else {
            NetworkCapabilities nw = cm.getNetworkCapabilities(cm.getActiveNetwork());
            if (nw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                result = ConnectionType.WiFi;
            } else if (nw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                result = ConnectionType.Mobile;
            } else if (nw.hasTransport(NetworkCapabilities.TRANSPORT_VPN)) {
                result = ConnectionType.VPN;
            }
        }
        return result;
    }

    public static String getCurrentTimeStamp() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault()).format(new Date());
    }

    /**
     * Prueft, ob Internetverbindung vorhanden ist. Ist keine vorhanden, wird ein Dialog gezeigt.
     *
     * @param context Context fuer Dialog
     * @return true, wenn irgendeine Internetverbindung vorhanden ist.
     */
    public static boolean hasInternetConnection(Context context) {
        ConnectionType type = getConnectionType(context);
        if (type == ConnectionType.NONE) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(R.string.NoInternetConnection);
            builder.setMessage(R.string.NoInternetConnectionMsg);
            builder.setPositiveButton(R.string.btnOK, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            Dialog dlg = builder.create();
            dlg.show();
        }
        return false;
    }

    /**
     * Laedt eine Class anhand des Namens und Typs
     *
     * @param className name der Class
     * @param type      Typ der Klasse. Kann auch Object oder ein Interface sein
     * @return Klasse, die den Parametern entspricht.
     */
    public static <T> T instantiate(@NonNull final String className, @NonNull final Class<T> type) {
        try {
            return type.cast(Class.forName(className).newInstance());
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Laedt ein CSV in eine Tabelle.
     *
     * @param in        Eingabe
     * @param tablename Tabellenname
     * @param database  database
     */
    public static void loadCSVFile(InputStream in, SupportSQLiteDatabase database, String tablename) {
        ContentValues cv = new ContentValues();
        try {
            Log.d("gerwalex", "Lade Tabelle " + tablename);
            BufferedReader buffer = new BufferedReader(new InputStreamReader(in));
            String[] colnames = buffer.readLine().split(";");
            String line;
            while ((line = buffer.readLine()) != null) {
                String[] csvcolumns = line.split(";");
                for (int i = 0; i < csvcolumns.length; i++) {
                    if (csvcolumns[i] != null) {
                        csvcolumns[i] = csvcolumns[i].trim();
                        cv.put(colnames[i], csvcolumns[i]);
                    }
                }
                database.insert(tablename, SQLiteDatabase.CONFLICT_NONE, cv);
                cv.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("gerwalex", "Werte: " + cv);
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Liest ein csv-File in ContentValues ein. In der ersten Zeile muessen die Spaltennamen
     * vermerkt sein. Zeilen beginnend mit '--' werden ueberlesen.
     */
    public static List<ContentValues> loadCSVFile(InputStream in) throws IOException {
        List<ContentValues> list = new ArrayList<>();
        BufferedReader buffer = new BufferedReader(new InputStreamReader(in));
        String[] colnames = buffer.readLine().split(";");
        String line;
        while ((line = buffer.readLine()) != null) {
            if (!line.startsWith("--")) {
                String[] csvcolumns = line.split(";");
                ContentValues cv = new ContentValues();
                for (int i = 0; i < csvcolumns.length; i++) {
                    if (csvcolumns[i] != null) {
                        csvcolumns[i] = csvcolumns[i].trim();
                        cv.put(colnames[i], csvcolumns[i]);
                    }
                }
                list.add(cv);
            }
        }
        return list;
    }

    /**
     * Fuehrt einen HTTP-request durch
     *
     * @param url    URL
     * @param method Requestmethod: POST oder GET
     * @param params parameter, wie sie fuer den Request benoetigt werden
     */
    public static List<String> makeHttpRequest(@NonNull String url, @NonNull String method,
                                               @Nullable HashMap<String, String> params) throws IOException {
        List<String> result = new ArrayList<>();
        StringBuilder sbParams = new StringBuilder();
        int i = 0;
        String charset = "UTF-8";
        if (params != null) {
            for (String key : params.keySet()) {
                try {
                    if (i != 0) {
                        sbParams.append("&");
                    }
                    sbParams.append(key).append("=").append(URLEncoder.encode(params.get(key), charset));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                i++;
            }
        }
        HttpURLConnection conn = null;
        switch (method) {
            case "POST":
                URL urlObj = new URL(url);
                conn = (HttpsURLConnection) urlObj.openConnection();
                conn.setDoOutput(true);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Accept-Charset", charset);
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.connect();
                String paramsString = sbParams.toString();
                DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
                wr.writeBytes(paramsString);
                wr.flush();
                wr.close();
                break;
            case "GET":
                // request method is GET
                if (sbParams.length() != 0) {
                    url += "?" + sbParams;
                }
                urlObj = new URL(url);
                conn = (HttpsURLConnection) urlObj.openConnection();
                conn.setDoOutput(false);
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Accept-Charset", charset);
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.connect();
                break;
            default:
                throw new IllegalArgumentException("Method nicht bekannt (Nur POST ooder GET moeglich " + method);
        }
        //Receive the response from the server
        InputStream in = new BufferedInputStream(conn.getInputStream());
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String line;
        while ((line = reader.readLine()) != null) {
            result.add(line);
        }
        conn.disconnect();
        return result;
    }

    public static void send(Context context, CharSequence text) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, text);
        context.startActivity(Intent.createChooser(intent, "Share stacktrace using"));
    }

    public static void send(Context context, CharSequence title, CharSequence text) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, text);
        context.startActivity(Intent.createChooser(intent, title));
    }

    public static ResultCode unzip(File zipedFile, File directory) throws IOException {
        if (!directory.exists() && !directory.mkdirs()) {
            return ResultCode.FileError;
        }
        if (directory.isDirectory()) {
            try {
                byte[] data = new byte[BUFFERSIZE];
                ZipInputStream in = new ZipInputStream(new FileInputStream(zipedFile));
                ZipEntry entry = null;
                while ((entry = in.getNextEntry()) != null) {
                    //create dir if required while unzipping
                    File f = new File(directory, entry.getName());
                    if (entry.isDirectory() && !f.isDirectory() && !f.mkdirs()) {
                        return ResultCode.FileError;
                    } else {
                        BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(f), BUFFERSIZE);
                        int length;
                        while ((length = in.read(data)) > 0) {
                            out.write(data, 0, length);
                        }
                        //noinspection ResultOfMethodCallIgnored
                        f.setLastModified(entry.getTime());
                        in.closeEntry();
                        out.close();
                    }
                }
                in.close();
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        } else {
            throw new IllegalArgumentException("Target must be Directory");
        }
        return ResultCode.UNZIPFileOK;
    }

    /**
     * Schribt eine Liste mit Strings in ein file
     *
     * @return true, wenn erfolgreich
     */
    public static boolean writeFile(File file, List<String> content) {
        try {
            FileOutputStream out = new FileOutputStream(file);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(out));
            for (String s : content) {
                if (s != null) {
                    bw.write(s);
                    bw.newLine();
                }
            }
            bw.flush();
            bw.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public enum ConnectionType {
        NONE, Mobile, WiFi, VPN
    }

    public enum ResultCode {
        /**
         * Alles OK
         */
        RESULT_OK,
        /**
         *
         */
        UNZIPFileOK,
        /**
         *
         */
        ZIPFileCreateOK,
        /**
         *
         */
        FileError,
        /**
         *
         */
        FileNotFound,
        /**
         *
         */
        keineBerechtigung,
        /**
         *
         */
        Sonstige
    }
}
