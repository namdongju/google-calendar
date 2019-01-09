package com.example.st.google_calendar

import com.google.api.services.calendar.model.CalendarList
import com.google.api.services.calendar.model.Event
import io.reactivex.Single
import javax.inject.Inject


class Repository @Inject constructor(private val remote: DataService) : Service {
    override fun getCalendarList(): Single<CalendarList> {
        return remote.getCalendarList()
    }

    override fun getEventList(calendarId: String): Single<List<Event>> {
        return remote.getEventList(calendarId)
    }
}
