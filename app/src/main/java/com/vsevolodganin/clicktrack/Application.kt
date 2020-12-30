package com.vsevolodganin.clicktrack

import com.vsevolodganin.clicktrack.di.component.DaggerApplicationComponent

class Application : android.app.Application() {
    val daggerComponent = DaggerApplicationComponent.builder()
        .application(this)
        .build()
}
