package com.chess.cryptobot.market

import okhttp3.Authenticator
import okhttp3.Credentials
import java.io.IOException
import java.net.*


class BinanceProxySelector(proxyUrl: String?,
                           proxyPort: String?,
                           private val proxyUserName: String?,
                           private val proxyPassword: String?): ProxySelector() {
    private var defaultProxySelector: ProxySelector? = getDefault()

    val proxyAuthenticator = Authenticator { _, response ->
        val builder = response.request().newBuilder()
        if (response.request().header("Proxy-Authorization") == null
                && proxyUserName!=null && proxyPassword != null) {
            val credential: String = Credentials.basic(proxyUserName, proxyPassword)
            builder.header("Proxy-Authorization", credential)
        }

        builder.build()
    }

    private var noProxy = ArrayList<Proxy>()
    private var secureProxy = ArrayList<Proxy>()

    init {
        noProxy.add(Proxy.NO_PROXY)

        if (proxyUrl == null) {
            secureProxy.add(Proxy.NO_PROXY)
        }else {
            val port = proxyPort ?: "80"
            secureProxy.add(Proxy(Proxy.Type.HTTP, InetSocketAddress(
                    proxyUrl, port.toInt())))
        }
    }

    override fun select(uri: URI?): MutableList<Proxy> {
        if (uri!!.path.endsWith("withdraw.html")) {
            return secureProxy
        }

        return   defaultProxySelector?.select(uri) ?: noProxy
    }

    override fun connectFailed(uri: URI?, sa: SocketAddress?, ioe: IOException?) {

    }
}