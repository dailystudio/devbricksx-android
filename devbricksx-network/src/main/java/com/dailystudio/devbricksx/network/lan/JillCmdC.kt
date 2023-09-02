package com.dailystudio.devbricksx.network.lan

import com.dailystudio.devbricksx.development.Logger
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket

class JillCmdC() {

    companion object {
        private val GSON = Gson()
    }

    private val jillCmdScope = CoroutineScope(Dispatchers.IO)

    private var socket: Socket? = null
    private var writer: PrintWriter? = null
    private var reader: BufferedReader? = null

    private fun connectServer(ip: String, port: Int) {
        try {
            val newSocket = Socket(ip, port).also {
                socket = it
            }

            Logger.debug("Jack Client is connected with Jill [ip: $ip, port; $port]")

            writer = PrintWriter(newSocket.getOutputStream(), true)
            reader = BufferedReader(InputStreamReader(newSocket.getInputStream()))

        } catch (e: IOException) {
            Logger.debug("Jack Client error: $e")
        }
    }

    private fun disconnectServer() {
        try {
            writer?.close()
            reader?.close()
            socket?.close()
        } catch (e: IOException) {
            Logger.debug("Jack Client disconnect error: $e")
        }

        jillCmdScope.cancel()
    }

    private fun sendMessage(message: String): String {
        Logger.debug("[JCMD] write message : [$writer]")
        writer?.println(message)

        val response = reader?.readLine()
        Logger.debug("[JCMD] jack get resp: [$response]")

        return response ?: ""
    }

    suspend fun connect(jillInfo: JillInfo) {
        val ip = jillInfo.serviceIp
        val port = jillInfo.servicePort

        withContext(jillCmdScope.coroutineContext) {
            Logger.debug("Jack Client is connecting with Jill [ip: $ip, port; $port]")
            connectServer(ip, port)
        }
    }

    suspend fun disconnect() {
        Logger.debug("Jack Client is disconnecting from Jill [socket: $socket]")
        withContext(jillCmdScope.coroutineContext) {
            disconnectServer()
        }
    }

    suspend fun executeCmd(action: String, args: Map<String, String> = mapOf()): JillCmdResult {
        return try {
            withContext(jillCmdScope.coroutineContext) {
                val cmd = JillCmd(action, args)

                val resp = sendMessage(GSON.toJson(cmd))

                GSON.fromJson(resp, JillCmdResult::class.java)
            }
        } catch (e: Exception) {
            Logger.debug("failed to execute cmd [$action]: $e")
            JillCmdResult.ERROR
        }
    }

}
