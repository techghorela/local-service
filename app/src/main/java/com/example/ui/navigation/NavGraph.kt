package com.example.ui.navigation

import android.app.Activity
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.data.auth.AuthRepository
import com.example.data.local.UserPreferences
import com.example.data.remote.SupabaseRepository
import com.example.data.repository.BookingRepository
import com.example.data.repository.ServiceRepository
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.BookingScreen
import com.example.ui.screens.CategoryScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.MyBookingsScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.SearchScreen
import com.example.ui.screens.TrackingScreen
import com.example.util.RazorpayManager
import com.example.viewmodel.AuthViewModel
import com.example.viewmodel.BookingViewModel
import com.example.viewmodel.BookingViewModelFactory
import com.example.viewmodel.HomeViewModel
import com.example.viewmodel.HomeViewModelFactory
import com.example.viewmodel.MyBookingsViewModel
import com.example.viewmodel.MyBookingsViewModelFactory
import com.example.viewmodel.ProfileViewModel
import com.example.viewmodel.ProfileViewModelFactory
import com.example.viewmodel.SearchViewModel
import com.example.viewmodel.SearchViewModelFactory
import java.net.URLDecoder
import java.net.URLEncoder

object NavRoutes {
    const val HOME = "home"
    const val CATEGORY = "category/{categoryName}"
    const val SEARCH = "search"
    const val BOOKING = "booking/{serviceId}"
    const val MY_BOOKINGS = "my_bookings"
    const val PROFILE = "profile"
    const val AUTH = "auth"
    const val TRACKING = "tracking/{bookingId}"

    fun categoryRoute(categoryName: String): String {
        val encoded = URLEncoder.encode(categoryName, "UTF-8")
        return "category/$encoded"
    }

    fun bookingRoute(serviceId: String): String {
        return "booking/$serviceId"
    }

    fun trackingRoute(bookingId: Long): String {
        return "tracking/$bookingId"
    }
}

