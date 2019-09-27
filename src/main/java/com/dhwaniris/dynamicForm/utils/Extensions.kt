package com.dhwaniris.dynamicForm.utils

import com.google.gson.Gson
import org.json.JSONArray
import org.json.JSONObject
import java.lang.reflect.Type



data class Optional<T>(val value: T?)
/**
 * convert any to optional type
 */
fun <T> T?.asOptional() = Optional(this)


/**
 * convert object to Json object type
 */
fun Any?.toJson(): JSONObject {
    return if (this !is Collection<Any?>) {
        if (this != null)
            JSONObject(Gson().toJson(this))
        else
            JSONObject()
    } else {
        val json = JSONObject()
        json.put("data", Gson().toJson(this))
        json
    }
}

fun Array<Any>?.toJson():JSONArray{
    return if (this != null)
        JSONArray(Gson().toJson(this))
    else
        JSONArray()
}
/**
 * convert list or Array object to jsonArray
 */
fun Collection<Any>?.toJson(): JSONArray {
    return if (this != null)
        JSONArray(Gson().toJson(this))
    else
        JSONArray()
}

fun Collection<Any>?.toJson(type: Type): JSONArray {
    return if (this != null)
        JSONArray(Gson().toJson(this, type))
    else
        JSONArray()
}