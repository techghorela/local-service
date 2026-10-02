package com.example.ui.screens

import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.HisarBlock
import com.example.data.model.TimeSlot
import com.example.ui.components.ServiceIconHelper
import com.example.ui.theme.BrandSuccess
import com.example.ui.theme.SwiggyOrange
import com.example.ui.theme.SwiggyOrangeDark
import com.example.viewmodel.BookingViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingScreen(
    bookingViewModel: BookingViewModel,
    onBackClick: () -> Unit,
    onNavigateToMyBookings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by bookingViewModel.uiState.collectAsState()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = if (state.currentStep == 4) "Booking Confirmed!" else "Book ${state.service?.name ?: "Service"}",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp
                            )
                        )
                        if (state.currentStep < 4) {
                            Text(
                                text = "Step ${state.currentStep} of 3",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 12.sp,
                                    color = SwiggyOrange,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            if (state.currentStep > 1 && state.currentStep < 4) {
                                bookingViewModel.goToPreviousStep()
                            } else {
                                onBackClick()
                            }
                        },
                        modifier = Modifier.testTag("booking_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            if (state.currentStep < 4) {
                Surface(
                    color = Color.White,
                    shadowElevation = 8.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Estimated Total",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 11.sp,
                                    color = Color(0xFF64748B)
                                )
                            )
                            Text(
                                text = state.service?.price ?: "₹0",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 18.sp,
                                    color = SwiggyOrange
                                )
                            )
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (state.currentStep == 1) {
                                OutlinedButton(
                                    onClick = { bookingViewModel.continueWithDeferredValues() },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF2563EB)),
                                    border = BorderStroke(1.dp, Color(0xFF93C5FD)),
                                    modifier = Modifier
                                        .height(48.dp)
                                        .testTag("btn_skip_store_later_bottom")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.FlashOn,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Store Later »",
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp
                                        )
                                    )
                                }
                            }

                            Button(
                                onClick = {
                                    if (state.currentStep < 3) {
                                        bookingViewModel.goToNextStep()
                                    } else if (state.currentStep == 3) {
                                        bookingViewModel.confirmBooking()
                                    }
                                },
                                enabled = !state.isSubmitting,
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = SwiggyOrange),
                                modifier = Modifier
                                    .height(48.dp)
                                    .testTag("booking_primary_action_btn")
                            ) {
                                if (state.isSubmitting) {
                                    CircularProgressIndicator(
                                        color = Color.White,
                                        modifier = Modifier.size(22.dp)
                                    )
                                } else {
                                    Text(
                                        text = when (state.currentStep) {
                                            1 -> "Next: Schedule"
                                            2 -> "Review Booking"
                                            else -> if (state.isDetailsDeferred) "Confirm & Store Later" else "Confirm Booking"
                                        },
                                        style = MaterialTheme.typography.labelLarge.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = Color.White
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF8F9FA))
        ) {
            // Step Progress Indicator (1/3, 2/3, 3/3)
            if (state.currentStep < 4) {
                BookingProgressIndicator(currentStep = state.currentStep)
            }

            AnimatedContent(
                targetState = state.currentStep,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "bookingStepAnimation",
                modifier = Modifier.fillMaxSize()
            ) { step ->
                when (step) {
                    1 -> BookingStep1(
                        state = state,
                        onNameChange = { bookingViewModel.updateName(it) },
                        onPhoneChange = { bookingViewModel.updatePhone(it) },
                        onAddressChange = { bookingViewModel.updateAddress(it) },
                        onBlockChange = { bookingViewModel.updateBlock(it) },
                        onContinueWithDeferredValues = { bookingViewModel.continueWithDeferredValues() }
                    )
                    2 -> BookingStep2(
                        state = state,
                        onDateChange = { bookingViewModel.updateDate(it) },
                        onTimeSlotChange = { bookingViewModel.updateTimeSlot(it) },
                        onFlexibleScheduleChange = { bookingViewModel.setFlexibleSchedule(it) },
                        onNotesChange = { bookingViewModel.updateNotes(it) }
                    )
                    3 -> BookingStep3(
                        state = state,
                        onEditStep1 = { bookingViewModel.goToPreviousStep(); bookingViewModel.goToPreviousStep() },
                        onEditStep2 = { bookingViewModel.goToPreviousStep() }
                    )
                    else -> BookingSuccessView(
                        state = state,
                        onNotifyWhatsApp = {
                            try {
                                val intent = Intent(Intent.ACTION_VIEW).apply {
                                    data = Uri.parse(state.whatsAppIntentUri)
                                }
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                Toast.makeText(context, "Could not open WhatsApp. Booking is already saved!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        onViewBookings = onNavigateToMyBookings
                    )
                }
            }
        }
    }
}

@Composable
fun BookingProgressIndicator(currentStep: Int) {
    Surface(
        color = Color.White,
        shadowElevation = 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            StepItem(step = 1, title = "Details", isCurrent = currentStep == 1, isDone = currentStep > 1)
            StepConnector(isDone = currentStep > 1)
            StepItem(step = 2, title = "Schedule", isCurrent = currentStep == 2, isDone = currentStep > 2)
            StepConnector(isDone = currentStep > 2)
            StepItem(step = 3, title = "Confirm", isCurrent = currentStep == 3, isDone = currentStep > 3)
        }
    }
}

