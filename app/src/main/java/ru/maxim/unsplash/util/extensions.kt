package ru.maxim.unsplash.util

import android.content.Context
import android.widget.TextView
import android.widget.Toast

fun TextView.setDrawableStart(drawableResource: Int) = setCompoundDrawablesRelativeWithIntrinsicBounds(drawableResource, 0, 0, 0)
fun TextView.setDrawableEnd(drawableResource: Int) = setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, drawableResource, 0)
fun TextView.clearDrawables() = setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)

fun Context.toast(text: String?) { Toast.makeText(this, text, Toast.LENGTH_SHORT).show() }
fun Context.toast(resource: Int) { Toast.makeText(this, getString(resource), Toast.LENGTH_SHORT).show() }
fun Context.longToast(text: String?) { Toast.makeText(this, text, Toast.LENGTH_LONG).show() }
fun Context.longToast(resource: Int) { Toast.makeText(this, getString(resource), Toast.LENGTH_LONG).show() }