package net.ganin.vsevolod.clicktrack.lib

import kotlinx.serialization.Serializable
import net.ganin.vsevolod.clicktrack.lib.android.AndroidParcelable
import net.ganin.vsevolod.clicktrack.lib.android.AndroidParcelize

@Serializable
@AndroidParcelize
public class ClickTrackWithMeta(
    public val name: String,
    public val clickTrack: ClickTrack
) : AndroidParcelable
