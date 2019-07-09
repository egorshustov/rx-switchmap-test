package com.egorshustov.rxswitchmaptest

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.egorshustov.rxswitchmaptest.data.Post
import kotlinx.android.synthetic.main.item_post.view.*

class PostsAdapter(var onPostClickListener: OnPostClickListener) : RecyclerView.Adapter<PostsAdapter.PostHolder>() {
    private var posts = mutableListOf<Post>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_post, null, false)
        return PostHolder(view, onPostClickListener)
    }

    override fun onBindViewHolder(holder: PostHolder, position: Int) {
        holder.bind(posts[position])
    }

    override fun getItemCount(): Int {
        return posts.size
    }

    fun updatePost(post: Post) {
        posts[posts.indexOf(post)] = post
        notifyItemChanged(posts.indexOf(post))
    }

    fun getPosts(): List<Post> {
        return posts
    }

    fun setPosts(posts: List<Post>) {
        this.posts = posts.toMutableList()
        notifyDataSetChanged()
    }

    inner class PostHolder(itemView: View, private var onPostClickListener: OnPostClickListener) :
        RecyclerView.ViewHolder(itemView), View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(post: Post) {
            itemView.text_title.text = post.title
        }

        override fun onClick(v: View?) {
            onPostClickListener.onPostClick(adapterPosition)
        }
    }

    interface OnPostClickListener {
        fun onPostClick(position: Int)
    }
}