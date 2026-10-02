package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.HomeRepairService
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.SwiggyOrange

data class CategoryChipItem(
    val name: String,
    val icon: ImageVector
)

@Composable
fun CategoryChipsRow(
    selectedCategory: String,
    onCategorySelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val categories = listOf(
        CategoryChipItem("All Services", Icons.Default.GridView),
        CategoryChipItem("Appliance Repair", Icons.Default.HomeRepairService),
        CategoryChipItem("Home Maintenance", Icons.Default.CleaningServices),
        CategoryChipItem("Construction", Icons.Default.Build),
        CategoryChipItem("Beauty & Salon", Icons.Default.Face)
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .testTag("category_chips_row"),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        categories.forEach { item ->
            val isSelected = (item.name == "All Services" && selectedCategory.isEmpty()) ||
                    item.name.equals(selectedCategory, ignoreCase = true)

            val backgroundColor by animateColorAsState(
                targetValue = if (isSelected) SwiggyOrange else Color.White,
                label = "chipBg"
            )
            val contentColor by animateColorAsState(
                targetValue = if (isSelected) Color.White else Color(0xFF334155),
                label = "chipContent"
            )
            val borderColor = if (isSelected) SwiggyOrange else Color(0xFFE2E8F0)

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(24.dp))
                    .background(backgroundColor)
                    .border(1.dp, borderColor, RoundedCornerShape(24.dp))
                    .clickable {
                        if (item.name == "All Services") {
                            onCategorySelected("")
                        } else {
                            onCategorySelected(item.name)
                        }
                    }
                    .padding(horizontal = 14.dp, vertical = 9.dp)
                    .testTag("category_chip_${item.name.replace(" ", "_")}")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.name,
                        tint = contentColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = contentColor
                        )
                    )
                }
            }
        }
    }
}
