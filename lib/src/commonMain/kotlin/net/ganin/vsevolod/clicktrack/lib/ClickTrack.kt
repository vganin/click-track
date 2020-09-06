package net.ganin.vsevolod.clicktrack.lib

public class ClickTrack(
    public val duration: Float,
    public val initialCue: Cue,
    public val followingCues: List<CueWithTimestamp>
)

public class CueWithTimestamp(
    public val timestamp: Float,
    public val cue: Cue
)
