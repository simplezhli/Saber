package com.zl.weilu.saber.bean

import com.zl.weilu.saber.annotation.LiveData

/**
 * @Description:
 * @Author: weilu
 * @Time: 2019/8/30 0030 10:34.
 */
@LiveData
class KotlinBean {

    // Int、Boolean等基础类型暂不支持。可使用对象包裹基础类型使用。
    var age: Long = 0
    var sex: Boolean? = false
    
}