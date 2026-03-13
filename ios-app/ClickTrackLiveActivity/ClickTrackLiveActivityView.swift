import ActivityKit
import SwiftUI
import WidgetKit

struct ClickTrackLiveActivityView: View {
    let context: ActivityViewContext<ClickTrackActivityAttributes>

    var body: some View {
        HStack(spacing: 16) {
            VStack(alignment: .leading, spacing: 4) {
                Text(context.attributes.title)
                    .font(.headline)
                    .foregroundStyle(.primary)
                Text(context.state.contentText)
                    .font(.subheadline)
                    .foregroundStyle(.secondary)
                    .lineLimit(1)
            }
            Spacer()
            HStack(spacing: 12) {
                Link(destination: URL(string: context.state.isPaused ? "clicktrack://player/resume" : "clicktrack://player/pause")!) {
                    Image(systemName: context.state.isPaused ? "play.fill" : "pause.fill")
                        .font(.title2)
                }
                Link(destination: URL(string: "clicktrack://player/stop")!) {
                    Image(systemName: "stop.fill")
                        .font(.title2)
                }
            }
            .foregroundStyle(.primary)
        }
        .padding()
    }
}

@available(iOS 16.1, *)
struct ClickTrackLiveActivity: Widget {
    var body: some WidgetConfiguration {
        ActivityConfiguration(for: ClickTrackActivityAttributes.self) { context in
            ClickTrackLiveActivityView(context: context)
                .activityBackgroundTint(Color(.systemBackground))
        } dynamicIsland: { context in
            DynamicIsland {
                DynamicIslandExpandedRegion(.leading) {
                    VStack(alignment: .leading) {
                        Text(context.attributes.title)
                            .font(.headline)
                        Text(context.state.contentText)
                            .font(.caption)
                            .foregroundStyle(.secondary)
                            .lineLimit(1)
                    }
                    .padding(.leading)
                }
                DynamicIslandExpandedRegion(.trailing) {
                    HStack(spacing: 8) {
                        Link(destination: URL(string: context.state.isPaused ? "clicktrack://player/resume" : "clicktrack://player/pause")!) {
                            Image(systemName: context.state.isPaused ? "play.fill" : "pause.fill")
                        }
                        Link(destination: URL(string: "clicktrack://player/stop")!) {
                            Image(systemName: "stop.fill")
                        }
                    }
                    .padding(.trailing)
                }
            } compactLeading: {
                Image(systemName: "metronome.fill")
            } compactTrailing: {
                Image(systemName: context.state.isPaused ? "pause.fill" : "play.fill")
            } minimal: {
                Image(systemName: "metronome.fill")
            }
        }
    }
}
