package com.gerwalex.demo.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gerwalex.demo.R
import com.gerwalex.demo.databinding.FragmentPermissionsBinding
import com.gerwalex.lib.main.BasicFragment
import com.gerwalex.lib.permissions.Permission
import com.gerwalex.lib.permissions.launchMultiplePermission
import com.gerwalex.lib.permissions.launchSinglePermission
import com.gerwalex.lib.permissions.registerPermission
import com.google.android.material.snackbar.Snackbar

class FragmentPermission : BasicFragment() {

    private lateinit var binding: FragmentPermissionsBinding
    private val cameraPermission = registerPermission {
        onCameraPermissionResult(it)
    }
    private val storagePermission = registerPermission {
        onStoragePermissionResult(it)
    }
    private fun onStoragePermissionResult(state: Permission.PermissionState) {
        when (state) {
            Permission.PermissionState.Denied -> {
                Snackbar
                    .make(requireView(), R.string.permission_denied, Snackbar.LENGTH_LONG)
                    .show()
            }
            Permission.PermissionState.Granted -> {
                Snackbar
                    .make(requireView(), R.string.permission_granted, Snackbar.LENGTH_LONG)
                    .show()
            }
            Permission.PermissionState.PermanentlyDenied -> {
                Snackbar
                    .make(requireView(), R.string.permission_permamently_denied, Snackbar.LENGTH_LONG)
                    .show()
            }
        }
    }

    private fun onCameraPermissionResult(state: Permission.PermissionState) {
        when (state) {
            Permission.PermissionState.Denied -> {
                Snackbar
                    .make(requireView(), R.string.permission_denied, Snackbar.LENGTH_LONG)
                    .show()
            }
            Permission.PermissionState.Granted -> {
                Snackbar
                    .make(requireView(), R.string.permission_granted, Snackbar.LENGTH_LONG)
                    .show()
            }
            Permission.PermissionState.PermanentlyDenied -> {
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
            cameraPermission.launchSinglePermission(android.Manifest.permission.CAMERA)
        }
        binding.btnStoragePermission.setOnClickListener {
            storagePermission.launchMultiplePermission(
                arrayOf(
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                )
            )
        }
    }
}
