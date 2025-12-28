import SwiftUI
import ClickTrackMultiplatform

struct ContentView: UIViewControllerRepresentable {
    
    let applicationComponent: ApplicationComponent
    
    func makeUIViewController(context: Context) -> some UIViewController {
        return MainKt.createMainViewController(applicationComponent: applicationComponent)
    }

    func updateUIViewController(_ uiViewController: UIViewControllerType, context: Context) {
        uiViewController.view.setNeedsLayout()
    }
    
}
