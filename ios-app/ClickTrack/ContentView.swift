import SwiftUI
import ClickTrackMultiplatform

struct ContentView: UIViewControllerRepresentable {
    
    let applicationComponent: ApplicationComponent
    let decomposeComponentContext: DecomposeComponentContext
    let audioSessionNotification: any AudioSessionNotification
    
    func makeUIViewController(context: Context) -> some UIViewController {
        return MainKt.createMainViewController(
            applicationComponent: applicationComponent,
            componentContext: decomposeComponentContext,
            audioSessionNotification: audioSessionNotification,
        )
    }

    func updateUIViewController(_ uiViewController: UIViewControllerType, context: Context) {
        uiViewController.view.setNeedsLayout()
    }
    
}
