package com.example.st.google_calendar.controller.calendarview


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.st.google_calendar.R
import com.example.st.google_calendar.google_calender.GoogleCalendarRepository
import com.google.api.services.calendar.model.Event
import dagger.android.support.DaggerFragment
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_google_event_list.*
import javax.inject.Inject

class CalendarFragment : DaggerFragment() {

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
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_google_event_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        arguments?.let {
            val safeArgs = CalendarFragmentArgs.fromBundle(it)
            if (safeArgs.calendarId != getString(R.string.calendarId_default)) {
                setupCalendarData(safeArgs.calendarId)
            }
        }
    }

    private fun setupCalendarData(calendarId: String) {
        getEvents(calendarId).subscribe({
            text_calendar.text = it.fold("") { acc, event ->
                acc + "date=${event.start.date} summary=${event.summary}\n"
            }
        }, {
            it.printStackTrace()
        }).apply {
            compositeDisposable.add(this)
        }
    }

    private fun getEvents(calendarId: String): Single<List<Event>> =
            googleCalendarRepository.getEvents(calendarId)
                    .observeOn(AndroidSchedulers.mainThread())

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }
}