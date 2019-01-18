package com.example.st.google_calendar.application.di

import dagger.Component
import dagger.BindsInstance
import javax.inject.Singleton
import android.app.Application
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import com.example.st.google_calendar.application.GlobalApplication
import com.example.st.google_calendar.google_calender.GoogleCalendarDataModule

@Singleton
@Component(
    modules = [AndroidSupportInjectionModule::class,
        ApplicationModule::class,
        ActivityBindingModule::class,
        GoogleCalendarDataModule::class
    ]
)

interface AppComponent : AndroidInjector<GlobalApplication> {
    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder
        fun build(): AppComponent
    }
}