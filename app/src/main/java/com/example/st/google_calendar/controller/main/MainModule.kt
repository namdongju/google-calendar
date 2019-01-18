package com.example.st.google_calendar.controller.main

import com.example.st.google_calendar.application.scopes.FragmentScoped
import com.example.st.google_calendar.controller.auth.AuthFragment
import com.example.st.google_calendar.controller.calendarview.CalendarFragment
import com.example.st.google_calendar.controller.calendarview.CalendarSelectFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class MainModule {

    @FragmentScoped
    @ContributesAndroidInjector()
    abstract fun authFragment(): AuthFragment

    @FragmentScoped
    @ContributesAndroidInjector()
    abstract fun calendarSelectFragment(): CalendarSelectFragment

    @FragmentScoped
    @ContributesAndroidInjector()
    abstract fun calendarFragment(): CalendarFragment
}