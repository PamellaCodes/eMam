package com.example.emam2.presentation.forum

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.emam2.ui.theme.AccentColor
import com.example.emam2.ui.theme.PrimaryColor
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForumScreen(viewModel: ForumViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    if (uiState.showPostDialog) {
        NewPostDialog(
            content = uiState.newPostContent,
            onContentChange = viewModel::onPostContentChanged,
            onSubmit = viewModel::submitPost,
            onDismiss = viewModel::hidePostDialog
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Community", fontWeight = FontWeight.Bold, color = PrimaryColor, fontSize = 20.sp)
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = viewModel::showPostDialog,
                containerColor = PrimaryColor
            ) {
                Icon(Icons.Filled.Edit, contentDescription = "Buat postingan", tint = Color.White)
            }
        },
        containerColor = Color(0xFFF8F8F8)
    ) { padding ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PrimaryColor)
            }
        } else if (uiState.posts.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Filled.Forum, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(64.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Belum ada postingan", color = Color.Gray)
                    Text("Jadilah yang pertama berbagi!", color = Color.Gray, fontSize = 13.sp)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(uiState.posts, key = { it.id }) { post ->
                    ForumPostCard(
                        post = post,
                        currentUid = uiState.currentUid,
                        onLike = { viewModel.toggleLike(post) },
                        onDelete = { viewModel.deletePost(post) }
                    )
                }
            }
        }
    }
}

@Composable
fun ForumPostCard(
    post: ForumPost,
    currentUid: String,
    onLike: () -> Unit,
    onDelete: () -> Unit
) {
    val dateFormat = SimpleDateFormat("d MMM yyyy", Locale("id"))
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Hapus Postingan?") },
            text = { Text("Postingan ini akan dihapus permanen.") },
            confirmButton = {
                Button(
                    onClick = {
                        onDelete()
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) { Text("Hapus") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Batal") }
            }
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Author row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.size(42.dp).clip(CircleShape).background(AccentColor),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = post.authorName.firstOrNull()?.toString() ?: "B",
                        fontWeight = FontWeight.Bold,
                        color = PrimaryColor,
                        fontSize = 18.sp
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(post.authorName, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = Color.Black)
                    Text(dateFormat.format(Date(post.timestamp)), fontSize = 12.sp, color = Color.Gray)
                }

                // Tombol delete hanya untuk post milik sendiri
                if (post.uid == currentUid) {
                    IconButton(
                        onClick = { showDeleteDialog = true },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Filled.DeleteOutline,
                            contentDescription = "Hapus",
                            tint = Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(post.content, fontSize = 14.sp, color = Color.DarkGray, lineHeight = 20.sp)

            Spacer(modifier = Modifier.height(12.dp))

            HorizontalDivider(color = Color.LightGray, thickness = 0.5.dp)

            Spacer(modifier = Modifier.height(8.dp))

            // Like button
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onLike, modifier = Modifier.size(32.dp)) {
                    Icon(
                        imageVector = if (post.isLikedByMe) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = "Like",
                        tint = if (post.isLikedByMe) PrimaryColor else Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text("${post.likesCount}", fontSize = 13.sp, color = Color.Gray)
            }
        }
    }
}

@Composable
fun NewPostDialog(
    content: String,
    onContentChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Buat Postingan", fontWeight = FontWeight.Bold, color = PrimaryColor) },
        text = {
            OutlinedTextField(
                value = content,
                onValueChange = onContentChange,
                placeholder = { Text("Bagikan pengalaman, tips, atau pertanyaan kamu...") },
                modifier = Modifier.fillMaxWidth().height(150.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryColor,
                    unfocusedBorderColor = Color.LightGray
                )
            )
        },
        confirmButton = {
            Button(
                onClick = onSubmit,
                enabled = content.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor),
                shape = RoundedCornerShape(12.dp)
            ) { Text("Posting") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal", color = Color.Gray) }
        }
    )
}