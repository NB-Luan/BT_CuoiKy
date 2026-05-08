package com.example.myapplication.chatdetail


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.myapplication.model.Message

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChatBubble(
    message: Message,
    onLongClick: (Int) -> Unit
) {
    val isDeleted = message.isDeleted == 1

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = if (message.isMine) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Bottom
    ) {
        if (!message.isMine) {
            AsyncImage(
                model = "https://ui-avatars.com/api/?name=Thang+Pham&background=random",
                contentDescription = null,
                modifier = Modifier.size(32.dp).clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        Box(
            modifier = Modifier.fillMaxWidth(0.85f),
            contentAlignment = if (message.isMine) Alignment.CenterEnd else Alignment.CenterStart
        ) {
            Surface(
                color = when {
                    isDeleted -> Color.LightGray.copy(alpha = 0.2f)
                    message.isMine -> MaterialTheme.colorScheme.primary
                    else -> MaterialTheme.colorScheme.surfaceVariant
                },
                shape = RoundedCornerShape(
                    topStart = 16.dp, topEnd = 16.dp,
                    bottomStart = if (message.isMine) 16.dp else 0.dp,
                    bottomEnd = if (message.isMine) 0.dp else 16.dp
                ),
                tonalElevation = 2.dp,
                modifier = Modifier.combinedClickable(
                    onClick = { },
                    onLongClick = {
                        if(message.isMine && !isDeleted) {
                            onLongClick(message.id)
                        }
                    }
                )
            ) {
                Text(

                    text = if (isDeleted) "Tin nhắn đã bị thu hồi" else message.content,

                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                    color = when {
                        isDeleted -> Color.Gray
                        message.isMine -> Color.White
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

    }
}

@Composable
fun ChatInputBar(onSendMessage: (String) -> Unit) {
    var textState by remember { mutableStateOf("") }

    var showEmojiPicker by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        AnimatedVisibility(visible = showEmojiPicker) {
            EmojiGrid(
                emojis = emojiList,
                onEmojiSelected = { selectedEmoji ->
                    textState += selectedEmoji
                }
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { showEmojiPicker = !showEmojiPicker }
            ) {
                Text("😀", fontSize = 24.sp)
            }

            Spacer(modifier = Modifier.width(4.dp))

            OutlinedTextField(
                value = textState,
                onValueChange = { textState = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Nhắn tin...", color = Color.Gray) },
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.background,
                    unfocusedContainerColor = MaterialTheme.colorScheme.background,
                    unfocusedBorderColor = Color.Transparent
                )
            )

            Spacer(modifier = Modifier.width(8.dp))


            IconButton(
                onClick = {
                    if (textState.isNotBlank()) {
                        onSendMessage(textState)
                        textState = ""
                        showEmojiPicker = false
                    }
                },
                modifier = Modifier.background(
                    color = if (textState.isNotBlank()) MaterialTheme.colorScheme.primary else Color.DarkGray,
                    shape = CircleShape
                )
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = null,
                    tint = Color.White
                )
            }
        }
    }
}
