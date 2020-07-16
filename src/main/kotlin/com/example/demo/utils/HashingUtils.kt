package com.example.demo.utils

import java.security.MessageDigest
import javax.xml.bind.DatatypeConverter

class HashingUtils {

    private val md5 = MessageDigest.getInstance("MD5")

    fun hash(text: String): String {
        md5.update(text.toByteArray())
        val digest: ByteArray = md5.digest()
        return DatatypeConverter.printHexBinary(digest).toUpperCase()
    }

    fun verify(text: String, hash: String) = hash(text) == hash
}