@Composable
fun StepItem(step: Int, title: String, isCurrent: Boolean, isDone: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(
                    when {
                        isDone -> BrandSuccess
                        isCurrent -> SwiggyOrange
                        else -> Color(0xFFE2E8F0)
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isDone) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Completed",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            } else {
                Text(
                    text = step.toString(),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (isCurrent) Color.White else Color(0xFF64748B)
                    )
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 11.sp,
                fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                color = if (isCurrent) SwiggyOrange else Color(0xFF64748B)
            )
        )
    }
}

@Composable
fun StepConnector(isDone: Boolean) {
    Box(
        modifier = Modifier
            .width(48.dp)
            .height(2.dp)
            .background(if (isDone) BrandSuccess else Color(0xFFCBD5E1))
    )
}

// -------------------------------------------------------------
// STEP 1: Details (Name, Phone, Address, Block Selector)
// -------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingStep1(
    state: com.example.viewmodel.BookingUiState,
    onNameChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onAddressChange: (String) -> Unit,
    onBlockChange: (String) -> Unit,
    onContinueWithDeferredValues: () -> Unit
) {
    var blockDropdownExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Values not available callout card
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)),
            border = BorderStroke(1.dp, Color(0xFFBFDBFE)),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("deferred_values_banner")
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.FlashOn,
                        contentDescription = null,
                        tint = Color(0xFF2563EB),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Values not available right now? (विवरण बाद में भरें)",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E40AF),
                            fontSize = 13.sp
                        )
                    )
                }
                Text(
                    text = "No address or phone right now? You can continue directly! We will save your booking slot, and you can store or update these values anytime later from 'My Bookings'.",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color(0xFF1E3A8A),
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                )
                Button(
                    onClick = onContinueWithDeferredValues,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                    modifier = Modifier
                        .height(36.dp)
                        .testTag("btn_continue_store_later")
                ) {
                    Text("Continue & Store Later »", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Selected service summary card
        state.service?.let { service ->
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(SwiggyOrange.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = ServiceIconHelper.getIconForName(service.iconName),
                            contentDescription = service.name,
                            tint = SwiggyOrange,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = service.name,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        )
                        Text(
                            text = service.category,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        )
                    }

                    Text(
                        text = service.price,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = SwiggyOrange
                        )
                    )
                }
            }
        }

        // Form Fields
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Customer Information",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color(0xFF1E293B)
                    )
                )

                // Name
                OutlinedTextField(
                    value = state.customerName,
                    onValueChange = onNameChange,
                    label = { Text("Full Name") },
                    leadingIcon = {
                        Icon(Icons.Default.Person, contentDescription = null, tint = SwiggyOrange)
                    },
                    isError = state.nameError != null,
                    supportingText = state.nameError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SwiggyOrange,
                        focusedLabelColor = SwiggyOrange
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("booking_name_input")
                )

                // Phone
                OutlinedTextField(
                    value = state.customerPhone,
                    onValueChange = onPhoneChange,
                    label = { Text("Phone Number") },
                    placeholder = { Text("10-digit mobile") },
                    leadingIcon = {
                        Icon(Icons.Default.Phone, contentDescription = null, tint = SwiggyOrange)
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    isError = state.phoneError != null,
                    supportingText = state.phoneError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SwiggyOrange,
                        focusedLabelColor = SwiggyOrange
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("booking_phone_input")
                )

                // Hisar Block selector dropdown
                ExposedDropdownMenuBox(
                    expanded = blockDropdownExpanded,
                    onExpandedChange = { blockDropdownExpanded = it },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = state.selectedBlock,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Hisar Block / Tehsil") },
                        leadingIcon = {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = SwiggyOrange)
                        },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = blockDropdownExpanded) },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SwiggyOrange,
                            focusedLabelColor = SwiggyOrange
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                            .testTag("booking_block_dropdown")
                    )

                    ExposedDropdownMenu(
                        expanded = blockDropdownExpanded,
                        onDismissRequest = { blockDropdownExpanded = false }
                    ) {
                        HisarBlock.allNames.forEach { block ->
                            DropdownMenuItem(
                                text = { Text(block) },
                                onClick = {
                                    onBlockChange(block)
                                    blockDropdownExpanded = false
                                },
                                modifier = Modifier.testTag("block_option_$block")
                            )
                        }
                    }
                }

                // Full Address
                OutlinedTextField(
                    value = state.customerAddress,
                    onValueChange = onAddressChange,
                    label = { Text("House / Flat No., Street, Colony") },
                    placeholder = { Text("e.g., House 142, Urban Estate II, Hisar") },
                    leadingIcon = {
                        Icon(Icons.Default.Home, contentDescription = null, tint = SwiggyOrange)
                    },
                    minLines = 2,
                    isError = state.addressError != null,
                    supportingText = state.addressError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SwiggyOrange,
                        focusedLabelColor = SwiggyOrange
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("booking_address_input")
                )
            }
        }
    }
}

