import SwiftUI
import ClickTrackMultiplatform
import FirebaseCore

@main
struct ClickTrackApp: App {
    
    @UIApplicationDelegateAdaptor(AppDelegate.self)
    var appDelegate: AppDelegate
    
    private let applicationComponent = MainKt.createApplicationComponent()
    
    var body: some Scene {
        WindowGroup {
            ContentView(
                applicationComponent: applicationComponent,
                decomposeComponentContext: ComponentContextKt.createComponentContext(stateKeeper: appDelegate.stateKeeper)
            )
        }
    }
}

class AppDelegate: NSObject, UIApplicationDelegate {
    
    var stateKeeper = StateKeeperUtilsKt.createStateKeeperDispatcher(savedState: nil)
    
    func application(
        _ application: UIApplication,
        didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey : Any]? = nil
    ) -> Bool {
        FirebaseApp.configure()
        return true
      }
    
    func application(_ application: UIApplication, shouldSaveSecureApplicationState coder: NSCoder) -> Bool {
        StateKeeperUtilsKt.save(coder: coder, state: stateKeeper.save())
        return true
    }
    
    func application(_ application: UIApplication, shouldRestoreSecureApplicationState coder: NSCoder) -> Bool {
        stateKeeper = StateKeeperUtilsKt.createStateKeeperDispatcher(savedState: StateKeeperUtilsKt.restore(coder: coder))
        return true
    }
}
