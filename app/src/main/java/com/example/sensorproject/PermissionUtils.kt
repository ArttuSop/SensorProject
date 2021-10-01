package com.example.sensorproject

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.DialogFragment


object PermissionUtils {

    fun requestPermission(
        activity: MapsFragment, requestId: Int,
        permission: String, finishActivity: Boolean
    ) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity.requireActivity(), permission)) {
            // Display a dialog with rationale.
            RationaleDialog.newInstance(requestId, finishActivity)
                .show(activity.childFragmentManager, "dialog")
        } else {
            // Location permission has not been granted yet, request it.
            ActivityCompat.requestPermissions(activity.requireActivity(), arrayOf(permission), requestId)
        }
    }

    fun isPermissionGranted(
        grantPermissions: Array<String>, grantResults: IntArray,
        permission: String
    ): Boolean {
        for (i in grantPermissions.indices) {
            if (permission == grantPermissions[i]) {
                return grantResults[i] == PackageManager.PERMISSION_GRANTED
            }
        }
        return false
    }


    class PermissionDeniedDialog : DialogFragment() {
        private var finishActivity = false
        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            finishActivity = requireArguments().getBoolean(ARGUMENT_FINISH_ACTIVITY)
            return AlertDialog.Builder(activity)
                .setMessage(R.string.location_permission_denied)
                .setPositiveButton(android.R.string.ok, null)
                .create()
        }

        override fun onDismiss(dialog: DialogInterface) {
            super.onDismiss(dialog)
            if (finishActivity) {
                Toast.makeText(
                    activity, R.string.permission_required_toast,
                    Toast.LENGTH_SHORT
                ).show()
                requireActivity().finish()
            }
        }

        companion object {
            private const val ARGUMENT_FINISH_ACTIVITY = "finish"

            fun newInstance(finishActivity: Boolean): PermissionDeniedDialog {
                val arguments = Bundle()
                arguments.putBoolean(ARGUMENT_FINISH_ACTIVITY, finishActivity)
                val dialog = PermissionDeniedDialog()
                dialog.arguments = arguments
                return dialog
            }
        }
    }

    class RationaleDialog : DialogFragment() {
        private var finishActivity = false
        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val arguments = arguments
            val requestCode = arguments!!.getInt(ARGUMENT_PERMISSION_REQUEST_CODE)
            finishActivity = arguments.getBoolean(ARGUMENT_FINISH_ACTIVITY)
            return AlertDialog.Builder(activity)
                .setMessage(R.string.permission_rationale_location)
                .setPositiveButton(android.R.string.ok,
                    DialogInterface.OnClickListener { dialog, which -> // After click on Ok, request the permission.
                        ActivityCompat.requestPermissions(
                            requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                            requestCode
                        )
                        // Do not finish the Activity while requesting permission.
                        finishActivity = false
                    })
                .setNegativeButton(android.R.string.cancel, null)
                .create()
        }

        override fun onDismiss(dialog: DialogInterface) {
            super.onDismiss(dialog)
            if (finishActivity) {
                Toast.makeText(
                    activity,
                    R.string.permission_required_toast,
                    Toast.LENGTH_SHORT
                )
                    .show()
                requireActivity().finish()
            }
        }

        companion object {
            private const val ARGUMENT_PERMISSION_REQUEST_CODE = "requestCode"
            private const val ARGUMENT_FINISH_ACTIVITY = "finish"


            fun newInstance(requestCode: Int, finishActivity: Boolean): RationaleDialog {
                val arguments = Bundle()
                arguments.putInt(ARGUMENT_PERMISSION_REQUEST_CODE, requestCode)
                arguments.putBoolean(ARGUMENT_FINISH_ACTIVITY, finishActivity)
                val dialog = RationaleDialog()
                dialog.arguments = arguments
                return dialog
            }
        }
    }
}