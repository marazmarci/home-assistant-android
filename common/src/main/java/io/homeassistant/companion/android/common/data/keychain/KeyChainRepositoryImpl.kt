package io.homeassistant.companion.android.common.data.keychain

import android.content.Context
import android.security.KeyChain
import android.util.Log
import io.homeassistant.companion.android.common.data.prefs.PrefsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.withContext
import java.security.PrivateKey
import java.security.cert.X509Certificate
import java.util.concurrent.Executors
import javax.inject.Inject

class KeyChainRepositoryImpl @Inject constructor(
    private val prefsRepository: PrefsRepository
) : KeyChainRepository {

    companion object {
        private const val TAG = "KeyChainRepository"
    }

    private var certificateChainAlias: String? = null
    private var privateKey: PrivateKey? = null
    private var chain: Array<X509Certificate>? = null
    // TODO SharedFlow / StateFlow ???

    override suspend fun clear() {
        prefsRepository.saveClientCertificateChainAlias("")
    }

    override suspend fun load(context: Context, alias: String) {
        this.certificateChainAlias = alias
        prefsRepository.saveClientCertificateChainAlias(alias)
        load(context)
    }

    override suspend fun load(context: Context) = withContext(Dispatchers.IO) {
        if (certificateChainAlias == null) {
            certificateChainAlias = prefsRepository.getClientCertificateChainAlias()
        }

        doLoad(context)
    }

    override fun getAlias(): String? {
        return certificateChainAlias
    }

    override fun getPrivateKey(): PrivateKey? {
        return privateKey
    }

    override fun getCertificateChain(): Array<X509Certificate>? {
        return chain
    }

    private suspend fun doLoad(context: Context): Unit = withContext(Dispatchers.IO) {
        synchronized(Lock) { // TODO different mutex solution?
            certificateChainAlias?.takeIf { it.isNotEmpty() }?.let { alias ->
                if (chain == null) {
                    chain = try {
                        KeyChain.getCertificateChain(context, alias)
                    } catch (e: Exception) {
                        Log.e(TAG, "Exception getting certificate chain", e)
                        null
                    }
                }
                if (privateKey == null) {
                    privateKey = try {
                        KeyChain.getPrivateKey(context, alias)
                    } catch (e: Exception) {
                        Log.e(TAG, "Exception getting private key", e)
                        null
                    }
                }
            }
        }
    }
}

private object Lock