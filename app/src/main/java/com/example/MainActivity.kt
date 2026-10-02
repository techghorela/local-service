package com.example

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.example.data.auth.AuthRepository
import com.example.data.local.AppDatabase
import com.example.data.local.UserPreferences
import com.example.data.remote.SupabaseClientProvider
import com.example.data.remote.SupabaseRepository
import com.example.data.repository.BookingRepository
import com.example.data.repository.ServiceRepository
import com.example.data.util.BookingStatusSimulator
import com.example.ui.navigation.AppNavGraph
import com.example.ui.theme.MyApplicationTheme
import com.example.util.RazorpayManager
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity(), PaymentResultWithDataListener {

    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 1. Initialize Razorpay safely
        try {
            RazorpayManager.init(this)
        } catch (t: Throwable) {
            Log.w(TAG, "Razorpay init caught: ${t.message}")
        }

        // 2. Local database & Preferences
        val database = AppDatabase.getDatabase(applicationContext)
        val userPreferences = UserPreferences(applicationContext)

        // 3. Supabase & Auth Repositories
        val supabase = SupabaseClientProvider.client
        val supabaseRepository = SupabaseRepository(client = supabase, bookingDao = database.bookingDao())
        val authRepository = AuthRepository(userPreferences)

        val serviceRepository = ServiceRepository(database.serviceDao())
        val bookingRepository = BookingRepository(database.bookingDao(), supabaseRepository)

        // 4. Sync pending bookings on startup
        lifecycleScope.launch {
            try {
                val syncedCount = bookingRepository.syncPendingWithCloud()
                Log.i(TAG, "Synced $syncedCount pending bookings with Supabase on startup")
            } catch (e: Exception) {
                Log.w(TAG, "Startup cloud sync error: ${e.message}")
            }
        }

        // 5. Start demo booking status simulator
        try {
            val simulator = BookingStatusSimulator(database.bookingDao(), supabaseRepository, lifecycleScope)
            simulator.startSimulation()
        } catch (t: Throwable) {
            Log.w(TAG, "Booking simulator warning: ${t.message}")
        }

        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    AppNavGraph(
                        navController = navController,
                        serviceRepository = serviceRepository,
                        bookingRepository = bookingRepository,
                        supabaseRepository = supabaseRepository,
                        authRepository = authRepository,
                        userPreferences = userPreferences
                    )
                }
            }
        }
    }

    override fun onPaymentSuccess(razorpayPaymentId: String?, paymentData: PaymentData?) {
        Log.i(TAG, "Razorpay payment success: $razorpayPaymentId")
        RazorpayManager.handleSuccess(razorpayPaymentId, paymentData)
    }

    override fun onPaymentError(code: Int, response: String?, paymentData: PaymentData?) {
        Log.w(TAG, "Razorpay payment error: code=$code, response=$response")
        RazorpayManager.handleError(code, response, paymentData)
    }
}

