package com.example.st.google_calendar.application.di

import com.example.onemask.myapplication.scopes.ActivityScoped
import com.example.st.google_calendar.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBindModule {
    @ActivityScoped
    @ContributesAndroidInjector
    abstract fun mainActivity()  : MainActivity
}
