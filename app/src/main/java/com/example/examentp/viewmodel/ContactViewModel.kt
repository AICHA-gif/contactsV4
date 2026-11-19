package com.example.examenTP.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.examenTP.data.ContactDao
import com.example.examenTP.data.ContactDbHelper
import com.example.examenTP.model.Contact
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ContactViewModel(app: Application) : AndroidViewModel(app) {

    private val dao = ContactDao(
        ContactDbHelper.getInstance(app)
    )

    private val _contacts = MutableStateFlow<List<Contact>>(emptyList())
    val contacts: StateFlow<List<Contact>> = _contacts

    fun loadContacts() {
        viewModelScope.launch {
            _contacts.value = withContext(Dispatchers.IO) {
                dao.getAll()
            }
        }
    }

    fun search(text: String) {
        viewModelScope.launch {
            _contacts.value = withContext(Dispatchers.IO) {
                if (text.isBlank()) dao.getAll()
                else dao.search(text)
            }
        }
    }

    fun getContact(id: Long, callback: (Contact?) -> Unit) {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                dao.getById(id)
            }
            callback(result)
        }
    }

    fun save(contact: Contact, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                if (contact.id == 0L) {
                    dao.insert(contact) != -1L
                } else {
                    dao.update(contact) > 0
                }
            }
            loadContacts()
            callback(result)
        }
    }

    fun delete(id: Long, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                dao.delete(id) > 0
            }
            loadContacts()
            callback(result)
        }
    }
}