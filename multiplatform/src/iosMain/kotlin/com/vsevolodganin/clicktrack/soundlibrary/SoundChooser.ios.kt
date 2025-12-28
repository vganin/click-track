package com.vsevolodganin.clicktrack.soundlibrary

import com.vsevolodganin.clicktrack.di.component.MainControllerScope
import com.vsevolodganin.clicktrack.model.ClickSoundSource
import com.vsevolodganin.clicktrack.model.ClickSoundType
import com.vsevolodganin.clicktrack.model.ClickSoundsId
import com.vsevolodganin.clicktrack.storage.ClickSoundsRepository
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import platform.Foundation.NSURL
import platform.UIKit.UIApplication
import platform.UIKit.UIDocumentPickerDelegateProtocol
import platform.UIKit.UIDocumentPickerViewController
import platform.UniformTypeIdentifiers.UTTypeAudio
import platform.darwin.NSObject

@SingleIn(MainControllerScope::class)
@Inject
actual class SoundChooser(
    private val clickSoundsRepository: ClickSoundsRepository,
) {
    actual suspend fun launchFor(
        id: ClickSoundsId.Database,
        type: ClickSoundType,
    ): Unit = withContext(Dispatchers.Main) {
        val picker = UIDocumentPickerViewController(
            forOpeningContentTypes = listOf(UTTypeAudio),
            asCopy = false,
        ).apply {
            delegate = Delegate(id, type, clickSoundsRepository)
            modalPresentationStyle = platform.UIKit.UIModalPresentationFormSheet
            allowsMultipleSelection = false
        }

        UIApplication.sharedApplication.keyWindow?.rootViewController
            ?.presentViewController(
                viewControllerToPresent = picker,
                animated = true,
                completion = null,
            )
    }

    private class Delegate(
        private val id: ClickSoundsId.Database,
        private val type: ClickSoundType,
        private val clickSoundsRepository: ClickSoundsRepository,
    ) : NSObject(), UIDocumentPickerDelegateProtocol {

        override fun documentPicker(controller: UIDocumentPickerViewController, didPickDocumentsAtURLs: List<*>) {
            val url = didPickDocumentsAtURLs.firstOrNull() as? NSURL ?: return
            if (url.startAccessingSecurityScopedResource()) {
                try {
                    clickSoundsRepository.update(id, type, ClickSoundSource(url.absoluteString!!))
                } finally {
                    url.stopAccessingSecurityScopedResource()
                }
            }
        }
    }
}
