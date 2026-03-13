import ActivityKit
import ClickTrackMultiplatform
import Foundation

protocol PlayerURLHandler {
    func handlePlayerURL(_ url: URL)
}

func makeAudioSessionNotification() -> any AudioSessionNotification {
    if #available(iOS 16.1, *) {
        return LiveActivityNotification()
    } else {
        return NowPlayingInfoCenterNotification()
    }
}

/// Implements the Kotlin `AudioSessionNotification` protocol using Live Activities (iOS 16.1+).
@available(iOS 16.1, *)
class LiveActivityNotification: NSObject, AudioSessionNotification, PlayerURLHandler {

    private var onPauseCallback: (() -> Void)?
    private var onResumeCallback: (() -> Void)?
    private var onStopCallback: (() -> Void)?

    func show(title: String, contentText: String, isPaused: Bool) {
        LiveActivityBridge.shared.show(title: title, contentText: contentText, isPaused: isPaused)
    }

    func hide() {
        LiveActivityBridge.shared.hide()
    }

    func setCallbacks(onPause: @escaping () -> Void, onResume: @escaping () -> Void, onStop: @escaping () -> Void) {
        self.onPauseCallback = onPause
        self.onResumeCallback = onResume
        self.onStopCallback = onStop
    }

    func handlePlayerURL(_ url: URL) {
        guard url.scheme == "clicktrack", url.host == "player" else { return }
        switch url.path {
        case "/pause":
            onPauseCallback?()
        case "/resume":
            onResumeCallback?()
        case "/stop":
            onStopCallback?()
        default:
            break
        }
    }
}
