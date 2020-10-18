package net.ganin.vsevolod.clicktrack.di.component

import dagger.BindsInstance
import dagger.Component
import net.ganin.vsevolod.clicktrack.Application
import net.ganin.vsevolod.clicktrack.di.module.ApplicationScopedAndroidModule
import net.ganin.vsevolod.clicktrack.di.module.ApplicationScopedCoroutineModule
import javax.inject.Singleton

typealias ApplicationScoped = Singleton

@ApplicationScoped
@Component(
    modules = [
        ApplicationScopedCoroutineModule::class,
        ApplicationScopedAndroidModule::class,
    ]
)
interface ApplicationComponent {
    fun viewModelComponentBuilder(): ViewModelComponent.Builder

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        fun build(): ApplicationComponent
    }
}
