package com.example.examenTP.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.examenTP.R
import com.example.examenTP.model.Contact
import com.example.examenTP.utils.PreferencesManager
import java.io.File

class ContactAdapter(
    private val context: Context,
    private val onClick: (Contact) -> Unit,
    private val onCall: (Contact) -> Unit
) : RecyclerView.Adapter<ContactAdapter.ContactHolder>() {

    private var contacts = listOf<Contact>()

    private val preferences = PreferencesManager(context)

    fun updateContacts(list: List<Contact>) {
        contacts = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ContactHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_contact, parent, false)

        return ContactHolder(view)
    }

    override fun getItemCount() = contacts.size

    override fun onBindViewHolder(
        holder: ContactHolder,
        position: Int
    ) {

        val contact = contacts[position]

        holder.nom.text = contact.nom
        holder.prenom.text = contact.prenom
        holder.telephone.text = contact.telephone

        val taille = preferences.getTaille()
        val couleur = preferences.getCouleur()

        holder.nom.textSize = taille
        holder.prenom.textSize = taille
        holder.telephone.textSize = taille

        holder.nom.setTextColor(couleur)
        holder.prenom.setTextColor(couleur)

        holder.btnAppeler.visibility =
            if (preferences.getAppel()) View.VISIBLE else View.GONE

        holder.btnAppeler.setOnClickListener {
            onCall(contact)
        }

        holder.itemView.setOnClickListener {
            onClick(contact)
        }

        if (contact.photoUri != null) {
            val file = File(contact.photoUri)

            if (file.exists()) {
                holder.photo.setImageURI(
                    Uri.fromFile(file)
                )
            }
        }
    }

    class ContactHolder(view: View) :
        RecyclerView.ViewHolder(view) {

        val nom: TextView = view.findViewById(R.id.txtNom)
        val prenom: TextView = view.findViewById(R.id.txtPrenom)
        val telephone: TextView = view.findViewById(R.id.txtTelephone)
        val photo: ImageView = view.findViewById(R.id.imgContact)
        val btnAppeler: Button = view.findViewById(R.id.btnAppeler)
    }
}