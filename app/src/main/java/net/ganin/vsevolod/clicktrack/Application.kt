package net.ganin.vsevolod.clicktrack

import net.ganin.vsevolod.clicktrack.di.component.DaggerApplicationComponent

class Application : android.app.Application() {
    val daggerComponent = DaggerApplicationComponent.builder()
        .application(this)
        .build()
}
