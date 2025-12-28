import SwiftUI
import ClickTrackMultiplatform

@main
struct ClickTrackApp: App {
    
    private let applicationComponent = MainKt.createApplicationComponent()
    
    var body: some Scene {
        WindowGroup {
            ContentView(applicationComponent: applicationComponent)
        }
    }
}
