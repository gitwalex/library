package com.gerwalex.demo.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.gerwalex.demo.R;
import com.gerwalex.demo.databinding.FragmentFireworkBinding;
import com.gerwalex.lib.main.BasicFragment;
import com.plattysoft.leonids.ParticleSystem;

public class FragmentFirework extends BasicFragment {

    private FragmentFireworkBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentFireworkBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.fireworkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParticleSystem ps1 = new ParticleSystem(requireActivity(), 80, R.drawable.confeti2, 10000);
                ps1.setSpeedModuleAndAngleRange(0f, 0.3f, 0, 360)
                  .setRotationSpeed(60)
                  .setAcceleration(0.00005f, 90)
                  .emit(binding.fireworkBtn, 15);
                ParticleSystem ps2 = new ParticleSystem(requireActivity(), 80, R.drawable.confeti3, 10000);
                ps2.setSpeedModuleAndAngleRange(0f, 0.3f, 0, 360)
                  .setRotationSpeed(60)
                  .setAcceleration(0.00005f, 90)
                  .emit(binding.fireworkBtn, 15);
            }
        });
    }
}
