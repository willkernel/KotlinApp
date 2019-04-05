package com.willkernel.kotlinapp.utils

import android.content.Context
import android.content.SharedPreferences
import com.willkernel.kotlinapp.MyApplication
import java.io.*
import java.net.URLDecoder
import java.net.URLEncoder
import kotlin.reflect.KProperty


class Preference<T>(val name: String, private val default: T) {
    companion object {
        private const val file_name = "kotlin_preference"
        private val prefs: SharedPreferences by lazy {
            MyApplication.context.getSharedPreferences(file_name, Context.MODE_PRIVATE)
        }

        fun clearPreference() {
            prefs.edit().clear().apply()
        }

        fun clearPreference(key: String) {
            prefs.edit().remove(key).apply()
        }
    }

    private fun getSharedPrefValue(name: String, default: T): T {
        with(prefs) {
            val res: Any = when (default) {
                is Long -> getLong(name, default)
                is String -> getString(name, default)
                is Int -> getInt(name, default)
                is Boolean -> getBoolean(name, default)
                is Float -> getFloat(name, default)
                else -> deSerialization(getString(name, serialize(default)))
            }
            return res as T
        }
    }

    private fun setSharedPrefValue(name: String, value: T) {
        with(prefs.edit()) {
            when (value) {
                is Long -> putLong(name, value)
                is String -> putString(name, value)
                is Int -> putInt(name, value)
                is Boolean -> putBoolean(name, value)
                is Float -> putFloat(name, value)
                else -> putString(name, serialize(value))
            }.apply()
        }
    }

    /**
     * 反序列化对象

     * @throws IOException
     * *
     * @throws ClassNotFoundException
     */
    @Throws(IOException::class, ClassNotFoundException::class)
    private fun <A> deSerialization(str: String): A {
        val redStr = URLDecoder.decode(str, "UTF-8")
        val byteArrayInputStream = ByteArrayInputStream(redStr.toByteArray(charset("ISO-8859-1")))
        val objectInputStream = ObjectInputStream(byteArrayInputStream)
        val obj = objectInputStream.readObject() as A
        objectInputStream.close()
        byteArrayInputStream.close()
        return obj
    }

    /**
     * 序列化对象

     * @param person
     * *
     * @return
     * *
     * @throws IOException
     */
    @Throws(IOException::class)
    private fun <A> serialize(obj: A): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        val objectOutputStream = ObjectOutputStream(byteArrayOutputStream)
        objectOutputStream.writeObject(obj)
        val str = byteArrayOutputStream.toString("ISO-8859-1")
        val encode = URLEncoder.encode(str, "UTF-8")
        objectOutputStream.close()
        byteArrayOutputStream.close()
        return encode
    }

    fun contains(key: String): Boolean {
        return prefs.contains(key)
    }

    operator fun getValue(thisRef: Any, property: KProperty<*>): T {
        return getSharedPrefValue(name, default)
    }

    operator fun setValue(thisRef: Any, property: KProperty<*>, t: T) {
        setSharedPrefValue(name, t)
    }


}