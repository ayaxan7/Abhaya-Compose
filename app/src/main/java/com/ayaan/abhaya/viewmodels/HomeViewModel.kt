package com.ayaan.abhaya.viewmodels

import androidx.lifecycle.ViewModel
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.ayaan.abhaya.network.RetrofitClient
import com.ayaan.abhaya.network.SosRequest
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeViewModel : ViewModel(){
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private val _sosSendingState = MutableStateFlow<SosSendingState>(SosSendingState.Idle)
    val sosSendingState: StateFlow<SosSendingState> = _sosSendingState.asStateFlow()

    // User data state flows
    private val _userData = MutableStateFlow<UserData?>(null)
    val userData: StateFlow<UserData?> = _userData.asStateFlow()

    private val _userDataLoadingState = MutableStateFlow<UserDataLoadingState>(UserDataLoadingState.Idle)
    val userDataLoadingState: StateFlow<UserDataLoadingState> = _userDataLoadingState.asStateFlow()

    // Function to fetch user data
    fun fetchUserData() {
        viewModelScope.launch {
            try {
                _userDataLoadingState.value = UserDataLoadingState.Loading

                val currentUser = auth.currentUser ?: run {
                    _userDataLoadingState.value = UserDataLoadingState.Error("No authenticated user found")
                    return@launch
                }

                try {
                    // Query Firestore for user data
                    val userDoc = firestore.collection("users")
                        .document(currentUser.uid)
                        .get()
                        .await()

                    if (userDoc.exists()) {
                        // Extract name and phone number from document
                        val name = userDoc.getString("name") ?: "Anonymous"
                        val phoneNo = userDoc.getString("phone") ?: "XXXXXXXXXX"

                        Log.d("UserData", "Fetched user data: name=$name, phoneNo=$phoneNo")

                        // Update state flow with fetched data
                        _userData.value = UserData(name, phoneNo)
                        _userDataLoadingState.value = UserDataLoadingState.Success
                    } else {
                        Log.w("UserData", "User document does not exist for uid: ${currentUser.uid}")
                        _userDataLoadingState.value = UserDataLoadingState.Error("User data not found")
                    }
                } catch (e: Exception) {
                    Log.e("UserData", "Error fetching user data", e)
                    _userDataLoadingState.value = UserDataLoadingState.Error(e.message ?: "Unknown error occurred")
                }
            } catch (e: Exception) {
                Log.e("UserData", "Exception in fetchUserData", e)
                _userDataLoadingState.value = UserDataLoadingState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    fun sendSos(latitude: Double, longitude: Double, name: String, phoneNo: String) {
        viewModelScope.launch {
            try {
                _sosSendingState.value = SosSendingState.Loading

                val user = auth.currentUser ?: run {
                    _sosSendingState.value = SosSendingState.Error("No authenticated user found")
                    return@launch
                }

                try {
                    // Get user document
                    val userDoc = firestore.collection("users")
                        .document(user.uid)
                        .get()
                        .await()

                    // Get ID token
                    val idTokenResult = user.getIdToken(true).await()
                    val idToken = idTokenResult.token ?: run {
                        _sosSendingState.value = SosSendingState.Error("Failed to get ID token")
                        return@launch
                    }

                    val authHeader = "Bearer $idToken"

                    // Use current user data if available, or fallback to defaults
                    val currentUserData = _userData.value
//                    val name = currentUserData?.name ?: "Anonymous"
//                    val phoneNo = currentUserData?.phoneNo ?: "XXXXXXXXXX"

                    // Create request body
                    val sosRequest = SosRequest(
                        longitude = longitude,
                        latitude = latitude,
                        name = name,
                        phoneNo = phoneNo,
                        time = System.currentTimeMillis(),
                        uid= user.uid
                    )

                    Log.d("SOS Request", "Sending SOS with fields: " +
                            "longitude=$longitude, " +
                            "latitude=$latitude, " +
                            "name=$name, " +
                            "phoneNo=$phoneNo, " +
                            "time=${System.currentTimeMillis()}")

                    // Make API call
                    RetrofitClient.sosApiService.sendSos(authHeader, sosRequest)
                        .enqueue(object : Callback<ResponseBody> {
                            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                                if (response.isSuccessful) {
                                    Log.d("SOS Request", "SOS sent successfully. Response code: ${response.code()}")
                                    _sosSendingState.value = SosSendingState.Success
                                } else {
                                    val errorBody = response.errorBody()?.string() ?: "Unknown error"
                                    Log.e("SOS Request", "Failed to send SOS. Response code: ${response.code()}. Response body: $errorBody")
                                    _sosSendingState.value = SosSendingState.Error("Failed to send SOS. Try again.")
                                }
                            }

                            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                                Log.e("SOS Request", "Failed to send SOS request", t)
                                _sosSendingState.value = SosSendingState.Error("Failed to send SOS. Try again.")
                            }
                        })

                } catch (e: Exception) {
                    Log.e("SOS Request", "Exception while sending SOS", e)
                    _sosSendingState.value = SosSendingState.Error(e.message ?: "Unknown error occurred")
                }

            } catch (e: Exception) {
                _sosSendingState.value = SosSendingState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    // State class for the SOS sending operation
    sealed class SosSendingState {
        object Idle : SosSendingState()
        object Loading : SosSendingState()
        object Success : SosSendingState()
        data class Error(val message: String) : SosSendingState()
    }

    // State class for user data loading
    sealed class UserDataLoadingState {
        object Idle : UserDataLoadingState()
        object Loading : UserDataLoadingState()
        object Success : UserDataLoadingState()
        data class Error(val message: String) : UserDataLoadingState()
    }

    // Data class to hold user information
    data class UserData(
        val name: String,
        val phoneNo: String
    )
}