package com.greencashx.p2penergy.ui.screens.ai

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.greencashx.p2penergy.ui.theme.*
import com.greencashx.p2penergy.ui.viewmodel.AIChatViewModel
import com.greencashx.p2penergy.ui.viewmodel.ChatMessage
import kotlinx.coroutines.launch

private val predefinedQuestions = listOf(
    "How does this app work?",
    "How to do KYC verification?",
    "How much kW solar panel do I need?",
    "What is P2P energy trading?",
    "How to sell solar energy?",
    "What are carbon credits?",
    "Minimum solar investment?",
    "How is my energy measured?"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SolarChatScreen(
    navController: NavController,
    viewModel: AIChatViewModel = hiltViewModel()
) {
    val messages by viewModel.messages.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    // Auto-scroll to bottom when new message appears
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            scope.launch {
                listState.animateScrollToItem(messages.size - 1)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(GreenPrimary.copy(alpha = 0.2f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🌿", fontSize = 18.sp)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                "Solar AI Assistant",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = OnSurfaceLight
                            )
                            Text(
                                "Powered by GreenBot",
                                style = MaterialTheme.typography.labelSmall,
                                color = GreenPrimary
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = OnSurfaceLight
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SurfaceDark,
                    titleContentColor = OnSurfaceLight
                )
            )
        },
        containerColor = BackgroundDark
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Chat list area
            LazyColumn(
                state = listState,
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Welcome card shown only when no messages
                if (messages.isEmpty()) {
                    item {
                        WelcomeCard()
                    }
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Quick Questions",
                            style = MaterialTheme.typography.labelMedium,
                            color = OnSurfaceMuted,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                    item {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(predefinedQuestions) { question ->
                                QuestionChip(
                                    text = question,
                                    onClick = { viewModel.sendMessage(question) }
                                )
                            }
                        }
                    }
                }

                items(messages) { message ->
                    ChatBubble(message = message)
                }

                if (isLoading) {
                    item {
                        TypingIndicator()
                    }
                }
            }

            // Quick question chips (shown above input when there are messages)
            if (messages.isNotEmpty()) {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(SurfaceDark)
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(predefinedQuestions) { question ->
                        QuestionChip(
                            text = question,
                            onClick = { viewModel.sendMessage(question) }
                        )
                    }
                }
            }

            // Input row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceDark)
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = {
                        Text("Ask about solar energy...", color = OnSurfaceMuted, fontSize = 14.sp)
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GreenPrimary,
                        unfocusedBorderColor = CardDark,
                        focusedTextColor = OnSurfaceLight,
                        unfocusedTextColor = OnSurfaceLight,
                        cursorColor = GreenPrimary,
                        focusedContainerColor = CardDark,
                        unfocusedContainerColor = CardDark
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(onSend = {
                        if (inputText.isNotBlank()) {
                            viewModel.sendMessage(inputText)
                            inputText = ""
                        }
                    })
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        if (inputText.isNotBlank()) {
                            viewModel.sendMessage(inputText)
                            inputText = ""
                        }
                    },
                    enabled = inputText.isNotBlank() && !isLoading,
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            if (inputText.isNotBlank() && !isLoading) GreenPrimary
                            else CardDark,
                            CircleShape
                        )
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = if (inputText.isNotBlank() && !isLoading) Color.White else OnSurfaceMuted,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun WelcomeCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        shape = RoundedCornerShape(20.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(listOf(GreenPrimary.copy(alpha = 0.3f), EnergyBlue.copy(alpha = 0.2f))),
                    RoundedCornerShape(20.dp)
                )
                .padding(20.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Text("🌿", fontSize = 42.sp)
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    "Solar AI Assistant",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = OnSurfaceLight
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    "Ask me anything about solar energy, GreenCashX, KYC verification, P2P trading, carbon credits, and more!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = OnSurfaceMuted,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun QuestionChip(text: String, onClick: () -> Unit) {
    SuggestionChip(
        onClick = onClick,
        label = {
            Text(
                text,
                style = MaterialTheme.typography.labelSmall,
                fontSize = 11.sp
            )
        },
        colors = SuggestionChipDefaults.suggestionChipColors(
            containerColor = GreenPrimary.copy(alpha = 0.15f),
            labelColor = GreenPrimary
        ),
        border = SuggestionChipDefaults.suggestionChipBorder(
            enabled = true,
            borderColor = GreenPrimary.copy(alpha = 0.4f),
            borderWidth = 1.dp
        )
    )
}

@Composable
private fun ChatBubble(message: ChatMessage) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Bottom
    ) {
        if (!message.isUser) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(GreenPrimary.copy(alpha = 0.2f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text("🌿", fontSize = 16.sp)
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Box(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .clip(
                    if (message.isUser)
                        RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp, bottomStart = 18.dp, bottomEnd = 4.dp)
                    else
                        RoundedCornerShape(topStart = 4.dp, topEnd = 18.dp, bottomStart = 18.dp, bottomEnd = 18.dp)
                )
                .background(
                    if (message.isUser)
                        Brush.linearGradient(listOf(GreenPrimary, GreenPrimary.copy(alpha = 0.8f)))
                    else
                        Brush.linearGradient(listOf(CardDark, SurfaceDark))
                )
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Text(
                text = message.text,
                style = MaterialTheme.typography.bodyMedium,
                color = if (message.isUser) Color.White else OnSurfaceLight,
                lineHeight = 20.sp
            )
        }

        if (message.isUser) {
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(GreenPrimary.copy(alpha = 0.2f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text("👤", fontSize = 16.sp)
            }
        }
    }
}

@Composable
private fun TypingIndicator() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.Bottom
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(GreenPrimary.copy(alpha = 0.2f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text("🌿", fontSize = 16.sp)
        }
        Spacer(modifier = Modifier.width(8.dp))
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 18.dp, bottomStart = 18.dp, bottomEnd = 18.dp))
                .background(CardDark)
                .padding(horizontal = 14.dp, vertical = 14.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                CircularProgressIndicator(
                    modifier = Modifier.size(12.dp),
                    color = GreenPrimary,
                    strokeWidth = 2.dp
                )
                Text("GreenBot is thinking...", style = MaterialTheme.typography.labelSmall, color = OnSurfaceMuted)
            }
        }
    }
}
