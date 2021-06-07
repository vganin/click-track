package com.vsevolodganin.clicktrack.sounds

import com.vsevolodganin.clicktrack.di.module.PlayerDispatcher
import com.vsevolodganin.clicktrack.player.PlayerSoundPool
import com.vsevolodganin.clicktrack.sounds.model.BuiltinClickSounds
import com.vsevolodganin.clicktrack.sounds.model.ClickSounds
import com.vsevolodganin.clicktrack.sounds.model.UserClickSounds
import com.vsevolodganin.clicktrack.storage.ClickSoundsRepository
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class AllClickSoundsPreloader @Inject constructor(
    private val clickSoundsRepository: ClickSoundsRepository,
    private val playerSoundPool: PlayerSoundPool,
    @PlayerDispatcher private val playerDispatcher: CoroutineDispatcher,
) {
    fun preload() = GlobalScope.launch(playerDispatcher) {
        playerSoundPool.preload(
            BuiltinClickSounds.values()
                .asSequence()
                .map(BuiltinClickSounds::sounds)
                .flatMap(ClickSounds::asIterable)
                .asIterable()
        )

        clickSoundsRepository.getAll().firstOrNull()?.also { userSounds ->
            val userSoundsFlattened = userSounds
                .asSequence()
                .map(UserClickSounds::value)
                .flatMap(ClickSounds::asIterable)
                .asIterable()
            playerSoundPool.preload(userSoundsFlattened)
        }
    }
}
