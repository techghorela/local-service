package com.example.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.FaceRetouchingNatural
import androidx.compose.material.icons.filled.FormatPaint
import androidx.compose.material.icons.filled.Foundation
import androidx.compose.material.icons.filled.HomeRepairService
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.material.icons.filled.PestControl
import androidx.compose.material.icons.filled.Plumbing
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.ui.graphics.vector.ImageVector

object ServiceIconHelper {
    fun getIconForName(name: String): ImageVector {
        return when (name.lowercase()) {
            "ac_unit" -> Icons.Default.AcUnit
            "air" -> Icons.Default.Air
            "build" -> Icons.Default.Build
            "tv" -> Icons.Default.Tv
            "kitchen" -> Icons.Default.Kitchen
            "water_drop" -> Icons.Default.WaterDrop
            "bolt" -> Icons.Default.Bolt
            "plumbing" -> Icons.Default.Plumbing
            "pest_control" -> Icons.Default.PestControl
            "cleaning_services" -> Icons.Default.CleaningServices
            "foundation" -> Icons.Default.Foundation
            "engineering" -> Icons.Default.Engineering
            "format_paint" -> Icons.Default.FormatPaint
            "face_retouching_natural" -> Icons.Default.FaceRetouchingNatural
            "content_cut" -> Icons.Default.ContentCut
            else -> Icons.Default.HomeRepairService
        }
    }
}
