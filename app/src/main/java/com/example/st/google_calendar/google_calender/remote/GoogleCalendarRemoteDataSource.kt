package com.example.st.google_calendar.google_calender.remote

import com.example.st.google_calendar.google_calender.GoogleCalendarDataSource
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.HttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.calendar.Calendar
import com.google.api.services.calendar.model.CalendarList
import com.google.api.services.calendar.model.Event
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class GoogleCalendarRemoteDataSource @Inject constructor(
        httptransport : HttpTransport,
        jacksonFactory: JacksonFactory,
        googleAccountCredential : GoogleAccountCredential
) : GoogleCalendarDataSource {
    private val calendar: Calendar = Calendar.Builder(httptransport, jacksonFactory, googleAccountCredential)
        .setApplicationName("Google Calendar Api MVC")
        .build()

    override fun getCalendarList(): Single<CalendarList> {
        return Single.fromCallable { calendar.CalendarList().list().execute() }
            .subscribeOn(Schedulers.io())
    }

    override fun getEvents(calendarId: String): Single<List<Event>> {
        return Single.fromCallable {
            calendar.events()
                .list(calendarId)
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute() }
            .subscribeOn(Schedulers.io())
            .map { it.items }
    }
}