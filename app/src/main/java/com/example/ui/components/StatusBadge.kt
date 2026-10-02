package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BrandError
import com.example.ui.theme.BrandSuccess
import com.example.ui.theme.BrandWarning
import com.example.ui.theme.SwiggyOrange

@Composable
fun StatusBadge(
    status: String,
    modifier: Modifier = Modifier
) {
    val (backgroundColor, textColor) = when (status.lowercase()) {
        "confirmed" -> Pair(Color(0xFFE0F2FE), Color(0xFF0369A1))
        "completed" -> Pair(Color(0xFFDCFCE7), BrandSuccess)
        "cancelled" -> Pair(Color(0xFFFEE2E2), BrandError)
        else -> Pair(Color(0xFFFEF3C7), Color(0xFFB45309)) // Pending
    }

    Box(
        modifier = modifier
            .background(backgroundColor, RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = status.uppercase(),
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        )
    }
}
