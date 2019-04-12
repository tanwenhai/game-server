package com.twh.core

import com.google.common.base.Charsets
import com.twh.commons.JsonUtils

class ServerMetaData {
    private val serverType: ServerType? = null

    private val serverStatus: ServerStatus? = null

    private val ip: String? = null

    private val port: Int = 0

    fun toJsonByteArray(): ByteArray {
        return JsonUtils.obj2str(this)!!.toByteArray(Charsets.UTF_8)
    }

    fun from(bytes: ByteArray): ServerMetaData? {
        return JsonUtils.json2pojo(String(bytes, Charsets.UTF_8), ServerMetaData::class.java)
    }
}