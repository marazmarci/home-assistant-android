package io.homeassistant.companion.android.common.data.keychain

import android.app.Activity
import android.os.Build
import android.security.KeyChain
import android.security.KeyChainAliasCallback
import androidx.annotation.RequiresApi
import java.security.Principal
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object KeyChainHelper {

    /**
     * TODO documentation
     */
    @RequiresApi(Build.VERSION_CODES.M)
    suspend fun choosePrivateKeyAlias(activity: Activity, principals: Array<Principal>?): String? =
        suspendCoroutine { continuation ->
            val callback = KeyChainAliasCallback { alias ->
                continuation.resume(alias)
            }
            KeyChain.choosePrivateKeyAlias(activity, callback, arrayOf<String>(), principals, null, null)
        }

}