package com.dailyshayari.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.dailyshayari.data.Notification
import com.dailyshayari.db.ShayariDatabase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NotificationViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = ShayariDatabase.getDatabase(application).notificationDao()

    val allNotifications: StateFlow<List<Notification>> = dao.getAllNotifications()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val unreadNotifications: StateFlow<List<Notification>> = dao.getUnreadNotifications()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun markAllAsRead() {
        viewModelScope.launch {
            dao.markAllAsRead()
        }
    }

    fun markAsRead(id: Int) {
        viewModelScope.launch {
            dao.markAsRead(id)
        }
    }
}
