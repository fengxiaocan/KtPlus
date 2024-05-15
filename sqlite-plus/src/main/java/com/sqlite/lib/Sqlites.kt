package com.sqlite.lib

import android.content.Context

fun Context.openDatabase(){
    this.openOrCreateDatabase()
}