package com.dailystudio.devbricksx.network.lan

import android.net.Uri
import android.text.TextUtils
import com.dailystudio.devbricksx.development.Logger
import fi.iki.elonen.NanoHTTPD

class JillHttpD(port: Int): NanoHTTPD(port) {

    private val emptyResponse = newFixedLengthResponse("")

    override fun serve(session: IHTTPSession?): Response {
        Logger.debug("session: [%s]", session)

        return if (session != null) {
            handleHttpSession(session)
        } else {
            super.serve(session)
        }

    }

    private fun handleHttpSession(session: IHTTPSession): Response {
        val uriString = session.uri
        if (TextUtils.isEmpty(uriString)) {
            return emptyResponse
        }
        val uri = Uri.parse(uriString) ?: return emptyResponse
        val command = uri.lastPathSegment
        Logger.debug("[JCMD] command = %s", command)

        return newFixedLengthResponse(command)
    }
}