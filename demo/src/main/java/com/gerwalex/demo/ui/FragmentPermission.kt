package com.gerwalex.demo.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gerwalex.demo.R
import com.gerwalex.demo.databinding.FragmentPermissionsBinding
import com.gerwalex.lib.main.BasicFragment
import com.gerwalex.lib.permissions.PermissionState
import com.gerwalex.lib.permissions.PermissionUtil.registerforPermissionRequest
import com.google.android.material.snackbar.Snackbar

class FragmentPermission : BasicFragment() {

    private lateinit var binding: FragmentPermissionsBinding
    private val cameraPermission = registerforPermissionRequest()
    private val storagePermission = registerforPermissionRequest()
    private fun onStoragePermissionResult(state: PermissionState) {
        when (state) {
            PermissionState.Denied -> {
                Snackbar
                    .make(requireView(), R.string.permission_denied, Snackbar.LENGTH_LONG)
                    .show()
            }
            PermissionState.Granted -> {
                Snackbar
                    .make(requireView(), R.string.permission_granted, Snackbar.LENGTH_LONG)
                    .show()
            }
            PermissionState.PermanentlyDenied -> {
                Snackbar
                    .make(requireView(), R.string.permission_permamently_denied, Snackbar.LENGTH_LONG)
                    .show()
            }
        }
    }

    private fun onCameraPermissionResult(state: PermissionState) {
        when (state) {
            PermissionState.Denied -> {
                Snackbar
                    .make(requireView(), R.string.permission_denied, Snackbar.LENGTH_LONG)
                    .show()
            }
            PermissionState.Granted -> {
                Snackbar
                    .make(requireView(), R.string.permission_granted, Snackbar.LENGTH_LONG)
                    .show()
            }
            PermissionState.PermanentlyDenied -> {
                Snackbar
                    .make(requireView(), R.string.permission_permamently_denied, Snackbar.LENGTH_LONG)
                    .show()
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentPermissionsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnCameraPermission.setOnClickListener {
            cameraPermission.launchSinglePermission(android.Manifest.permission.CAMERA) {
                onCameraPermissionResult(it)
            }
        }
        binding.btnStoragePermission.setOnClickListener {
            storagePermission.launchMultiplePermission(
                arrayOf(
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                )
            ) {
                onStoragePermissionResult(it)
            }
        }
    }
}
