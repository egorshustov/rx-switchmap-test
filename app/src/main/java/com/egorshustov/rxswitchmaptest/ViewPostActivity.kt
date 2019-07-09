package com.egorshustov.rxswitchmaptest

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.egorshustov.rxswitchmaptest.data.Post
import kotlinx.android.synthetic.main.activity_view_post.*


class ViewPostActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_post)

        getIncomingIntent()
    }

    private fun getIncomingIntent() {
        if (intent.hasExtra("post")) {
            val post = intent.getParcelableExtra<Post>("post")
            text.text = post.title
        }
    }
}