package io.homeassistant.companion.android.common.data

import android.util.Log
import io.homeassistant.companion.android.common.data.keychain.KeyChainRepository
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import java.net.Socket
import java.security.KeyStore
import java.security.Principal
import java.security.PrivateKey
import java.security.cert.X509Certificate
import javax.inject.Inject
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLEngine
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509ExtendedKeyManager
import javax.net.ssl.X509TrustManager

class TLSHelper @Inject constructor(
    private val keyChainRepository: KeyChainRepository
) {

    fun setupOkHttpClientSSLSocketFactory(builder: OkHttpClient.Builder) {
        val trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
        trustManagerFactory.init(null as KeyStore?)
        val trustManagers = trustManagerFactory.trustManagers

        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(arrayOf(getMTLSKeyManagerForOKHTTP()), trustManagers, null)

        builder.sslSocketFactory(sslContext.socketFactory, trustManagers[0] as X509TrustManager)
    }

    private fun getMTLSKeyManagerForOKHTTP(): X509ExtendedKeyManager {
        Log.i("RUBBERDUCK", "TLSHelper::getMTLSKeyManagerForOKHTTP()")
        return object : X509ExtendedKeyManager() {
            override fun chooseEngineClientAlias(
                keyType: Array<out String>?,
                issuers: Array<out Principal>?,
                engine: SSLEngine?
            ): String {
                Log.i("RUBBERDUCK", "TLSHelper::getMTLSKeyManagerForOKHTTP: chooseEngineClientAlias(keyType = ${keyType?.toList()}, issuers = ${issuers?.toList()}, engine = $engine)")
                return super.chooseEngineClientAlias(keyType, issuers, engine)
            }

            override fun chooseEngineServerAlias(
                keyType: String?,
                issuers: Array<out Principal>?,
                engine: SSLEngine?
            ): String {
                Log.i("RUBBERDUCK", "TLSHelper::getMTLSKeyManagerForOKHTTP: chooseEngineServerAlias(keyType = ${keyType?.toList()}, issuers = ${issuers?.toList()}, engine = $engine)")
                return super.chooseEngineServerAlias(keyType, issuers, engine)
            }

            override fun getClientAliases(
                keyType: String?,
                issuers: Array<out Principal>?
            ): Array<String> {
                Log.i("RUBBERDUCK", "TLSHelper::getMTLSKeyManagerForOKHTTP: getClientAliases(keyType = $keyType, issuers = ${issuers?.toList()})")
                return emptyArray()
            }

            override fun chooseClientAlias(
                keyType: Array<out String>?,
                issuers: Array<out Principal>?,
                socket: Socket?
            ): String {
                Log.i("RUBBERDUCK", "TLSHelper::getMTLSKeyManagerForOKHTTP: chooseClientAlias(keyType = ${keyType?.toList()}, issuers = ${issuers?.toList()}, socket = $socket)")
                return ""
            }

            override fun getServerAliases(
                keyType: String?,
                issuers: Array<out Principal>?
            ): Array<String> {
                Log.i("RUBBERDUCK", "TLSHelper::getMTLSKeyManagerForOKHTTP: getServerAliases(keyType = $keyType, issuers = ${issuers?.toList()})")
                return arrayOf()
            }

            override fun chooseServerAlias(
                keyType: String?,
                issuers: Array<out Principal>?,
                socket: Socket?
            ): String {
                Log.i("RUBBERDUCK", "TLSHelper::getMTLSKeyManagerForOKHTTP: chooseServerAlias(keyType = $keyType, issuers = ${issuers?.toList()}, socket = $socket)")
                return ""
            }

            // TODO use the alias parameter?
            override fun getCertificateChain(alias: String?): Array<X509Certificate>? {
                Log.i("RUBBERDUCK", "TLSHelper::getMTLSKeyManagerForOKHTTP: getCertificateChain(alias = $alias)")
                return keyChainRepository.getCertificateChain()
            }

            // TODO use the alias parameter?
            override fun getPrivateKey(alias: String?): PrivateKey? {
                Log.i("RUBBERDUCK", "TLSHelper::getMTLSKeyManagerForOKHTTP: getPrivateKey(alias = $alias)")
                return keyChainRepository.getPrivateKey()
            }
        }
    }
}
