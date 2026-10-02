package com.example.ui.components

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.SwiggyOrange
import com.example.ui.theme.SwiggyOrangeDark
import kotlinx.coroutines.delay

data class PromoBannerItem(
    val title: String,
    val subtitle: String,
    val tag: String,
    val categoryTarget: String,
    val imageResId: Int,
    val bgGradient: List<Color>
)

@Composable
fun PromoCarousel(
    onBannerClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val banners = listOf(
        PromoBannerItem(
            title = "AC Mega Service Fest",
            subtitle = "Complete jet wash & gas check starting ₹399 in Hisar",
            tag = "FLAT 30% OFF",
            categoryTarget = "Appliance Repair",
            imageResId = R.drawable.banner_promo_1,
            bgGradient = listOf(Color(0xFFE65100), Color(0xFFFC8019))
        ),
        PromoBannerItem(
            title = "Reliable Home Fixes",
            subtitle = "Electrician & Plumber at your doorstep within 30 mins",
            tag = "STARTING ₹199",
            categoryTarget = "Home Maintenance",
            imageResId = R.drawable.banner_promo_2,
            bgGradient = listOf(Color(0xFF0F172A), Color(0xFF1E293B))
        ),
        PromoBannerItem(
            title = "Salon & Spa at Home",
            subtitle = "Facial, waxing & grooming by verified beauticians",
            tag = "HISAR SPECIAL",
            categoryTarget = "Beauty & Salon",
            imageResId = R.drawable.banner_promo_1,
            bgGradient = listOf(Color(0xFF701A75), Color(0xFF9333EA))
        )
    )

    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { banners.size }
    )

    // Auto-scroll effect
    LaunchedEffect(pagerState.currentPage) {
        delay(3800)
        val nextPage = (pagerState.currentPage + 1) % banners.size
        pagerState.animateScrollToPage(nextPage)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("promo_carousel")
    ) {
        HorizontalPager(
            state = pagerState,
            contentPadding = PaddingValues(horizontal = 16.dp),
            pageSpacing = 12.dp,
            modifier = Modifier.fillMaxWidth()
        ) { page ->
            val banner = banners[page]
            Card(
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                colors = CardDefaults.cardColors(containerColor = banner.bgGradient.first()),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clickable { onBannerClick(banner.categoryTarget) }
                    .testTag("promo_banner_$page")
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    // Background banner photo
                    Image(
                        painter = painterResource(id = banner.imageResId),
                        contentDescription = banner.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Gradient overlay to guarantee contrast
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        Color.Black.copy(alpha = 0.85f),
                                        Color.Black.copy(alpha = 0.55f),
                                        Color.Black.copy(alpha = 0.2f)
                                    )
                                )
                            )
                    )

                    // Content overlay
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Badge Tag
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(SwiggyOrange)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = banner.tag,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 11.sp
                                )
                            )
                        }

                        // Banner texts & CTA
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = banner.title,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 17.sp
                                    ),
                                    maxLines = 1
                                )
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = banner.subtitle,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Color(0xFFE2E8F0),
                                        fontSize = 12.sp
                                    ),
                                    maxLines = 2
                                )
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color.White),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ArrowForward,
                                    contentDescription = "Explore",
                                    tint = SwiggyOrangeDark,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Pager indicator dots
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(banners.size) { index ->
                val isSelected = pagerState.currentPage == index
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .height(6.dp)
                        .width(if (isSelected) 20.dp else 6.dp)
                        .clip(CircleShape)
                        .background(
                            if (isSelected) SwiggyOrange else Color(0xFFCBD5E1)
                        )
                )
            }
        }
    }
}
