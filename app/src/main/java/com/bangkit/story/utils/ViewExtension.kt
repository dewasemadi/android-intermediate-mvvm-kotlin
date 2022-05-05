package com.bangkit.story.utils

import android.animation.Animator
import android.animation.ObjectAnimator
import android.content.Context
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import com.bangkit.story.R
import com.bumptech.glide.Glide

fun ImageView.setImage(context: Context, url: String?) {
    Glide.with(context).load(url).placeholder(R.drawable.blank_image).into(this)
}

fun View.fadeIn(): Animator {
    return ObjectAnimator.ofFloat(this, View.ALPHA, 1f).setDuration(200)
}

fun EditText.setTextValue(value: String?){
    if(value != null && value.isNotEmpty())
        this.setText(value)
}