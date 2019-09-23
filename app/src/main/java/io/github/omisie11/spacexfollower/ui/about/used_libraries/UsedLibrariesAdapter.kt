package io.github.omisie11.spacexfollower.ui.about.used_libraries

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.github.omisie11.spacexfollower.R
import kotlinx.android.synthetic.main.cores_recycler_item.view.text_lib_desc
import kotlinx.android.synthetic.main.cores_recycler_item.view.text_lib_name
import kotlinx.android.synthetic.main.used_libraries_recycler_item.view.*

class UsedLibrariesAdapter(private val itemClickListener: OnItemClickListener) :
    RecyclerView.Adapter<UsedLibrariesAdapter.ViewHolder>() {

    private var librariesList: List<UsedLibrary> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.used_libraries_recycler_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (librariesList.isNotEmpty()) holder.bind(librariesList[position], itemClickListener)
    }

    override fun getItemCount(): Int = librariesList.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val libLicenseTextView: TextView = itemView.text_lib_license
        private val libNameTextView: TextView = itemView.text_lib_name
        private val libDescTextView: TextView = itemView.text_lib_desc

        fun bind(library: UsedLibrary, itemClickListener: OnItemClickListener) {
            libLicenseTextView.text = library.license
            libNameTextView.text = library.name
            libDescTextView.text = library.description

            itemView.setOnClickListener {
                if (adapterPosition != -1) itemClickListener.onItemClicked(library.repositoryUrl)
            }
        }
    }

    fun setData(data: List<UsedLibrary>) {
        librariesList = data
        notifyDataSetChanged()
    }

    interface OnItemClickListener {
        fun onItemClicked(usedLibraryRepoUrl: String)
    }
}