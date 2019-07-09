package com.egorshustov.rxswitchmaptest

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.egorshustov.rxswitchmaptest.data.Post
import com.egorshustov.rxswitchmaptest.data.remote.ServiceGenerator
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Function
import io.reactivex.functions.Predicate
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity(), PostsAdapter.OnPostClickListener {
    private val publishSubject = PublishSubject.create<Post>()
    private val postsAdapter = PostsAdapter(this)
    private val compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initRecyclerView()
    }

    override fun onResume() {
        super.onResume()
        progress_bar.progress = 0
        initSwitchMapDemo()
        retrievePosts()
    }

    private fun initSwitchMapDemo() {
        publishSubject
            // apply switchMap operator so only one Observable can be used at a time.
            // it clears the previous one
            .switchMap(object : Function<Post, ObservableSource<Post>> {
                override fun apply(post: Post): ObservableSource<Post> {
                    return Observable
                        // simulate slow network speed with interval + takeWhile + filter operators
                        .interval(PERIOD, TimeUnit.MILLISECONDS)
                        .subscribeOn(AndroidSchedulers.mainThread())
                        .takeWhile(object : Predicate<Long> {
                            override fun test(t: Long): Boolean {
                                Log.d(TAG, "test: " + Thread.currentThread().name + ", " + t)
                                progress_bar.max = 3000 - PERIOD.toInt()
                                progress_bar.progress = Integer.parseInt((t * PERIOD + PERIOD).toString())
                                return t <= 3000 / PERIOD
                            } // stop the process if more than 5 seconds passes
                        })
                        .filter(object : Predicate<Long> {
                            override fun test(t: Long): Boolean {
                                return t >= 3000 / PERIOD
                            }
                        })
                        // flatMap to convert Long from the interval operator into a Observable<Post>
                        .subscribeOn(Schedulers.io())
                        .flatMap(object : Function<Long, ObservableSource<Post>> {
                            override fun apply(t: Long): ObservableSource<Post> {
                                return ServiceGenerator.getRequestApi()
                                    .getPost(post.id)
                            }
                        })
                }

            })
            .subscribe(object : Observer<Post> {
                override fun onSubscribe(d: Disposable) {
                    compositeDisposable.add(d)
                }

                override fun onNext(post: Post) {
                    Log.d(TAG, "onNext: done.")
                    navViewPostActivity(post)
                }

                override fun onError(e: Throwable) {
                    Log.e(TAG, "onError: ", e)
                }

                override fun onComplete() {
                }
            })
    }

    private fun retrievePosts() {
        ServiceGenerator.getRequestApi()
            .getPosts()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<List<Post>> {
                override fun onSubscribe(d: Disposable) {
                    compositeDisposable.add(d)
                }

                override fun onNext(posts: List<Post>) {
                    postsAdapter.setPosts(posts)
                }

                override fun onError(e: Throwable) {
                    Log.e(TAG, "onError: ", e)
                }

                override fun onComplete() {}
            })
    }

    private fun navViewPostActivity(post: Post) {
        val intent = Intent(this, ViewPostActivity::class.java)
        intent.putExtra("post", post)
        startActivity(intent)
    }

    private fun initRecyclerView() {
        recycler_posts.layoutManager = LinearLayoutManager(this)
        recycler_posts.adapter = postsAdapter
    }

    override fun onPostClick(position: Int) {
        Log.d(TAG, "onPostClick: clicked.")
        publishSubject.onNext(postsAdapter.getPosts()[position])
    }

    override fun onPause() {
        Log.d(TAG, "onPause: called.")
        compositeDisposable.clear()
        super.onPause()
    }

    companion object {
        private const val TAG = "RxSwitchMapTest"
        private const val PERIOD: Long = 100
    }
}
