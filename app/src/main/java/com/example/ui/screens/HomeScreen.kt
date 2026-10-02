package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RoomService
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ServiceEntity
import com.example.ui.components.CategoryChipsRow
import com.example.ui.components.PopularServicesSection
import com.example.ui.components.PromoCarousel
import com.example.ui.components.SearchBarClickable
import com.example.ui.components.ServiceGridCard
import com.example.ui.components.ShimmerGridSkeleton
import com.example.ui.components.StickyTopBarWithLocation
import com.example.ui.theme.BrandSuccess
import com.example.ui.theme.SwiggyOrange
import com.example.ui.theme.SwiggyOrangeDark
import com.example.viewmodel.HomeViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel,
    activeBookingsCount: Int,
    onNavigateToSearch: () -> Unit,
    onNavigateToCategory: (String) -> Unit,
    onNavigateToBooking: (ServiceEntity) -> Unit,
    onNavigateToMyBookings: () -> Unit,
    onNavigateToProfile: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by homeViewModel.uiState.collectAsState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var showOffersSheet by remember { mutableStateOf(false) }
    val offersSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Current bottom navigation selected item: 0=Home, 1=Bookings, 2=Offers, 3=Profile
    var selectedBottomNavIndex by remember { mutableStateOf(0) }

    Scaffold(
        topBar = {
            StickyTopBarWithLocation(
                selectedBlock = uiState.selectedBlock,
                onBlockSelected = { homeViewModel.onBlockSelected(it) },
                bookingBadgeCount = activeBookingsCount,
                onNotificationClick = {
                    Toast.makeText(context, "All services operating normally across Hisar District!", Toast.LENGTH_SHORT).show()
                },
                onBookingsClick = onNavigateToMyBookings
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp,
                modifier = Modifier.testTag("bottom_nav_bar")
            ) {
                // Home
                NavigationBarItem(
                    selected = selectedBottomNavIndex == 0,
                    onClick = { selectedBottomNavIndex = 0 },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.RoomService,
                            contentDescription = "Home"
                        )
                    },
                    label = { Text("Home", fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = SwiggyOrange,
                        selectedTextColor = SwiggyOrange,
                        indicatorColor = SwiggyOrange.copy(alpha = 0.15f)
                    ),
                    modifier = Modifier.testTag("nav_item_home")
                )

                // Bookings
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToMyBookings,
                    icon = {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Bookings"
                        )
                    },
                    label = { Text("Bookings") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = SwiggyOrange,
                        indicatorColor = SwiggyOrange.copy(alpha = 0.15f)
                    ),
                    modifier = Modifier.testTag("nav_item_bookings")
                )

                // Offers
                NavigationBarItem(
                    selected = false,
                    onClick = { showOffersSheet = true },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.LocalOffer,
                            contentDescription = "Offers"
                        )
                    },
                    label = { Text("Offers") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = SwiggyOrange,
                        indicatorColor = SwiggyOrange.copy(alpha = 0.15f)
                    ),
                    modifier = Modifier.testTag("nav_item_offers")
                )

                // Profile
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToProfile,
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profile"
                        )
                    },
                    label = { Text("Profile") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = SwiggyOrange,
                        indicatorColor = SwiggyOrange.copy(alpha = 0.15f)
                    ),
                    modifier = Modifier.testTag("nav_item_profile")
                )
            }
        },
        floatingActionButton = {
            // Floating Action Button: 'Quick Book' / 'WhatsApp Help' (links to WhatsApp)
            ExtendedFloatingActionButton(
                onClick = {
                    try {
                        val uri = Uri.parse("https://wa.me/919588323460?text=Hi+Local+Service+Hub%2C+I+need+quick+home+service+in+Hisar.")
                        val intent = Intent(Intent.ACTION_VIEW, uri)
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        Toast.makeText(context, "Opening WhatsApp Help...", Toast.LENGTH_SHORT).show()
                    }
                },
                containerColor = Color(0xFF25D366),
                contentColor = Color.White,
                elevation = FloatingActionButtonDefaults.elevation(6.dp),
                icon = {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "WhatsApp Help",
                        modifier = Modifier.size(20.dp)
                    )
                },
                text = {
                    Text(
                        text = "WhatsApp Help",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                },
                modifier = Modifier.testTag("fab_whatsapp_help")
            )
        },
        modifier = modifier
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF8F9FA))
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(bottom = 80.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("home_service_grid")
            ) {
                // 1. Search Bar Header
                item(span = { GridItemSpan(2) }) {
                    Box(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 12.dp)) {
                        SearchBarClickable(
                            onClick = onNavigateToSearch,
                            placeholder = "Search 'AC repair', 'Plumber', 'Salon'..."
                        )
                    }
                }

                // Test App Mode: Inquiries & System Audit Shortcut
                item(span = { GridItemSpan(2) }) {
                    Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFEFF6FF),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBFDBFE)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onNavigateToMyBookings() }
                                .testTag("home_test_app_banner")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFFDBEAFE)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ElectricBolt,
                                            contentDescription = null,
                                            tint = Color(0xFF1D4ED8),
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = "Test App: Inquiries & System Audit",
                                            style = MaterialTheme.typography.titleSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF1E3A8A),
                                                fontSize = 13.sp
                                            )
                                        )
                                        Text(
                                            text = "Audit lifecycles, test payments, and check 0 errors",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = Color(0xFF3B82F6),
                                                fontSize = 11.sp
                                            )
                                        )
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(0xFF1D4ED8)
                                ) {
                                    Text(
                                        text = "Open Hub",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        ),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // 2. Promotional Auto-scrolling Carousel
                item(span = { GridItemSpan(2) }) {
                    Box(modifier = Modifier.padding(top = 10.dp)) {
                        PromoCarousel(
                            onBannerClick = { category ->
                                onNavigateToCategory(category)
                            }
                        )
                    }
                }

                // 3. Category Filter Chips Row
                item(span = { GridItemSpan(2) }) {
                    Column {
                        Text(
                            text = "Browse Categories",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Color(0xFF0F172A)
                            ),
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                        )
                        CategoryChipsRow(
                            selectedCategory = uiState.selectedCategory,
                            onCategorySelected = { homeViewModel.onCategorySelected(it) }
                        )
                    }
                }

                // 4. Popular Services Horizontal Section
                if (uiState.popularServices.isNotEmpty()) {
                    item(span = { GridItemSpan(2) }) {
                        PopularServicesSection(
                            services = uiState.popularServices,
                            onServiceClick = { service -> onNavigateToBooking(service) },
                            onSeeAllClick = { onNavigateToCategory("Appliance Repair") },
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }

                // 5. Grid Section Header with Service Count & Refresh
                item(span = { GridItemSpan(2) }) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (uiState.selectedCategory.isEmpty()) "All Services in ${uiState.selectedBlock}" else "${uiState.selectedCategory} Services",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp,
                                    color = Color(0xFF0F172A)
                                )
                            )
                            Text(
                                text = "${uiState.filteredServices.size} expert services available",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 12.sp,
                                    color = Color(0xFF64748B)
                                )
                            )
                        }

                        IconButton(
                            onClick = {
                                Toast.makeText(context, "Updated service rates for ${uiState.selectedBlock}", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.testTag("refresh_home_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Refresh",
                                tint = SwiggyOrange
                            )
                        }
                    }
                }

                // 6. Shimmer Loading or Service Cards (2-column grid)
                if (uiState.isLoading) {
                    item(span = { GridItemSpan(2) }) {
                        ShimmerGridSkeleton()
                    }
                } else {
                    items(uiState.filteredServices, key = { it.id }) { service ->
                        Box(
                            modifier = Modifier.padding(
                                start = if (uiState.filteredServices.indexOf(service) % 2 == 0) 16.dp else 0.dp,
                                end = if (uiState.filteredServices.indexOf(service) % 2 == 1) 16.dp else 0.dp
                            )
                        ) {
                            ServiceGridCard(
                                service = service,
                                onClick = { onNavigateToCategory(service.category) },
                                onBookClick = { onNavigateToBooking(service) }
                            )
                        }
                    }
                }
            }

            // Offers bottom sheet dialog
            if (showOffersSheet) {
                OffersBottomSheet(
                    sheetState = offersSheetState,
                    onDismiss = {
                        coroutineScope.launch { offersSheetState.hide() }
                            .invokeOnCompletion { showOffersSheet = false }
                    }
                )
            }
        }
    }
}
