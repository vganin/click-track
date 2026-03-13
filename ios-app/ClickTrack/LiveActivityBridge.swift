import ActivityKit
import Foundation

/// Manages the Live Activity lifecycle from the main app side.
/// Kotlin calls into `LiveActivityNotification` which delegates here.
/// Button taps in the Live Activity send `clicktrack://player/{action}` URLs
/// back to the main app, handled in `ClickTrackApp.swift`.
@objc public class LiveActivityBridge: NSObject {
    @objc public static let shared = LiveActivityBridge()

    private var currentActivity: Any? // Activity<ClickTrackActivityAttributes>

    private override init() {}

    @available(iOS 16.1, *)
    func show(title: String, contentText: String, isPaused: Bool) {
        let state = ClickTrackActivityAttributes.ContentState(
            contentText: contentText,
            isPaused: isPaused
        )
        if let activity = currentActivity as? Activity<ClickTrackActivityAttributes> {
            Task {
                await activity.update(using: state)
            }
        } else {
            let attributes = ClickTrackActivityAttributes(title: title)
            do {
                let activity = try Activity<ClickTrackActivityAttributes>.request(
                    attributes: attributes,
                    contentState: state,
                    pushType: nil
                )
                currentActivity = activity
            } catch {
                // Live Activities not supported or denied — silently ignore
            }
        }
    }

    @available(iOS 16.1, *)
    func hide() {
        guard let activity = currentActivity as? Activity<ClickTrackActivityAttributes> else { return }
        Task {
            await activity.end(using: nil, dismissalPolicy: .immediate)
        }
        currentActivity = nil
    }
}
