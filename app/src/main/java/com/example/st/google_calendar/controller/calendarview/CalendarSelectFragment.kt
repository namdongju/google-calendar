package com.example.st.google_calendar.controller.calendarview


import javax.inject.Inject
import android.os.Bundle
import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import com.example.st.google_calendar.R
import com.example.st.google_calendar.google_calender.GoogleCalendarRepository
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
import com.google.api.services.calendar.model.CalendarListEntry
import dagger.android.support.DaggerFragment
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.fragment_calendar_list.*

const val RC_AUTH_PERMISSION = 1002

class CalendarSelectFragment : DaggerFragment() {

    @Inject
    lateinit var googleCalendarRepository: GoogleCalendarRepository

    private lateinit var compositeDisposable: CompositeDisposable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        compositeDisposable = CompositeDisposable()
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_google_event_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        showCalendarButtons()
    }

    private fun showCalendarButtons() {
        getCalendars().subscribe({
            layout_calendars.removeAllViews()
            addButtons(it)
        }, {
            when (it) {
                is UserRecoverableAuthIOException -> startActivityForResult(it.intent, RC_AUTH_PERMISSION)
                else -> it.printStackTrace()
            }
        }).apply {
            compositeDisposable.add(this)
        }
    }

    private fun getCalendars(): Single<List<CalendarListEntry>> =
            googleCalendarRepository.getCalendarList()
                    .observeOn(AndroidSchedulers.mainThread())
                    .map { it.items }

    private fun addButtons(calendars: List<CalendarListEntry>) {
        calendars.forEach {
            layout_calendars.addView(createButton(it))
        }
    }

    private fun createButton(calendar: CalendarListEntry): Button {
        val button = Button(requireContext())
        button.text = calendar.summary
        button.setOnClickListener {
            moveToCalendarFragment(calendar.id)
        }
        return button
    }

    private fun moveToCalendarFragment(calendarId: String) {
        CalendarSelectFragmentDirections.actionCalendarListToEventList().apply {
            this.calendarId = calendarId
            findNavController().navigate(this)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_AUTH_PERMISSION && resultCode == Activity.RESULT_OK) {
            getCalendars()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }
}