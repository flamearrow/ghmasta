package band.mlgb.ghmasta.ui.repomvvm

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import band.mlgb.ghmasta.data.model.Repository
import band.mlgb.ghmasta.databinding.SearchResultRepositoryItemBinding
import javax.inject.Inject

class SearchResultPagingAdapter @Inject constructor() :
    PagingDataAdapter<Repository, SearchResultPagingAdapter.SearchResultViewHolder>(DIFF_CALLBACK) {
    class SearchResultViewHolder(private val binding: SearchResultRepositoryItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(repository: Repository) {
            binding.languages = repository.language
            binding.description = repository.description
            binding.name = repository.name
            binding.starCount = repository.stargazersCount.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchResultViewHolder {
        return SearchResultViewHolder(
            SearchResultRepositoryItemBinding.inflate(
                LayoutInflater.from(
                    parent.context
                )
            )
        )
    }

    override fun onBindViewHolder(holder: SearchResultViewHolder, position: Int) {
        // Create a UIModel to hold separator and actual item
        getItem(position)?.let {
            holder.bind(it)
        } ?: run {
            Log.d("BGLM", "empty returned from paging adapter")
        }
    }


    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Repository>() {
            override fun areItemsTheSame(oldItem: Repository, newItem: Repository): Boolean {
                return oldItem.id == oldItem.id
            }

            override fun areContentsTheSame(oldItem: Repository, newItem: Repository): Boolean {
                return oldItem.id == oldItem.id
            }

        }
    }
}