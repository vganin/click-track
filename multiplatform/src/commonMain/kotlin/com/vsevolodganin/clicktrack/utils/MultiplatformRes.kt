package com.vsevolodganin.clicktrack.utils

import clicktrack.multiplatform.generated.resources.Res

typealias MultiplatformRes = Res

@Suppress("UnusedReceiverParameter")
val MultiplatformRes.string get() = Res.string