// -------------------------------------------------------------
// STEP 2: Date Picker & Time Slot Selector
// -------------------------------------------------------------
@Composable
fun BookingStep2(
    state: com.example.viewmodel.BookingUiState,
    onDateChange: (String) -> Unit,
    onTimeSlotChange: (TimeSlot) -> Unit,
    onFlexibleScheduleChange: (Boolean) -> Unit,
    onNotesChange: (String) -> Unit
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)

    // Helper to open standard Android DatePickerDialog
    fun showDatePicker() {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val selectedCalendar = Calendar.getInstance()
                selectedCalendar.set(year, month, dayOfMonth)
                onDateChange(dateFormat.format(selectedCalendar.time))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    // Quick date options
    val todayCal = Calendar.getInstance()
    val todayStr = dateFormat.format(todayCal.time)
    todayCal.add(Calendar.DAY_OF_YEAR, 1)
    val tomorrowStr = dateFormat.format(todayCal.time)
    todayCal.add(Calendar.DAY_OF_YEAR, 1)
    val dayAfterTomorrowStr = dateFormat.format(todayCal.time)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Flexible timing card
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (state.isFlexibleSchedule) Color(0xFFEFF6FF) else Color.White
            ),
            border = BorderStroke(1.dp, if (state.isFlexibleSchedule) Color(0xFF3B82F6) else Color(0xFFE2E8F0)),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onFlexibleScheduleChange(!state.isFlexibleSchedule) }
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = null,
                    tint = if (state.isFlexibleSchedule) Color(0xFF2563EB) else Color(0xFF64748B),
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Time not decided yet? (Flexible Timing)",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = if (state.isFlexibleSchedule) Color(0xFF1E40AF) else Color(0xFF1E293B)
                        )
                    )
                    Text(
                        text = "Continue now; technician will call in advance to coordinate convenient timing.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 11.sp,
                            color = Color(0xFF64748B)
                        )
                    )
                }
                androidx.compose.material3.Switch(
                    checked = state.isFlexibleSchedule,
                    onCheckedChange = { onFlexibleScheduleChange(it) }
                )
            }
        }

        // Date Selection Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Select Service Date",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color(0xFF1E293B)
                    )
                )

                // Quick date chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val quickDates = listOf(
                        Pair("Today", todayStr),
                        Pair("Tomorrow", tomorrowStr),
                        Pair("In 2 Days", dayAfterTomorrowStr)
                    )

                    quickDates.forEach { (label, dateVal) ->
                        val isSelected = state.scheduledDate == dateVal
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) SwiggyOrange else Color(0xFFF1F5F9))
                                .clickable { onDateChange(dateVal) }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color.White else Color(0xFF475569)
                                    )
                                )
                                Text(
                                    text = dateVal.take(6),
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 10.sp,
                                        color = if (isSelected) Color.White.copy(alpha = 0.9f) else Color(0xFF64748B)
                                    )
                                )
                            }
                        }
                    }
                }

                // Date Picker trigger button
                OutlinedButton(
                    onClick = { showDatePicker() },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("open_date_picker_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = "Pick Date",
                        tint = SwiggyOrange
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Chosen Date: ${state.scheduledDate}",
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF1E293B)
                    )
                }
            }
        }

        // Time Slot Selection Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Select Time Slot",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color(0xFF1E293B)
                    )
                )

                TimeSlot.entries.forEach { slot ->
                    val isSelected = state.selectedTimeSlot == slot
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) Color(0xFFFFF7ED) else Color(0xFFF8FAFC))
                            .border(
                                width = if (isSelected) 1.5.dp else 1.dp,
                                color = if (isSelected) SwiggyOrange else Color(0xFFE2E8F0),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { onTimeSlotChange(slot) }
                            .padding(14.dp)
                            .testTag("time_slot_${slot.name}"),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) SwiggyOrange else Color(0xFFCBD5E1)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Schedule,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text(
                                    text = slot.title,
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = if (isSelected) SwiggyOrangeDark else Color(0xFF1E293B)
                                    )
                                )
                                Text(
                                    text = slot.timing,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 12.sp,
                                        color = Color(0xFF64748B)
                                    )
                                )
                            }
                        }

                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Selected",
                                tint = SwiggyOrange,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }

        // Additional Notes Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Special Instructions (Optional)",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color(0xFF1E293B)
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = state.notes,
                    onValueChange = onNotesChange,
                    placeholder = { Text("e.g. Bring extra ladder, call before arrival...") },
                    minLines = 2,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SwiggyOrange
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("booking_notes_input")
                )
            }
        }
    }
}

