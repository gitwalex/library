package com.gerwalex.lib.main;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gerwalex.lib.BR;
import com.gerwalex.lib.R;
import com.gerwalex.lib.adapters.SortedItemListAdapter;
import com.gerwalex.lib.adapters.ViewHolder;
import com.gerwalex.lib.databinding.LinearRecyclerviewBinding;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * FileChooser fuer Dateien. Ermittelt vor Anzeige die Berechtigung, wenn erforderlich.
 */
public class FragmentFileChooser extends BasicFragment {
    public static final String FILE = "FILE";
    public static final String FILEDIRECTORY = "FILEDIRECTORY";
    private LinearRecyclerviewBinding binding;
    private FileListAdapter mAdapter;
    private File mDirectoy;
    private final ActivityResultLauncher<String> readExternalPermissionContract =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(),
                    new ActivityResultCallback<Boolean>() {
                        @Override
                        public void onActivityResult(Boolean result) {
                            if (result) {
                                mAdapter.replace(createFileList(mDirectoy));
                            } else {
                                Snackbar sc = Snackbar.make(requireView(), R.string.read_external_storage_denied,
                                        Snackbar.LENGTH_INDEFINITE);
                                sc.setAction(getString(R.string.btnOK), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        requireActivity().setResult(Activity.RESULT_CANCELED);
                                    }
                                });
                                sc.show();
                            }
                        }
                    });

    public static FragmentFileChooser newInstance(String path) {
        File file = new File(path);
        if (!file.isDirectory()) {
            throw new IllegalArgumentException("File must be Directory");
        }
        Bundle args = new Bundle();
        args.putString(FILEDIRECTORY, path);
        FragmentFileChooser fragment = new FragmentFileChooser();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Erstellt eine Liste der Files innerhalb eines Directories. Ist das File ungleich dem in
     * angegebenen Directory, wird am Anfang der Liste der Parent des uebergebenen Files eingefuegt.
     * Damit kann eine Navigation erfolgen.
     * <p>
     * Die erstellte Liste wird direkt in den ExcludedCatsAdapter einestellt.
     * <p>
     * Ausserdem wird im Subtitle der Toolbar der Name des akuellten Verzeichnisses eingeblendet.
     *
     * @param file File, zu dem die Liste erstellt werden soll
     */
    private List<File> createFileList(File file) {
        File[] files = file.listFiles();
        List<File> mFiles = null;
        if (files != null) {
            mFiles = Arrays.asList(files);
            setTitle(file.getAbsolutePath());
        }
        return mFiles;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDirectoy = new File(requireArguments().getString(FILEDIRECTORY));
        if (!mDirectoy.isDirectory()) {
            throw new IllegalArgumentException("File must be Directory");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = LinearRecyclerviewBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mAdapter = new FileListAdapter(getContext());
        RecyclerView rc = binding.defaultRecyclerView;
        rc.setAdapter(mAdapter);
        rc.setLayoutManager(new LinearLayoutManager(getContext()));
        readExternalPermissionContract.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE);
    }

    public static class FileWrapper extends BaseObservable {
        private final File mFile;
        private final String size;

        FileWrapper(Context context, File file) {
            mFile = file;
            size = Formatter.formatFileSize(context, file.length());
        }

        @Bindable
        public String getFilename() {
            return mFile.getName();
        }

        @Bindable
        public String getSize() {
            return size;
        }

        public boolean isDirectory() {
            return mFile.isDirectory();
        }
    }

    public class FileListAdapter extends SortedItemListAdapter<File>
            implements View.OnClickListener, View.OnLongClickListener {
        public FileListAdapter(Context context) {
            super(File.class);
        }

        @Override
        protected boolean areContentsTheSame(File item, File other) {
            return false;
        }

        @Override
        protected boolean areItemsTheSame(File item, File other) {
            return item.getAbsolutePath().equals(other.getAbsolutePath());
        }

        @Override
        protected int compare(File item, File other) {
            if (item.isDirectory() && !other.isDirectory()) {
                // Directory before File
                return -1;
            } else if (!item.isDirectory() && other.isDirectory()) {
                // File after directory
                return 1;
            } else {
                // Otherwise in Alphabetic order...
                return item.getName().compareTo(other.getName());
            }
        }

        /**
         * Prueft, ob an Position 0 eine View eingefuegt werden muss, damit in das uebergeordnete
         * Verzeichnis gewechselt werden kann. Ansonsten wird der Default zuruckgeliefert.
         */
        @Override
        public int getItemViewType(int position) {
            return R.layout.filechooser_list_items;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, File file, int position) {
            FileWrapper f = new FileWrapper(getContext(), file);
            holder.setVariable(BR.file, f);
        }

        @Override
        public void onClick(View v) {
            File file = getItemAt(getPosition(v));
            if (!file.isDirectory()) {
                Intent intent = new Intent();
                intent.putExtra(FILE, file.getAbsolutePath());
                requireActivity().setResult(Activity.RESULT_OK, intent);
            }
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int itemType) {
            ViewHolder holder = super.onCreateViewHolder(viewGroup, itemType);
            holder.itemView.setOnClickListener(this);
            holder.itemView.setOnLongClickListener(this);
            return holder;
        }

        /**
         * Wird ein Dateieintrag lang ausgewaehlt, wird ein Loeschen-Dialog angeboten.
         */
        @Override
        public boolean onLongClick(View v) {
            File file = getItemAt(getPosition(v));
            if (!file.isDirectory()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setPositiveButton(R.string.btnOK, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        File parent = file.getParentFile();
                        file.delete();
                        mAdapter.replace(createFileList(parent));
                    }
                });
                builder.setTitle(R.string.deleteFile);
                builder.setIcon(android.R.drawable.ic_dialog_alert);
                Dialog dlg = builder.create();
                dlg.show();
                return true;
            }
            return false;
        }
    }
}