@Composable
fun AppNavGraph(
    navController: NavHostController,
    serviceRepository: ServiceRepository,
    bookingRepository: BookingRepository,
    supabaseRepository: SupabaseRepository,
    authRepository: AuthRepository,
    userPreferences: UserPreferences,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activity = context as? Activity

    val homeViewModel: HomeViewModel = viewModel(
        factory = HomeViewModelFactory(serviceRepository, userPreferences)
    )
    val bookingViewModel: BookingViewModel = viewModel(
        factory = BookingViewModelFactory(bookingRepository, serviceRepository, userPreferences)
    )
    val myBookingsViewModel: MyBookingsViewModel = viewModel(
        factory = MyBookingsViewModelFactory(bookingRepository)
    )
    val profileViewModel: ProfileViewModel = viewModel(
        factory = ProfileViewModelFactory(userPreferences, authRepository)
    )
    val searchViewModel: SearchViewModel = viewModel(
        factory = SearchViewModelFactory(serviceRepository)
    )
    val authViewModel = viewModel<AuthViewModel>(
        factory = object : androidx.lifecycle.ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return AuthViewModel(authRepository, userPreferences) as T
            }
        }
    )

    val allBookings by myBookingsViewModel.bookings.collectAsState()
    val activeBookingsCount = allBookings.count { it.status.equals("Pending", ignoreCase = true) || it.status.equals("Confirmed", ignoreCase = true) }
    val userEmail by profileViewModel.userEmail.collectAsState()

    NavHost(
        navController = navController,
        startDestination = NavRoutes.HOME,
        modifier = modifier
    ) {
        composable(NavRoutes.HOME) {
            HomeScreen(
                homeViewModel = homeViewModel,
                activeBookingsCount = activeBookingsCount,
                onNavigateToSearch = { navController.navigate(NavRoutes.SEARCH) },
                onNavigateToCategory = { categoryName ->
                    navController.navigate(NavRoutes.categoryRoute(categoryName))
                },
                onNavigateToBooking = { service ->
                    bookingViewModel.resetBooking(service)
                    navController.navigate(NavRoutes.bookingRoute(service.id))
                },
                onNavigateToMyBookings = { navController.navigate(NavRoutes.MY_BOOKINGS) },
                onNavigateToProfile = { navController.navigate(NavRoutes.PROFILE) }
            )
        }

        composable(
            route = NavRoutes.CATEGORY,
            arguments = listOf(navArgument("categoryName") { type = NavType.StringType })
        ) { backStackEntry ->
            val rawCategory = backStackEntry.arguments?.getString("categoryName") ?: ""
            val categoryName = try {
                URLDecoder.decode(rawCategory, "UTF-8")
            } catch (e: Exception) {
                rawCategory
            }
            CategoryScreen(
                initialCategory = categoryName,
                serviceRepository = serviceRepository,
                onBackClick = { navController.popBackStack() },
                onBookService = { service ->
                    bookingViewModel.resetBooking(service)
                    navController.navigate(NavRoutes.bookingRoute(service.id))
                }
            )
        }

        composable(NavRoutes.SEARCH) {
            SearchScreen(
                searchViewModel = searchViewModel,
                onBackClick = { navController.popBackStack() },
                onServiceSelected = { service ->
                    bookingViewModel.resetBooking(service)
                    navController.navigate(NavRoutes.bookingRoute(service.id))
                }
            )
        }

        composable(
            route = NavRoutes.BOOKING,
            arguments = listOf(navArgument("serviceId") { type = NavType.StringType })
        ) { backStackEntry ->
            val serviceId = backStackEntry.arguments?.getString("serviceId") ?: ""
            if (bookingViewModel.uiState.value.service?.id != serviceId) {
                bookingViewModel.loadServiceById(serviceId)
            }
            BookingScreen(
                bookingViewModel = bookingViewModel,
                onBackClick = { navController.popBackStack() },
                onNavigateToMyBookings = {
                    navController.navigate(NavRoutes.MY_BOOKINGS) {
                        popUpTo(NavRoutes.HOME)
                    }
                }
            )
        }

        composable(NavRoutes.MY_BOOKINGS) {
            MyBookingsScreen(
                viewModel = myBookingsViewModel,
                onRepeatBooking = { booking ->
                    bookingViewModel.resetBooking()
                    bookingViewModel.loadServiceById(booking.serviceName.lowercase().replace(" ", "_"))
                    bookingViewModel.updateName(booking.name)
                    bookingViewModel.updatePhone(booking.phone)
                    bookingViewModel.updateAddress(booking.address)
                    bookingViewModel.updateBlock(booking.block)
                    navController.navigate(NavRoutes.bookingRoute(booking.serviceName.lowercase().replace(" ", "_")))
                },
                onTrackBooking = { booking ->
                    navController.navigate(NavRoutes.trackingRoute(booking.id))
                },
                onPayBooking = { booking ->
                    if (activity != null) {
                        RazorpayManager.startPayment(
                            activity = activity,
                            booking = booking,
                            userEmail = userEmail,
                            onSuccess = { bookingId, paymentId ->
                                myBookingsViewModel.updatePaymentSuccess(bookingId, paymentId)
                                Toast.makeText(context, "Payment Successful! ID: $paymentId", Toast.LENGTH_LONG).show()
                            },
                            onError = { _, message ->
                                Toast.makeText(context, "Payment failed/cancelled: $message", Toast.LENGTH_SHORT).show()
                            }
                        )
                    } else {
                        Toast.makeText(context, "Unable to initiate payment", Toast.LENGTH_SHORT).show()
                    }
                },
                onBackClick = { navController.popBackStack() },
                onExploreServices = {
                    navController.navigate(NavRoutes.HOME) {
                        popUpTo(NavRoutes.HOME) { inclusive = true }
                    }
                }
            )
        }

        composable(NavRoutes.PROFILE) {
            ProfileScreen(
                profileViewModel = profileViewModel,
                onBackClick = { navController.popBackStack() },
                onSignInClick = { navController.navigate(NavRoutes.AUTH) }
            )
        }

        composable(NavRoutes.AUTH) {
            AuthScreen(
                authViewModel = authViewModel,
                onAuthSuccess = { navController.popBackStack() },
                onSkipGuest = { navController.popBackStack() }
            )
        }

        composable(
            route = NavRoutes.TRACKING,
            arguments = listOf(navArgument("bookingId") { type = NavType.LongType })
        ) { backStackEntry ->
            val bookingId = backStackEntry.arguments?.getLong("bookingId") ?: 0L
            val booking = allBookings.find { it.id == bookingId }
            if (booking != null) {
                TrackingScreen(
                    booking = booking,
                    supabaseRepository = supabaseRepository,
                    onBack = { navController.popBackStack() }
                )
            } else {
                navController.popBackStack()
            }
        }
    }
}
