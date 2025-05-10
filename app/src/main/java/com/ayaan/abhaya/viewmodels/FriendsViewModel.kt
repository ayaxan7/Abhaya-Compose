package com.ayaan.abhaya.viewmodels

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class Friend(
    val name: String = "",
    val phone: String = "",
    val fcmToken: String = ""
)
class FriendsViewModel: ViewModel() {

    private val _friends = MutableStateFlow<List<Friend>>(emptyList())
    val friends: StateFlow<List<Friend>> = _friends

    init {
        fetchFriends()
    }
    fun deleteFriend(context: Context, phone: String) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        FirebaseFirestore.getInstance()
            .collection("users")
            .document(uid)
            .collection("friends")
            .document(phone)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(context, "Contact removed successfully", Toast.LENGTH_SHORT).show()
                fetchFriends() // Refresh the list
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Failed to remove contact: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
    fun fetchFriends() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        FirebaseFirestore.getInstance()
            .collection("users")
            .document(uid)
            .collection("friends")
            .get()
            .addOnSuccessListener { result ->
                val list = result.documents.mapNotNull { doc ->
                    doc.toObject(Friend::class.java)
                }
                _friends.value = list
            }
    }
    fun saveContactUnderUser(context: Context, name: String, phone: String) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val db = FirebaseFirestore.getInstance()

        // Extract the last 10 digits of the phone number
        val cleanPhone = phone.filter { it.isDigit() }.takeLast(10)

        // Step 1: Get FCM token from users collection
        db.collection("users")
            .whereEqualTo("phone", cleanPhone)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val fcmToken = querySnapshot.documents.firstOrNull()?.getString("fcmToken") ?: ""

                // Step 2: Create contact data map
                val contactData = hashMapOf(
                    "name" to name,
                    "phone" to cleanPhone,
                    "fcmToken" to fcmToken
                )

                // Step 3: Store it under users/{uid}/friends/{cleanPhone}
                db.collection("users")
                    .document(uid)
                    .collection("friends")
                    .document(cleanPhone)
                    .set(contactData)
                    .addOnSuccessListener {
                        Toast.makeText(context, "Friend saved successfully!", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(context, "Failed to save friend", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener {
                Toast.makeText(context, "Error looking up FCM token", Toast.LENGTH_SHORT).show()
            }
    }


}