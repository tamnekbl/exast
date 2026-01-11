package com.inrotate.exast.utils

import com.russhwolf.settings.Settings
import kotlin.reflect.KClass
import kotlin.reflect.KProperty


open class KPreference<T>(
    private val defVal: T?,
    private val preferenceName: String? = null,
    private val clazz: KClass<*>
) {

    @Suppress("UNCHECKED_CAST")
    open operator fun getValue(thisRef: PreferenceHolder, property: KProperty<*>): T = with(thisRef.storage) {
        val key = preferenceName ?: property.name
        when (clazz) {
            Int::class -> getInt (key, defVal as Int) as T
            Long::class -> getLong(key, defVal as Long) as T
            Float::class -> getFloat(key, defVal as Float) as T
            String::class -> getString(key, (defVal as String)) as T
            Boolean::class -> getBoolean(key, defVal as Boolean) as T
            else -> null!!
        }
    }

    open operator fun setValue(thisRef: PreferenceHolder, property: KProperty<*>, value: T) {
        val key = preferenceName ?: property.name
        thisRef.storage.run {
            when (clazz) {
                Int::class -> putInt(key, value as Int)
                Long::class -> putLong(key, value as Long)
                Float::class -> putFloat(key, value as Float)
                String::class -> putString(key, value as String)
                Boolean::class -> putBoolean(key, value as Boolean)
            }
            this
        }
        //thisRef.analytics.preferenceChanged(key, value)
    }
}

interface PreferenceHolder {
    val storage: Settings
}

class IntPreference(defVal: Int, preferenceName: String? = null) :
    KPreference<Int>(defVal, preferenceName, Int::class)

class LongPreference(defVal: Long, preferenceName: String? = null) :
    KPreference<Long>(defVal, preferenceName, Long::class)

class FloatPreference(defVal: Float, preferenceName: String? = null) :
    KPreference<Float>(defVal, preferenceName, Float::class)

class BooleanPreference(defVal: Boolean, preferenceName: String? = null) :
    KPreference<Boolean>(defVal, preferenceName, Boolean::class)

class StringPreference(defVal: String? = null, preferenceName: String? = null) :
    KPreference<String>(defVal, preferenceName, String::class)


