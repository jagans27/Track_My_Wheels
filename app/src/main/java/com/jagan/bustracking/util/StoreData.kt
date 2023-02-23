package com.jagan.bustracking.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class StoreData (private val context:Context) {
    companion object{
        private val Context.datastore: DataStore<Preferences> by preferencesDataStore("Data")
        val DATA_KEY1 = stringPreferencesKey("name")
        val DATA_KEY2 = stringPreferencesKey("bus")

        var dataStoreDriverName = ""
        var dataStoreBusNumber = ""

    }

    val getName : Flow<String> = context.datastore.data.map {
        it[DATA_KEY1]?:""
    }
    val getBus : Flow<String> = context.datastore.data.map {
        it[DATA_KEY2]?:""
    }

    suspend fun saveData(name:String,bus:String){
        context.datastore.edit{
            it[DATA_KEY1] = name
            it[DATA_KEY2] = bus

        }
    }
}