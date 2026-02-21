import SwiftUI
import WidgetKit

@main
struct ClickTrackLiveActivityBundle: WidgetBundle {
    var body: some Widget {
        if #available(iOS 16.1, *) {
            ClickTrackLiveActivity()
        }
    }
}
