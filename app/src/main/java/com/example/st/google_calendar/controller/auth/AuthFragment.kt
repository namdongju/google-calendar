package com.example.st.google_calendar.controller.auth


import android.Manifest
import android.accounts.AccountManager
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController

import javax.inject.Inject
import dagger.android.support.DaggerFragment
import com.example.st.google_calendar.R
import kotlinx.android.synthetic.main.fragment_auth.*
import pub.devrel.easypermissions.EasyPermissions
import pub.devrel.easypermissions.AfterPermissionGranted
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential

private const val RC_ACCOUNT_PICKER = 1001
private const val RP_GET_ACCOUNTS = 2001


class AuthFragment : DaggerFragment() {

    @Inject
    lateinit var googleAccountCredential: GoogleAccountCredential

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_auth, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        button_auth.setOnClickListener { selectAccount() }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_ACCOUNT_PICKER && resultCode == Activity.RESULT_OK) {
            data?.let {
                val accountName = it.getStringExtra(AccountManager.KEY_ACCOUNT_NAME)
                accountName?.let {
                    googleAccountCredential.selectedAccountName = it
                    moveToCalendarSelectFragment()
                } ?: run {
                    Toast.makeText(requireContext(), "선택된 계정이 없습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    @AfterPermissionGranted(RP_GET_ACCOUNTS)
    private fun selectAccount() {
        if (EasyPermissions.hasPermissions(requireContext(), Manifest.permission.GET_ACCOUNTS)) {
            startActivityForResult(googleAccountCredential.newChooseAccountIntent(), RC_ACCOUNT_PICKER)
        } else {
            EasyPermissions.requestPermissions(
                    this,
                    "구글 계정 권한이 필요합니다.",
                    RP_GET_ACCOUNTS,
                    android.Manifest.permission.GET_ACCOUNTS
            )
        }
    }

    private fun moveToCalendarSelectFragment() {
        AuthFragmentDirections.actionDestAuthToDestCalendarSelect().apply {
            findNavController().navigate(this)
        }
    }
}