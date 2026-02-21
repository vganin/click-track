import ActivityKit
import Foundation

struct ClickTrackActivityAttributes: ActivityAttributes {
    struct ContentState: Codable, Hashable {
        var contentText: String
        var isPaused: Bool
    }

    var title: String
}
