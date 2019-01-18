package com.example.st.google_calendar.application.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import com.example.st.google_calendar.controller.main.MainModule
import com.example.st.google_calendar.controller.main.MainActivity
import com.example.st.google_calendar.application.scopes.ActivityScoped

@Module
abstract class ActivityBindingModule {

    @ActivityScoped
    @ContributesAndroidInjector(modules = [MainModule::class])
    abstract fun mainActivity(): MainActivity

}