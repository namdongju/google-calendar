package com.example.st.google_calendar.application

import com.example.st.google_calendar.application.di.DaggerApplicationComponent
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication


abstract class BaseApplication : DaggerApplication(){
    override fun applicationInjector(): AndroidInjector<out DaggerApplication> =
        DaggerApplicationComponent.builder().application(this).bind()

}