package com.example.st.google_calendar.application

import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication


abstract class BaseApplication : DaggerApplication(){
    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        TODO()
    }
//            DaggerBaseApplication.builder().application(this).bind()
}