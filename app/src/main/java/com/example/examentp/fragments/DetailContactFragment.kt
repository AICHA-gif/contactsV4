package com.example.examenTP.fragments

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.examenTP.R
import com.example.examenTP.model.Contact
import com.example.examenTP.viewmodel.ContactViewModel
import java.io.File

class DetailContactFragment :
    Fragment(R.layout.fragment_detail_contact) {

    private val viewModel: ContactViewModel by viewModels(
        { requireActivity() }
    )

    private var contactId = 0L
    private var photoPath: String? = null

    private lateinit var nom: EditText
    private lateinit var prenom: EditText
    private lateinit var telephone: EditText
    private lateinit var email: EditText
    private lateinit var latitude: EditText
    private lateinit var longitude: EditText

    private val galerie = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->

        uri?.let {
            photoPath = copyPhoto(it)
            view?.findViewById<ImageView>(
                R.id.imgPhoto
            )?.setImageURI(Uri.fromFile(File(photoPath!!)))
        }
    }

    companion object {

        fun newInstance(id: Long) =
            DetailContactFragment().apply {

                arguments = Bundle().apply {
                    putLong("contact_id", id)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        contactId = arguments?.getLong("contact_id", 0L) ?: 0L

        nom = view.findViewById(R.id.edtNom)
        prenom = view.findViewById(R.id.edtPrenom)
        telephone = view.findViewById(R.id.edtTelephone)
        email = view.findViewById(R.id.edtEmail)
        latitude = view.findViewById(R.id.edtLatitude)
        longitude = view.findViewById(R.id.edtLongitude)

        if (contactId != 0L) {
            chargerContact()
        } else {
            view.findViewById<Button>(R.id.btnSupprimer).visibility =
                View.GONE
        }

        view.findViewById<Button>(R.id.btnGalerie).setOnClickListener {
            galerie.launch("image/*")
        }

        view.findViewById<Button>(R.id.btnEnregistrer).setOnClickListener {
            enregistrer()
        }

        view.findViewById<Button>(R.id.btnSupprimer).setOnClickListener {
            supprimer()
        }

        view.findViewById<Button>(R.id.btnAppelerDetail).setOnClickListener {
            appeler()
        }

        view.findViewById<Button>(R.id.btnEmail).setOnClickListener {
            envoyerEmail()
        }

        view.findViewById<Button>(R.id.btnCarte).setOnClickListener {
            ouvrirCarte()
        }
    }

    private fun chargerContact() {

        viewModel.getContact(contactId) { contact ->

            if (contact == null) {
                Toast.makeText(
                    requireContext(),
                    "Contact introuvable",
                    Toast.LENGTH_SHORT
                ).show()
                parentFragmentManager.popBackStack()
                return@getContact
            }

            nom.setText(contact.nom)
            prenom.setText(contact.prenom)
            telephone.setText(contact.telephone)
            email.setText(contact.email ?: "")

            latitude.setText(contact.latitude?.toString() ?: "")
            longitude.setText(contact.longitude?.toString() ?: "")

            photoPath = contact.photoUri

            contact.photoUri?.let {
                val file = File(it)
                if (file.exists()) {
                    view?.findViewById<ImageView>(
                        R.id.imgPhoto
                    )?.setImageURI(Uri.fromFile(file))
                }
            }
        }
    }

    private fun enregistrer() {

        val n = nom.text.toString().trim()
        val p = prenom.text.toString().trim()
        val t = telephone.text.toString().trim()

        if (n.isEmpty() || p.isEmpty() || t.isEmpty()) {
            Toast.makeText(
                requireContext(),
                "Nom, prénom et téléphone obligatoires",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val latText = latitude.text.toString().trim()
        val lonText = longitude.text.toString().trim()

        if (latText.isEmpty() != lonText.isEmpty()) {
            Toast.makeText(
                requireContext(),
                "Saisissez les deux coordonnées GPS",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val lat = latText.toDoubleOrNull()
        val lon = lonText.toDoubleOrNull()

        if (latText.isNotEmpty() &&
            (lat == null || lon == null ||
                    lat !in -90.0..90.0 ||
                    lon !in -180.0..180.0)
        ) {
            Toast.makeText(
                requireContext(),
                "Coordonnées GPS invalides",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val contact = Contact(
            id = contactId,
            nom = n,
            prenom = p,
            telephone = t,
            email = email.text.toString().trim().ifEmpty { null },
            photoUri = photoPath,
            latitude = lat,
            longitude = lon
        )

        viewModel.save(contact) { success ->

            if (success) {
                Toast.makeText(
                    requireContext(),
                    "Contact enregistré",
                    Toast.LENGTH_SHORT
                ).show()

                parentFragmentManager.popBackStack()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Erreur d'enregistrement",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun supprimer() {

        AlertDialog.Builder(requireContext())
            .setTitle("Supprimer")
            .setMessage("Voulez-vous supprimer ce contact ?")
            .setPositiveButton("Oui") { _, _ ->

                viewModel.delete(contactId) { success ->

                    if (success) {
                        parentFragmentManager.popBackStack()
                    }
                }
            }
            .setNegativeButton("Annuler", null)
            .show()
    }

    private fun appeler() {

        val numero = telephone.text.toString()

        if (numero.isBlank()) return

        startActivity(
            Intent(
                Intent.ACTION_DIAL,
                Uri.parse("tel:${Uri.encode(numero)}")
            )
        )
    }

    private fun envoyerEmail() {

        val adresse = email.text.toString()

        if (adresse.isBlank()) {
            Toast.makeText(
                requireContext(),
                "Adresse e-mail vide",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:${Uri.encode(adresse)}")
        }

        startActivity(intent)
    }

    private fun ouvrirCarte() {

        val lat = latitude.text.toString().toDoubleOrNull()
        val lon = longitude.text.toString().toDoubleOrNull()

        if (lat == null || lon == null) {
            Toast.makeText(
                requireContext(),
                "Coordonnées GPS indisponibles",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        parentFragmentManager.beginTransaction()
            .replace(
                R.id.fragmentContainer,
                CarteContactFragment.newInstance(lat, lon)
            )
            .addToBackStack(null)
            .commit()
    }

    private fun copyPhoto(uri: Uri): String? {

        return try {
            val file = File(
                requireContext().filesDir,
                "contact_${System.currentTimeMillis()}.jpg"
            )

            requireContext().contentResolver
                .openInputStream(uri)?.use { input ->
                    file.outputStream().use { output ->
                        input.copyTo(output)
                    }
                } ?: return null

            file.absolutePath

        } catch (e: Exception) {
            null
        }
    }
}