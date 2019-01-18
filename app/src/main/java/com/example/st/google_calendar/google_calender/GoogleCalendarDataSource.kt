package com.example.st.google_calendar.google_calender

import com.google.api.services.calendar.model.CalendarList
import com.google.api.services.calendar.model.Event
import io.reactivex.Single

interface GoogleCalendarDataSource {

    fun getCalendarList(): Single<CalendarList>
    fun getEvents(calendarId: String): Single<List<Event>>
}