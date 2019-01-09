package com.example.st.google_calendar

import android.Manifest
import android.accounts.AccountManager
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.Toast
import com.google.android.gms.common.GoogleApiAvailability
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
import com.google.api.client.http.HttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.ExponentialBackOff
import com.google.api.services.calendar.CalendarScopes
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_main.*
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import pub.devrel.easypermissions.EasyPermissions.hasPermissions
import java.util.*

private const val REQUEST_PERMISSION_GET_ACCOUNTS = 1001
private const val REQUEST_CODE_PLAY_SERVICE = 1002
private const val REQUEST_ACCOUNT_PICKER = 1003
private const val REQUEST_AUTHORIZATION = 1004
private const val RC_AUTH_PERMISSION = 1005

class MainActivity : AppCompatActivity() {

    private lateinit var googleAccountCredential: GoogleAccountCredential
    private lateinit var compositeDisposable: CompositeDisposable
    private lateinit var googleCalendarRepository: CalendarRepository
    private lateinit var calendarDataService: CalendarDataService

    private val REQUEST_ACCOUNT: String = "accountName"
    private var calendarId: String = "skaehdwn1014@gmail.com"
    private var test: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        compositeDisposable = CompositeDisposable()
        googleAccountCredential = GoogleAccountCredential.usingOAuth2(
                applicationContext, Arrays.asList(CalendarScopes.CALENDAR)
        ).setBackOff(ExponentialBackOff())

        initCalendarDataService()
        googleCalendarRepository = CalendarRepository(calendarDataService)


        button_oath.setOnClickListener{
            isGooglePlayServiceAvailable()
        }

        button_calendar.setOnClickListener{
            if(test)
             CalendarList()
        }

    }

    fun initCalendarDataService() {
        val httptransport: HttpTransport = AndroidHttp.newCompatibleTransport()
        val jsonFactory: JacksonFactory = JacksonFactory.getDefaultInstance()
        calendarDataService = CalendarDataService(httptransport, jsonFactory, googleAccountCredential)

    }

    private fun CalendarList() {
            googleCalendarRepository.getCalendarList()
                    .observeOn(AndroidSchedulers.mainThread())
                    .map { it.items }
                    .subscribe({
                        it.forEach { item ->
                            val button = Button(this)
                            text_field.text = item.summary
                            button_calendar.setOnClickListener{
                                getEventList(item.id)
                            }
                        }
                    }, {
                        when (it) {
                            is UserRecoverableAuthIOException -> startActivityForResult(it.intent, RC_AUTH_PERMISSION)
                            else -> it.printStackTrace()
                        }
                    }).apply { compositeDisposable.add(this) }

    }

    private fun getEventList(calendarId: String){
        if(isGooglePlayServiceAvailable()){
            googleCalendarRepository.getEventList(calendarId)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        text_field.text = it.fold("") { acc, event ->
                            acc + "날짜=${event.start.date}" + " "+ " 제목=${event.summary}\n"
                        }
                    }, {
                        when (it) {
                            is UserRecoverableAuthIOException -> startActivityForResult(it.intent, RC_AUTH_PERMISSION)
                            else -> it.printStackTrace()
                        }
                    })
                    .apply {
                        compositeDisposable.add(this)
                    }
        }
    }


    private fun isGooglePlayServiceAvailable(): Boolean {
        googleAccountCredential.selectedAccountName?.let {
            return true
        }.let {
            test = false
            chooseAccount()
            return false
        }
    }

    private fun getResultFromApi() {
        if (!isGooglePlayServiceAvailable()) {
            acquireGooglePlayServices()
        } else {
            googleAccountCredential.selectedAccountName?.let {
                CalendarList()
            }.let {
            }
        }
    }

    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    private fun chooseAccount() {
        if (hasPermissions(this, Manifest.permission.GET_ACCOUNTS)) {

            val accountName: String? = getPreferences(Context.MODE_PRIVATE).getString(REQUEST_ACCOUNT, null)
            accountName?.let {
                googleAccountCredential.selectedAccountName = accountName
                CalendarList()
            }.let {
                startActivityForResult(googleAccountCredential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);
            }

        } else {
            EasyPermissions.requestPermissions(
                    this,
                    "구글 계정 권한이 필요합니다.",
                    REQUEST_PERMISSION_GET_ACCOUNTS,
                    Manifest.permission.GET_ACCOUNTS
            )
        }
    }


    private fun acquireGooglePlayServices() {
        val availability: GoogleApiAvailability = GoogleApiAvailability.getInstance()
        val state = availability.isGooglePlayServicesAvailable(this)

        if (availability.isUserResolvableError(state)) {
            showErrorDialog(state);
        }

    }

    private fun showErrorDialog(state: Int) {
        val availability: GoogleApiAvailability = GoogleApiAvailability.getInstance()
        val dialog: Dialog = (availability.getErrorDialog(this, state, REQUEST_CODE_PLAY_SERVICE))
        dialog.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        val accountName = AccountManager.KEY_ACCOUNT_NAME

        when (requestCode) {
            REQUEST_CODE_PLAY_SERVICE -> {
                if (resultCode == Activity.RESULT_OK) {
                    text_field.text = "구글 플레이 서비스를 설치 해주세요."
                } else
                    getResultFromApi()
            }
            REQUEST_ACCOUNT_PICKER -> {
                if (resultCode == Activity.RESULT_OK) {
                    val accountName: String = data!!.getStringExtra(accountName)
                    accountName?.let {

                        val setting = getPreferences(Context.MODE_PRIVATE)
                                setting.edit().putString(REQUEST_ACCOUNT, accountName).apply {
                                    googleAccountCredential.setSelectedAccountName(accountName)?.let {
                                        getResultFromApi()
                                        apply()
                                    }
                                }
                    }
                }
            }
            REQUEST_AUTHORIZATION -> {
                if (requestCode == Activity.RESULT_OK)
                    getResultFromApi()
            }
            RC_AUTH_PERMISSION -> {
                Toast.makeText(this,"구글 인증이 필요합니다.",Toast.LENGTH_SHORT).show()
                getResultFromApi()
            }

        }
    }
}