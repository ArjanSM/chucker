package com.chuckerteam.chucker.internal.data.repository

import android.content.Context
import android.content.Context.MODE_PRIVATE
import androidx.annotation.VisibleForTesting
import com.chuckerteam.chucker.internal.data.model.FILTERS_PREF
import com.chuckerteam.chucker.internal.data.preferences.SharedPreferencesRepository
import com.chuckerteam.chucker.internal.data.repository.RepositoryProvider.initialize
import com.chuckerteam.chucker.internal.data.room.ChuckerDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * A singleton to hold the [HttpTransactionRepository] instance.
 * Make sure you call [initialize] before accessing the stored instance.
 */
internal object RepositoryProvider {

    private var transactionRepository: HttpTransactionRepository? = null
    private var sharedPreferencesRepository: SharedPreferencesRepository? = null
    fun transaction(): HttpTransactionRepository {
        return checkNotNull(transactionRepository) {
            "You can't access the transaction repository if you don't initialize it!"
        }
    }

    /**
     * Idempotent method. Must be called before accessing the repositories.
     */
    fun initialize(applicationContext: Context) {
        if (transactionRepository == null) {
            val db = ChuckerDatabase.create(applicationContext)
            transactionRepository = HttpTransactionDatabaseRepository(db)
        }
    }

    suspend fun initializePreferences(applicationContext: Context) {
        withContext(Dispatchers.IO) {
            if (sharedPreferencesRepository == null) {
                val sharedPreferences = applicationContext.getSharedPreferences(FILTERS_PREF, MODE_PRIVATE)
                sharedPreferencesRepository = SharedPreferencesRepository(sharedPreferences)
            }
        }
    }

    fun preferences(): SharedPreferencesRepository {
        return checkNotNull(sharedPreferencesRepository) {
            "You cannot access SharedPreferences if you don't initialize it"
        }
    }

    /**
     * Cleanup stored singleton objects
     */

    @VisibleForTesting
    fun close() {
        transactionRepository = null
        sharedPreferencesRepository = null
    }
}
