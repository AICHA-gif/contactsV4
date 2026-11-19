package com.example.examenTP.data

import android.content.ContentValues
import android.database.Cursor
import com.example.examenTP.model.Contact

class ContactDao(private val helper: ContactDbHelper) {

    fun insert(contact: Contact): Long {

        val values = contactToValues(contact)

        return helper.writableDatabase.insert(
            ContactContract.TABLE_NAME,
            null,
            values
        )
    }

    fun update(contact: Contact): Int {

        val values = contactToValues(contact)

        return helper.writableDatabase.update(
            ContactContract.TABLE_NAME,
            values,
            "${ContactContract.COL_ID} = ?",
            arrayOf(contact.id.toString())
        )
    }

    fun delete(id: Long): Int {

        return helper.writableDatabase.delete(
            ContactContract.TABLE_NAME,
            "${ContactContract.COL_ID} = ?",
            arrayOf(id.toString())
        )
    }

    fun getAll(): List<Contact> {

        val contacts = mutableListOf<Contact>()

        val cursor = helper.readableDatabase.query(
            ContactContract.TABLE_NAME,
            null,
            null,
            null,
            null,
            null,
            "nom COLLATE NOCASE ASC, prenom COLLATE NOCASE ASC"
        )

        cursor.use {
            while (it.moveToNext()) {
                contacts.add(cursorToContact(it))
            }
        }

        return contacts
    }

    fun getById(id: Long): Contact? {

        val cursor = helper.readableDatabase.query(
            ContactContract.TABLE_NAME,
            null,
            "${ContactContract.COL_ID} = ?",
            arrayOf(id.toString()),
            null,
            null,
            null
        )

        cursor.use {
            return if (it.moveToFirst()) {
                cursorToContact(it)
            } else null
        }
    }

    fun search(texte: String): List<Contact> {

        val contacts = mutableListOf<Contact>()

        val selection = """
            nom LIKE ? OR prenom LIKE ? OR telephone LIKE ?
        """.trimIndent()

        val arg = "%$texte%"

        val cursor = helper.readableDatabase.query(
            ContactContract.TABLE_NAME,
            null,
            selection,
            arrayOf(arg, arg, arg),
            null,
            null,
            "nom COLLATE NOCASE ASC, prenom COLLATE NOCASE ASC"
        )

        cursor.use {
            while (it.moveToNext()) {
                contacts.add(cursorToContact(it))
            }
        }

        return contacts
    }

    private fun contactToValues(c: Contact): ContentValues {

        return ContentValues().apply {

            put(ContactContract.COL_NOM, c.nom)
            put(ContactContract.COL_PRENOM, c.prenom)
            put(ContactContract.COL_TELEPHONE, c.telephone)
            put(ContactContract.COL_EMAIL, c.email)
            put(ContactContract.COL_PHOTO_URI, c.photoUri)

            if (c.latitude == null) {
                putNull(ContactContract.COL_LATITUDE)
            } else {
                put(ContactContract.COL_LATITUDE, c.latitude)
            }

            if (c.longitude == null) {
                putNull(ContactContract.COL_LONGITUDE)
            } else {
                put(ContactContract.COL_LONGITUDE, c.longitude)
            }
        }
    }

    private fun cursorToContact(c: Cursor): Contact {

        val lat = c.getColumnIndexOrThrow(ContactContract.COL_LATITUDE)
        val lon = c.getColumnIndexOrThrow(ContactContract.COL_LONGITUDE)

        return Contact(
            id = c.getLong(c.getColumnIndexOrThrow(ContactContract.COL_ID)),
            nom = c.getString(c.getColumnIndexOrThrow(ContactContract.COL_NOM)),
            prenom = c.getString(c.getColumnIndexOrThrow(ContactContract.COL_PRENOM)),
            telephone = c.getString(c.getColumnIndexOrThrow(ContactContract.COL_TELEPHONE)),
            email = c.getString(c.getColumnIndexOrThrow(ContactContract.COL_EMAIL)),
            photoUri = c.getString(c.getColumnIndexOrThrow(ContactContract.COL_PHOTO_URI)),
            latitude = if (c.isNull(lat)) null else c.getDouble(lat),
            longitude = if (c.isNull(lon)) null else c.getDouble(lon)
        )
    }
}