import SwiftUI
import ClickTrackMultiplatform

struct ContentView: UIViewControllerRepresentable {
    
    let applicationComponent: ApplicationComponent
    let decomposeComponentContext: DecomposeComponentContext
    
    func makeUIViewController(context: Context) -> some UIViewController {
        return MainKt.createMainViewController(
            applicationComponent: applicationComponent,
            componentContext: decomposeComponentContext,
        )
    }

    func updateUIViewController(_ uiViewController: UIViewControllerType, context: Context) {
        uiViewController.view.setNeedsLayout()
    }
    
}
