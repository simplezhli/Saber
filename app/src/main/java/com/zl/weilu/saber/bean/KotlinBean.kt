package com.zl.weilu.saber.bean

import com.zl.weilu.saber.annotation.LiveData

/**
 * @Description:
 * @Author: weilu
 * @Time: 2019/8/30 0030 10:34.
 */

class KotlinBean {

    @LiveData
    var i: Int = 0
    @LiveData
    var s: Short = 0
    @LiveData
    var bt: Byte = 0
    @LiveData
    var c: Char = 's'
    @LiveData
    var f: Float = 0f
    @LiveData
    var d: Double = 0.0
    @LiveData
    var l: Long = 0
    @LiveData
    var b: Boolean? = false
}