package com.example.emam2.presentation.forum

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

data class ForumPost(
    val id: String = "",
    val authorName: String = "",
    val content: String = "",
    val likesCount: Int = 0,
    val isLikedByMe: Boolean = false,
    val timestamp: Long = 0L,
    val uid: String = ""
)

data class ForumUiState(
    val posts: List<ForumPost> = emptyList(),
    val isLoading: Boolean = true,
    val showPostDialog: Boolean = false,
    val newPostContent: String = "",
    val userName: String = "",
    val currentUid: String = ""
)

class ForumViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ForumUiState())
    val uiState: StateFlow<ForumUiState> = _uiState

    private val db = FirebaseFirestore.getInstance()
    private val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    init {
        _uiState.update { it.copy(currentUid = uid) }
        loadUserName()
        loadPosts()
    }

    private fun loadUserName() {
        db.collection("users").document(uid).get()
            .addOnSuccessListener { doc ->
                _uiState.update { it.copy(userName = doc.getString("fullName") ?: "Bunda") }
            }
    }

    private fun loadPosts() {
        db.collection("forum")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot == null) return@addSnapshotListener
                val posts = snapshot.documents.map { doc ->
                    val likedBy = doc.get("likedBy") as? List<*> ?: emptyList<String>()
                    ForumPost(
                        id = doc.id,
                        authorName = doc.getString("authorName") ?: "",
                        content = doc.getString("content") ?: "",
                        likesCount = doc.getLong("likesCount")?.toInt() ?: 0,
                        isLikedByMe = likedBy.contains(uid),
                        timestamp = doc.getLong("timestamp") ?: 0L,
                        uid = doc.getString("uid") ?: ""
                    )
                }
                _uiState.update { it.copy(posts = posts, isLoading = false) }
            }
    }

    fun showPostDialog() = _uiState.update { it.copy(showPostDialog = true) }
    fun hidePostDialog() = _uiState.update { it.copy(showPostDialog = false, newPostContent = "") }
    fun onPostContentChanged(text: String) = _uiState.update { it.copy(newPostContent = text) }

    fun submitPost() {
        val content = _uiState.value.newPostContent.trim()
        if (content.isBlank()) return

        val post = hashMapOf(
            "authorName" to _uiState.value.userName,
            "content" to content,
            "likesCount" to 0,
            "likedBy" to emptyList<String>(),
            "timestamp" to System.currentTimeMillis(),
            "uid" to uid
        )

        db.collection("forum").add(post)
            .addOnSuccessListener { hidePostDialog() }
    }

    fun toggleLike(post: ForumPost) {
        val postRef = db.collection("forum").document(post.id)
        if (post.isLikedByMe) {
            postRef.update(
                "likesCount", post.likesCount - 1,
                "likedBy", com.google.firebase.firestore.FieldValue.arrayRemove(uid)
            )
        } else {
            postRef.update(
                "likesCount", post.likesCount + 1,
                "likedBy", com.google.firebase.firestore.FieldValue.arrayUnion(uid)
            )
        }
    }

    fun deletePost(post: ForumPost) {
        if (post.id.isEmpty()) return
        db.collection("forum").document(post.id).delete()
    }
}