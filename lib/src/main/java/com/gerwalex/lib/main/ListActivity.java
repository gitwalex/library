package com.gerwalex.lib.main;

import static com.gerwalex.lib.main.FragmentFileChooser.FILEDIRECTORY;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.gerwalex.lib.databinding.ListActivityBinding;

public class ListActivity extends AppCompatActivity {
    private ListActivityBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ListActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        try {
            Action action = Action.valueOf(getIntent().getAction());
            if (action == Action.ShowFiles) {
                String path = getIntent().getExtras().getString(FILEDIRECTORY);
                ft.add(FragmentFileChooser.newInstance(path), null);
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } finally {
            ft.commit();
        }
    }

    public enum Action {
        ShowFiles
    }
}
