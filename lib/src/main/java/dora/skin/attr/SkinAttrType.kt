package dora.skin.attr

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import dora.skin.SkinLoader
import dora.skin.SkinManager

enum class SkinAttrType(var attrType: String) {

    /**
     * 背景属性。
     */
    BACKGROUND("background") {
        override fun apply(view: View, resName: String) {
            val drawable = loader.getDrawable(resName)
            if (drawable != null) {
                view.setBackgroundDrawable(drawable)
            } else {
                val color = loader.getColor(resName)
                view.setBackgroundColor(color)
            }
        }
    },

    /**
     * 字体颜色。
     */
    TEXT_COLOR("textColor") {
        override fun apply(view: View, resName: String) {
            val colorStateList = loader.getColorStateList(resName) ?: return
            (view as TextView).setTextColor(colorStateList)
        }
    },

    /**
     * 图片资源。
     */
    SRC("src") {
        override fun apply(view: View, resName: String) {
            if (view is ImageView) {
                val drawable = loader.getDrawable(resName) ?: return
                view.setImageDrawable(drawable)
            }
        }
    };

    abstract fun apply(view: View, resName: String)

    /**
     * 获取资源管理器。
     */
    val loader: SkinLoader
        get() = SkinManager.getLoader()
}