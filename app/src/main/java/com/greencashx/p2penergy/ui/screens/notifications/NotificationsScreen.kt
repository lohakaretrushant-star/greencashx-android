package com.greencashx.p2penergy.ui.screens.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.greencashx.p2penergy.data.remote.dto.NotificationDto
import com.greencashx.p2penergy.ui.screens.dashboard.GreenCashXBottomBar
import com.greencashx.p2penergy.ui.viewmodel.NotificationsViewModel
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

private val GreenPrimary = Color(0xFF69F0AE)
private val CardBg       = Color(0xFFFFFFFF)
private val PageBg       = Color(0xFFF4F6F8)
private val TextPrimary  = Color(0xFF1C1E21)
private val TextMuted    = Color(0xFF65676B)
private val UnreadBorder = Color(0xFF69F0AE)
private val ReadBorder   = Color(0xFFE0E3E7)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    navController: NavController,
    viewModel: NotificationsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let {
            snackbarHostState.showSnackbar(it, duration = SnackbarDuration.Short)
            viewModel.clearMessages()
        }
    }
    LaunchedEffect(state.successMessage) {
        state.successMessage?.let {
            snackbarHostState.showSnackbar(it, duration = SnackbarDuration.Short)
            viewModel.clearMessages()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Notifications, contentDescription = null,
                            tint = GreenPrimary, modifier = Modifier.size(22.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Notifications", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextPrimary)
                        if (state.unreadCount > 0) {
                            Spacer(Modifier.width(8.dp))
                            Badge(containerColor = GreenPrimary) {
                                Text("${state.unreadCount}", color = Color.Black, fontSize = 11.sp)
                            }
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                    }
                },
                actions = {
                    if (state.unreadCount > 0) {
                        IconButton(onClick = { viewModel.markAllRead() }) {
                            Icon(Icons.Default.DoneAll, contentDescription = "Mark all read", tint = GreenPrimary)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CardBg)
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = { GreenCashXBottomBar(navController, currentRoute = "") },
        containerColor = PageBg
    ) { padding ->

        if (state.isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = GreenPrimary)
            }
        } else if (state.notifications.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Notifications, contentDescription = null,
                        tint = TextMuted, modifier = Modifier.size(48.dp))
                    Spacer(Modifier.height(12.dp))
                    Text("No notifications yet", color = TextMuted, fontSize = 15.sp)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(state.notifications, key = { it.id }) { notification ->
                    NotificationCard(
                        notification = notification,
                        onClick = {
                            if (!notification.isRead) viewModel.markRead(notification.id)
                        }
                    )
                }
                item { Spacer(Modifier.height(80.dp)) }
            }
        }
    }
}

@Composable
private fun NotificationCard(
    notification: NotificationDto,
    onClick: () -> Unit
) {
    val borderColor = if (notification.isRead) ReadBorder else UnreadBorder

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (notification.isRead) CardBg else Color(0xFFF0FFF7)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (notification.isRead) 1.dp else 3.dp)
    ) {
        Row(Modifier.fillMaxWidth()) {
            // Left colored border for unread
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp))
                    .background(borderColor)
            )
            Column(modifier = Modifier.padding(12.dp).weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = notification.title,
                        fontWeight = if (notification.isRead) FontWeight.Normal else FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = TextPrimary,
                        modifier = Modifier.weight(1f)
                    )
                    notification.createdAt?.let { ts ->
                        Text(
                            text = formatTimestamp(ts),
                            fontSize = 11.sp,
                            color = TextMuted,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    text = notification.body,
                    fontSize = 13.sp,
                    color = TextMuted,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
                if (!notification.isRead) {
                    Spacer(Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(RoundedCornerShape(50))
                                .background(GreenPrimary)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text("New", fontSize = 11.sp, color = GreenPrimary, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }
    }
}

private fun formatTimestamp(ts: String): String {
    return try {
        val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        parser.timeZone = TimeZone.getTimeZone("UTC")
        val date = parser.parse(ts) ?: return ""
        val diff = System.currentTimeMillis() - date.time
        when {
            diff < 60_000     -> "just now"
            diff < 3_600_000  -> "${diff / 60_000}m ago"
            diff < 86_400_000 -> "${diff / 3_600_000}h ago"
            else              -> "${diff / 86_400_000}d ago"
        }
    } catch (_: Exception) { "" }
}
