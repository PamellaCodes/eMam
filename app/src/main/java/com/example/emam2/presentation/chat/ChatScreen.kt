package com.example.emam2.presentation.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.lazy.items
import com.example.emam2.ui.theme.AccentColor
import com.example.emam2.ui.theme.PrimaryColor


@Composable
fun ChatScreen(viewModel: ChatViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()

    LaunchedEffect(uiState.messages.size) {
        if (uiState.messages.isNotEmpty()) {
            listState.animateScrollToItem(uiState.messages.size - 1)
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
        Row(
            modifier = Modifier.fillMaxWidth().background(Color.White).padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(40.dp).clip(CircleShape).background(PrimaryColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.SmartToy, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text("Chat dengan MAX.AI", fontWeight = FontWeight.Bold, color = PrimaryColor, fontSize = 16.sp)
                Text("Asisten Nutrisi Ibu Hamil", fontSize = 12.sp, color = Color.Gray)
            }
        }

        HorizontalDivider(color = Color.LightGray, thickness = 0.5.dp)

        LazyColumn(
            state = listState,
            modifier = Modifier.weight(1f).padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(uiState.messages) { message ->
                ChatBubble(message = message)
            }
            if (uiState.isLoading) {
                item { TypingIndicator() }
            }
        }

        ChatInputBar(
            value = uiState.userInput,
            onValueChange = viewModel::onInputChanged,
            onSend = viewModel::sendMessage,
            isLoading = uiState.isLoading
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewChatScreen() {
    MaterialTheme {
        val fakeMessages = listOf(
            ChatMessage("Halo Bunda! 👋 Aku MAX.AI, asisten nutrisi khusus ibu hamil.", isFromUser = false),
            ChatMessage("Berikan aku rekomendasi makanan yang sesuai dengan kondisi aku sekarang dengan dana 50.000", isFromUser = true),
            ChatMessage("Berdasarkan kondisi kamu saat ini, berikut menu yang bisa kamu coba:\n\nNasi + Tumis Hati Ayam Bayam", isFromUser = false)
        )
        Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
            Row(
                modifier = Modifier.fillMaxWidth().background(Color.White).padding(18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.size(40.dp).clip(CircleShape).background(PrimaryColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.SmartToy, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Chat dengan MAX.AI",
                        fontWeight = FontWeight.Bold,
                        color = PrimaryColor,
                        fontSize = 16.sp,
                        lineHeight = 20.sp
                    )

                    Text(
                        text = "Asisten Nutrisi Ibu Hamil",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        lineHeight = 12.sp
                    )
                }
            }
            HorizontalDivider(color = Color.LightGray, thickness = 0.5.dp)
            LazyColumn(
                modifier = Modifier.weight(1f).padding(horizontal = 16.dp),
                contentPadding = PaddingValues(vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(fakeMessages) { message ->
                    ChatBubble(message = message)
                }
            }
            ChatInputBar(value = "", onValueChange = {}, onSend = {}, isLoading = false)
        }
    }
}

@Composable
fun ChatBubble(message: ChatMessage) {
    val isUser = message.isFromUser
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!isUser) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(PrimaryColor)
                    .align(Alignment.Bottom),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.SmartToy, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Surface(
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isUser) 16.dp else 4.dp,
                bottomEnd = if (isUser) 4.dp else 16.dp
            ),
            color = if (isUser) PrimaryColor else AccentColor,
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Text(
                text = message.content,
                color = if (isUser) Color.White else Color.Black,
                fontSize = 14.sp,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
            )
        }
    }
}

@Composable
fun TypingIndicator() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier.size(32.dp).clip(CircleShape).background(PrimaryColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Filled.SmartToy, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
        }
        Spacer(modifier = Modifier.width(8.dp))
        Surface(
            shape = RoundedCornerShape(16.dp, 16.dp, 16.dp, 4.dp),
            color = AccentColor
        ) {
            Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                CircularProgressIndicator(
                    modifier = Modifier.size(8.dp),
                    color = PrimaryColor,
                    strokeWidth = 2.dp
                )
            }
        }
    }
}

@Composable
fun ChatInputBar(
    value: String,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit,
    isLoading: Boolean
) {
    Surface(shadowElevation = 8.dp, color = Color.White) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal =14.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.weight(1f),
                placeholder = { Text("contoh: Budget mingguan saya Rp…", fontSize = 14.sp, color = Color.Gray) },
                shape = RoundedCornerShape(32.dp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(onSend = { onSend() }),
                maxLines = 4,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryColor,
                    unfocusedBorderColor = Color.LightGray
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            FilledIconButton(
                onClick = onSend,
                enabled = value.isNotBlank() && !isLoading,
                colors = IconButtonDefaults.filledIconButtonColors(containerColor = PrimaryColor),
                modifier = Modifier.size(48.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                } else {
                    Icon(Icons.Filled.Send, contentDescription = "Kirim", tint = Color.White)
                }
            }
        }
    }
}