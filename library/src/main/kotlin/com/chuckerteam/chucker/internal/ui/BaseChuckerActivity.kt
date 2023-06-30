package com.chuckerteam.chucker.internal.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.chuckerteam.chucker.internal.data.preferences.PreferencesManager
import com.chuckerteam.chucker.internal.data.repository.RepositoryProvider
import kotlinx.coroutines.launch

internal abstract class BaseChuckerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        RepositoryProvider.initialize(applicationContext)
        lifecycleScope.launch {
            PreferencesManager.initialize(applicationContext)
        }
    }

    override fun onResume() {
        super.onResume()
        isInForeground = true
    }

    override fun onPause() {
        super.onPause()
        isInForeground = false
    }

    companion object {
        var isInForeground: Boolean = false
            private set
    }

    fun showToast(message: String, toastDuration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(this.applicationContext, message, toastDuration).show()
    }
}
