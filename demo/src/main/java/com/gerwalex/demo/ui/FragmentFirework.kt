package com.gerwalex.demo.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gerwalex.demo.R
import com.gerwalex.demo.databinding.FragmentFireworkBinding
import com.gerwalex.lib.main.BasicFragment
import com.plattysoft.leonids.ParticleSystem

class FragmentFirework : BasicFragment() {

    private lateinit var binding: FragmentFireworkBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentFireworkBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.fireworkBtn.setOnClickListener(
            View.OnClickListener {
                val ps1 = ParticleSystem(requireActivity(), 80, R.drawable.confeti2, 10000)
                ps1
                    .setSpeedModuleAndAngleRange(0f, 0.3f, 90, 270)
                    .setRotationSpeed(60f)
                    .setFadeOut(3000)
                    .setAcceleration(0.00005f, 90)
                    .emit(binding.fireworkBtn, 15)
                val ps2 = ParticleSystem(requireActivity(), 80, R.drawable.confeti3, 10000)
                ps2
                    .setSpeedModuleAndAngleRange(0f, 0.3f, 0, 360)
                    .setRotationSpeed(60f)
                    .setAcceleration(0.00005f, 90)
                    .emit(binding.fireworkBtn, 15)
            })
    }
}
