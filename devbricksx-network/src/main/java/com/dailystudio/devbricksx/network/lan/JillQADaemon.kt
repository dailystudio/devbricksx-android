package com.dailystudio.devbricksx.network.lan

import com.dailystudio.devbricksx.development.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.ServerSocket
import java.net.Socket

interface JillQAHandler {

    fun answerQuestion(msgOfQuestion: String): String

}

class JillQADaemon(private val handler: JillQAHandler) {

    private val daemonScope = CoroutineScope(Dispatchers.IO)

    fun start(port: Int) {
        daemonScope.launch {
            startServer(port)
        }
    }

    fun stop() {
        daemonScope.cancel()
    }

    private fun startServer(port: Int) {
        var serverSocket: ServerSocket? = null

        try {
            serverSocket = ServerSocket(port)
            Logger.debug("Jill Command Daemon is online [port: $port]")

            while (true) {
                val clientSocket = serverSocket.accept()
                println("Jack Client connected: ${clientSocket.inetAddress.hostAddress}")

                daemonScope.launch { handleClient(clientSocket) }
            }
        } catch (e: IOException) {
            Logger.error("Jill Command Daemon error: $e")
        } finally {
            serverSocket?.close()
        }
    }

    private fun handleClient(clientSocket: Socket) {
        Logger.debug("handling Jack Client: $clientSocket")
        try {
            val reader = BufferedReader(InputStreamReader(clientSocket.getInputStream()))
            val writer = PrintWriter(clientSocket.getOutputStream(), true)

            while (true) {
                val request = reader.readLine() ?: break
                Logger.debug("[JCMD] request from jack: [$request]")

                val response = handler.answerQuestion(request)
                Logger.debug("[JCMD] response from jill: [$response]")

                writer.println(response)
            }
        } catch (e: IOException) {
            Logger.error("Jack Client error: $e")
        } finally {
            clientSocket.close()
        }
    }

}