package com.gerwalex.lib.database;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.RoomDatabase;

import com.gerwalex.lib.R;
import com.gerwalex.lib.main.Utils;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public abstract class DB extends RoomDatabase {

    public static final String NEXT_DB_SAVE = "NEXT_DB_SAVE";
    public static final long NODBSAVE = -1;

    public synchronized Utils.ResultCode executeRestoreDBJob(final AppCompatActivity activity, File file) {
        Utils.ResultCode result = Utils.ResultCode.Sonstige;
        try {
            result = restore(activity, file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(R.string.dbRestore);
        switch (result) {
            default:
                String msg = activity.getString(R.string.dbRestoreFehler, file.getName());
                builder.setMessage(msg);
                builder.setPositiveButton(R.string.btnCancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                break;
            case FileNotFound:
                msg = activity.getString(R.string.dbRestoreFileNotFnd, file.getName());
                builder.setMessage(msg);
                builder.setPositiveButton(R.string.btnCancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                break;
            case FileError:
                msg = activity.getString(R.string.dbRestoreFileError, file.getName());
                builder.setMessage(msg);
                builder.setPositiveButton(R.string.btnCancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                break;
            case UNZIPFileOK:
                msg = activity.getString(R.string.dbRestoreOK, file.getName());
                builder.setMessage(msg);
                builder.setPositiveButton(R.string.btnOK, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(activity, activity.getClass());
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        activity.startActivity(intent);
                    }
                });
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        Intent intent = new Intent(activity, activity.getClass());
                        activity.startActivity(intent);
                    }
                });
                break;
        }
        builder.create()
                .show();
        return result;
    }

    public abstract String getDBName();

    public Utils.ResultCode restore(@NonNull final Context context, File file) throws IOException {
        synchronized (DB.class) {
            close();
            Utils.ResultCode result;
            try {
                result = Utils.unzip(file, Objects.requireNonNull(context.getDatabasePath(getDBName())
                        .getParentFile()));
                //   createNewInstance();
                Log.d("gerwalex", "DB restored: " + file.getName());
            } catch (IOException e) {
                Log.d("gerwalex", "Fehler bei restoreDB: " + e.getMessage());
                e.printStackTrace();
                throw e;
            }
            return result;
        }
    }
}
