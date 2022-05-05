package com.bangkit.story.ui.widget

import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import androidx.core.os.bundleOf
import com.bangkit.story.R
import com.bangkit.story.data.local.SessionManager
import com.bangkit.story.utils.getBitmapFromURL

internal class StackRemoteViewsFactory(private val context: Context) : RemoteViewsService.RemoteViewsFactory {
    private var widgetItems = ArrayList<String>()
    private lateinit var sessionManager: SessionManager

    override fun onCreate() {
        sessionManager = SessionManager(context)
    }

    override fun onDataSetChanged() {
        sessionManager.apply {
            for (i in 0..5) {
                widgetItems.add(getImage("image-$i"))
            }
        }
    }

    override fun getViewAt(position: Int): RemoteViews {
        val rv = RemoteViews(context.packageName, R.layout.layout_widget)
        val item = widgetItems[position]
        val bitmap = getBitmapFromURL(item)
        rv.setImageViewBitmap(R.id.storyPhotoWidget, bitmap)

        val extras = bundleOf(
            StackWidget.EXTRA_ITEM to position
        )

        val fillInIntent = Intent()
        fillInIntent.putExtras(extras)

        rv.setOnClickFillInIntent(R.id.storyPhotoWidget, fillInIntent)
        return rv
    }

    override fun onDestroy() {}

    override fun getCount() = widgetItems.size

    override fun getLoadingView() = null

    override fun getViewTypeCount() = 1

    override fun getItemId(i: Int) = 0L

    override fun hasStableIds() = false
}