// -------------------------------------------------------------
// STEP 3: Review & Confirmation
// -------------------------------------------------------------
@Composable
fun BookingStep3(
    state: com.example.viewmodel.BookingUiState,
    onEditStep1: () -> Unit,
    onEditStep2: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Deferred values notice if applicable
        if (state.isDetailsDeferred || state.customerPhone == "Not Provided Yet") {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7)),
                border = BorderStroke(1.dp, Color(0xFFFDE68A)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("step3_deferred_notice")
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = Color(0xFFD97706),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Values Marked to Store Later",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF92400E),
                                fontSize = 13.sp
                            )
                        )
                        Text(
                            text = "Contact and address values were not available now. Your booking slot is reserved, and you can store or update these values anytime in 'My Bookings'.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFFB45309),
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }
        }

        // Guarantee banner
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFECFDF5), RoundedCornerShape(12.dp))
                .border(1.dp, Color(0xFFA7F3D0), RoundedCornerShape(12.dp))
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Shield,
                contentDescription = "Safe",
                tint = BrandSuccess,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = "Hisar District Service Protection",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color(0xFF065F46)
                    )
                )
                Text(
                    text = "Pay after service · Verified local professionals",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 11.sp,
                        color = Color(0xFF047857)
                    )
                )
            }
        }

        // Service & Pricing Summary
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Service Details",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = state.service?.name ?: "Service",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF334155)
                        )
                    )
                    Text(
                        text = state.service?.price ?: "₹0",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Visiting & Inspection Fee",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
                    )
                    Text(
                        text = "FREE in ${state.selectedBlock}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = BrandSuccess,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Color(0xFFE2E8F0))
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Total Payable After Service",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E293B)
                        )
                    )
                    Text(
                        text = state.service?.price ?: "₹0",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = SwiggyOrange
                        )
                    )
                }
            }
        }

        // Contact & Location Summary
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Customer & Location",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E293B)
                        )
                    )
                    IconButton(onClick = onEditStep1, modifier = Modifier.size(28.dp)) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit details",
                            tint = SwiggyOrange,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Text(
                    text = "${state.customerName} · ${state.customerPhone}",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF334155)
                    )
                )

                Text(
                    text = "${state.customerAddress}, Block: ${state.selectedBlock}, Hisar District",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color(0xFF64748B)
                    )
                )
            }
        }

        // Schedule Summary
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Appointment Schedule",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E293B)
                        )
                    )
                    IconButton(onClick = onEditStep2, modifier = Modifier.size(28.dp)) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit schedule",
                            tint = SwiggyOrange,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Text(
                    text = "Date: ${state.scheduledDate}",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF334155)
                    )
                )

                Text(
                    text = "Time: ${state.selectedTimeSlot.title} (${state.selectedTimeSlot.timing})",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color(0xFF64748B)
                    )
                )

                if (state.notes.isNotEmpty()) {
                    Text(
                        text = "Note: ${state.notes}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFF64748B),
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                        )
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// STEP 4: Success Screen with optional WhatsApp Intent
// -------------------------------------------------------------
@Composable
fun BookingSuccessView(
    state: com.example.viewmodel.BookingUiState,
    onNotifyWhatsApp: () -> Unit,
    onViewBookings: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Success Checkmark Icon
        Box(
            modifier = Modifier
                .size(76.dp)
                .clip(CircleShape)
                .background(Color(0xFFDCFCE7)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Booking Confirmed",
                tint = BrandSuccess,
                modifier = Modifier.size(46.dp)
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = "Booking Saved Successfully!",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.ExtraBold,
                fontSize = 22.sp,
                color = Color(0xFF0F172A)
            )
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Booking ID: #LSH-${state.createdBookingId ?: 1001}",
            style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.Bold,
                color = SwiggyOrange
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Your booking is stored locally on this device. A technician in ${state.selectedBlock} will be assigned shortly.",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 13.sp,
                color = Color(0xFF64748B)
            ),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        if (state.isDetailsDeferred || state.customerPhone == "Not Provided Yet") {
            Spacer(modifier = Modifier.height(14.dp))
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7)),
                border = BorderStroke(1.dp, Color(0xFFFDE68A)),
                modifier = Modifier.fillMaxWidth().testTag("success_deferred_reminder")
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = Color(0xFFD97706),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Address & contact are pending. Open 'My Bookings' anytime to store or edit these values before service.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 12.sp,
                            color = Color(0xFF92400E)
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // WhatsApp Notification Button (Optional Intent)
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Want instant WhatsApp confirmation?",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1B5E20)
                    )
                )
                Text(
                    text = "Notify the Hisar support desk via WhatsApp (+91 95883 23460). Optional — booking is already saved.",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 11.sp,
                        color = Color(0xFF2E7D32)
                    ),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    modifier = Modifier.padding(vertical = 6.dp)
                )

                Button(
                    onClick = onNotifyWhatsApp,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF25D366)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("whatsapp_notify_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "WhatsApp",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Notify on WhatsApp",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Navigation CTA to My Bookings
        Button(
            onClick = onViewBookings,
            colors = ButtonDefaults.buttonColors(containerColor = SwiggyOrange),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("view_my_bookings_cta")
        ) {
            Text(
                text = "View in My Bookings",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )
        }
    }
}
