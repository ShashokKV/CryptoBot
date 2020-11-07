package com.chess.cryptobot.market

import okhttp3.Authenticator
import okhttp3.Credentials
import java.io.IOException
import java.net.*


class BinanceProxySelector(proxyUrl: String?,
                           proxyPort: String?,
                           private val proxyUserName: String?,
                           private val proxyPassword: String?): ProxySelector() {

    val proxyAuthenticator = Authenticator { _, response ->
        val builder = response.request.newBuilder()
        if (response.request.header("Proxy-Authorization") == null
                && proxyUserName!=null && proxyPassword != null) {
            val credential: String = Credentials.basic(proxyUserName, proxyPassword)
            builder.header("Proxy-Authorization", credential)
        }

        builder.build()
    }

    private var secureProxy = ArrayList<Proxy>()

    init {
        if (proxyUrl == null) {
            secureProxy.add(Proxy.NO_PROXY)
        }else {
            val port = proxyPort ?: "80"
            secureProxy.add(Proxy(Proxy.Type.HTTP, InetSocketAddress.createUnresolved(
                    proxyUrl, port.toInt())))
        }
    }

    override fun select(uri: URI?): MutableList<Proxy> {
        return secureProxy
    }

    override fun connectFailed(uri: URI?, sa: SocketAddress?, ioe: IOException?) {

    }
}