import SwiftUI
import ClickTrackMultiplatform

struct ContentView: UIViewControllerRepresentable {
    
    func makeUIViewController(context: Context) -> some UIViewController {
        return ComposeUIViewControllerProviderKt.provideComposeUIViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewControllerType, context: Context) {
        uiViewController.view.setNeedsLayout()
    }
    
}
