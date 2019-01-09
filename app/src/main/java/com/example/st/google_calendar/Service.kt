package com.example.st.google_calendar

import com.google.api.services.calendar.model.CalendarList
import com.google.api.services.calendar.model.Event
import io.reactivex.Single


interface Service {
    fun getCalendarList(): Single<CalendarList>
    fun getEventList(calendarId: String): Single<List<Event>>
}