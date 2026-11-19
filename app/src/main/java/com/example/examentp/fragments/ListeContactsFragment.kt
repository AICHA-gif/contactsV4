package com.example.examenTP.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.examenTP.R
import com.example.examenTP.adapter.ContactAdapter
import com.example.examenTP.viewmodel.ContactViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch

class ListeContactsFragment :
    Fragment(R.layout.fragment_liste_contacts) {

    private val viewModel: ContactViewModel by viewModels()

    private lateinit var adapter: ContactAdapter

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        val recycler = view.findViewById<RecyclerView>(
            R.id.recyclerContacts
        )

        val recherche = view.findViewById<EditText>(
            R.id.edtRecherche
        )

        adapter = ContactAdapter(
            requireContext(),

            onClick = { contact ->

                parentFragmentManager.beginTransaction()
                    .replace(
                        R.id.fragmentContainer,
                        DetailContactFragment.newInstance(contact.id)
                    )
                    .addToBackStack(null)
                    .commit()
            },

            onCall = { contact ->
                appeler(contact.telephone)
            }
        )

        recycler.layoutManager = LinearLayoutManager(requireContext())
        recycler.adapter = adapter

        view.findViewById<FloatingActionButton>(
            R.id.btnAjouter
        ).setOnClickListener {

            parentFragmentManager.beginTransaction()
                .replace(
                    R.id.fragmentContainer,
                    DetailContactFragment.newInstance(0L)
                )
                .addToBackStack(null)
                .commit()
        }

        recherche.addTextChangedListener(
            object : TextWatcher {

                override fun beforeTextChanged(
                    s: CharSequence?, start: Int, count: Int, after: Int
                ) {}

                override fun onTextChanged(
                    s: CharSequence?, start: Int, before: Int, count: Int
                ) {
                    viewModel.search(s.toString())
                }

                override fun afterTextChanged(s: Editable?) {}
            }
        )

        viewLifecycleOwner.lifecycleScope.launch {

            viewLifecycleOwner.repeatOnLifecycle(
                Lifecycle.State.STARTED
            ) {
                viewModel.contacts.collect { contacts ->
                    adapter.updateContacts(contacts)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        viewModel.loadContacts()

        if (::adapter.isInitialized) {
            adapter.notifyDataSetChanged()
        }
    }

    private fun appeler(numero: String) {

        if (numero.isBlank()) {
            Toast.makeText(
                requireContext(),
                "Numéro vide",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val intent = Intent(
            Intent.ACTION_DIAL,
            Uri.parse("tel:${Uri.encode(numero)}")
        )

        try {
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(
                requireContext(),
                "Application téléphone indisponible",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}