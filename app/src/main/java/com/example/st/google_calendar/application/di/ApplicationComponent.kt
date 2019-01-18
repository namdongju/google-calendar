package com.example.st.google_calendar.application.di

import android.app.Application
import com.example.st.google_calendar.application.BaseApplication
import com.example.st.google_calendar.remote.GoogleCalendarDataModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.support.AndroidSupportInjectionModule
import dagger.android.AndroidInjector
import javax.inject.Singleton

@Singleton
@Component(
        modules = [AndroidSupportInjectionModule::class,
            ApplicationModule::class,
            ActivityBindModule::class,
            GoogleCalendarDataModule::class
        ]
)
interface ApplicationComponent : AndroidInjector<BaseApplication>{
    @Component.Builder
    interface Builder{
        @BindsInstance
        fun application(application : Application) : Builder
        fun bind() : ApplicationComponent
    }
}