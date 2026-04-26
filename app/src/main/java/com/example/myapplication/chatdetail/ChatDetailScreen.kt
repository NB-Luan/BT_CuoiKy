package com.example.myapplication.chatdetail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.myapplication.model.Message
import com.example.myapplication.remote.ChatApi
import com.example.myapplication.ui.theme.DiscordGreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatDetailScreen() {
    var messages by remember { mutableStateOf(listOf<Message>()) }
    val chatApi = remember { ChatApi.create() }
    val scope = rememberCoroutineScope()

    var showDeleteDialog by remember { mutableStateOf(false) }
    var messageToDelete by remember { mutableStateOf<Message?>(null) }


    fun loadData() {
        scope.launch {
            try {
                val response = chatApi.getMessages(1, 2)
                messages = response.map { it.copy(isMine = it.senderId == 1) }
            } catch (e: Exception) { e.printStackTrace() }
        }
    }

    LaunchedEffect(Unit) { loadData() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AsyncImage(
                            model = "https://ui-avatars.com/api/?name=Thang+Pham&background=random",
                            contentDescription = null,
                            modifier = Modifier.size(36.dp).clip(CircleShape)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Thắng Phạm", style = MaterialTheme.typography.titleMedium)
                            Text("Đang hoạt động", style = MaterialTheme.typography.bodySmall, color = DiscordGreen)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        bottomBar = {
            ChatInputBar(onSendMessage = { text ->
                scope.launch {
                    try {
                        chatApi.sendMessages(1, 2, text)
                        loadData()
                    } catch (e: Exception) { e.printStackTrace() }
                }
            })
        }
    ) { paddingValues ->

        MessageList(
            messages = messages,
            padding = paddingValues,
            onLongClick = { msg ->
                messageToDelete = msg
                showDeleteDialog = true
            }
        )
        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Thu hồi tin nhắn") },
                text = { Text("Bạn có chắc muốn thu hồi tin nhắn này không? Hành động này không thể hoàn tác.") },
                confirmButton = {
                    TextButton(onClick = {
                        scope.launch {
                            messageToDelete?.let { msg ->
                                chatApi.deleteMessages(msg.id)
                                loadData()
                            }
                            showDeleteDialog = false
                        }
                    }) {
                        Text("Thu hồi", color = Color.Red)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text("Hủy")
                    }
                }
            )
        }
    }
}

@Composable
fun MessageList(messages: List<Message>, padding: PaddingValues, onLongClick: (Message) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(padding),
        contentPadding = PaddingValues(16.dp),
        reverseLayout = true
    ) {
        items(messages) { msg ->
            // Truyền tiếp vào ChatBubble
            ChatBubble(
                message = msg,
                onLongClick = { onLongClick(msg) }
            )
        }
    }
}