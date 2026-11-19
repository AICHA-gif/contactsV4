
package com.example.examenTP.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class ContactDbHelper private constructor(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "contacts.db"
        private const val DATABASE_VERSION = 1

        @Volatile
        private var INSTANCE: ContactDbHelper? = null

        fun getInstance(context: Context): ContactDbHelper {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: ContactDbHelper(
                    context.applicationContext
                ).also { INSTANCE = it }
            }
        }
    }

    override fun onCreate(db: SQLiteDatabase) {

        val createTable = """
            CREATE TABLE ${ContactContract.TABLE_NAME} (
                ${ContactContract.COL_ID} INTEGER PRIMARY KEY AUTOINCREMENT,
                ${ContactContract.COL_NOM} TEXT NOT NULL,
                ${ContactContract.COL_PRENOM} TEXT NOT NULL,
                ${ContactContract.COL_TELEPHONE} TEXT NOT NULL,
                ${ContactContract.COL_EMAIL} TEXT,
                ${ContactContract.COL_PHOTO_URI} TEXT,
                ${ContactContract.COL_LATITUDE} REAL,
                ${ContactContract.COL_LONGITUDE} REAL
            )
        """.trimIndent()

        db.execSQL(createTable)

        // Ajouter 5 contacts de démonstration
        ajouterContact(db, "Moussa", "Aicha", "+216 20111222",
            "aicha@gmail.com", 33.8076, 10.8451)

        ajouterContact(db, "Ali", "Ahmed", "+216 22123456",
            "ali@gmail.com", 33.8750, 10.8570)

        ajouterContact(db, "Sami", "Mohamed", "+216 55123456",
            "sami@gmail.com", null, null)

        ajouterContact(db, "Fatma", "Amira", "+216 98123456",
            "fatma@gmail.com", 36.8065, 10.1815)

        ajouterContact(db, "Sara", "Nour", "+216 50123456",
            "sara@gmail.com", null, null)
    }

    private fun ajouterContact(
        db: SQLiteDatabase,
        nom: String,
        prenom: String,
        telephone: String,
        email: String,
        latitude: Double?,
        longitude: Double?
    ) {
        val values = ContentValues().apply {
            put(ContactContract.COL_NOM, nom)
            put(ContactContract.COL_PRENOM, prenom)
            put(ContactContract.COL_TELEPHONE, telephone)
            put(ContactContract.COL_EMAIL, email)

            if (latitude != null) {
                put(ContactContract.COL_LATITUDE, latitude)
            } else {
                putNull(ContactContract.COL_LATITUDE)
            }

            if (longitude != null) {
                put(ContactContract.COL_LONGITUDE, longitude)
            } else {
                putNull(ContactContract.COL_LONGITUDE)
            }
        }

        db.insert(ContactContract.TABLE_NAME, null, values)
    }

    override fun onUpgrade(
        db: SQLiteDatabase,
        oldVersion: Int,
        newVersion: Int
    ) {
        // Les migrations seront ajoutées ici si le schéma change.
    }
}
