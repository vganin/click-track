import SwiftUI
import ClickTrackMultiplatform

struct ContentView: UIViewControllerRepresentable {
    
    func makeUIViewController(context: Context) -> some UIViewController {
        return MainKt.mainViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewControllerType, context: Context) {
        uiViewController.view.setNeedsLayout()
    }
    
}
