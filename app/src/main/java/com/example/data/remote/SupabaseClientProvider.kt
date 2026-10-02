package com.example.data.remote

import android.util.Log
import com.example.BuildConfig
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.auth.Auth

object SupabaseClientProvider {
    private const val TAG = "SupabaseClient"

    val client: SupabaseClient by lazy {
        val url: String = try {
            val configUrl = BuildConfig.SUPABASE_URL
            if (configUrl.isNullOrBlank()) "https://sample-project.supabase.co" else configUrl
        } catch (e: Throwable) {
            "https://sample-project.supabase.co"
        }

        val key: String = try {
            val configKey = BuildConfig.SUPABASE_ANON_KEY
            if (configKey.isNullOrBlank()) "sample_anon_key" else configKey
        } catch (e: Throwable) {
            "sample_anon_key"
        }

        Log.i(TAG, "Configured Supabase Client for URL: $url")
        createSupabaseClient(
            supabaseUrl = url,
            supabaseKey = key
        ) {
            install(Postgrest)
            install(Realtime)
            install(Auth)
        }
    }

    val supabase: SupabaseClient get() = client
}
