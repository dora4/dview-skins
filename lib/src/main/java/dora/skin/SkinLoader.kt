package dora.skin

import android.content.res.Resources.NotFoundException
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.view.View
import android.widget.ImageView

class SkinLoader(
    private val resources: Resources, private val pluginPkgName: String, private
        val suffix: String? = "") : ISkinLoader {

    fun getBitmap(resName: String): Bitmap? {
        return BitmapFactory.decodeResource(
            resources,
            resources.getIdentifier(
                appendSuffix(resName),
                DEF_TYPE_DRAWABLE,
                pluginPkgName
            )
        )
    }

    fun getDrawable(resName: String): Drawable? {
        return try {
            resources.getDrawable(
                resources.getIdentifier(
                    appendSuffix(resName),
                    DEF_TYPE_DRAWABLE,
                    pluginPkgName
                )
            )
        } catch (e: NotFoundException) {
            e.printStackTrace()
            null
        }
    }

    fun getColor(resName: String): Int {
        return try {
            resources.getColor(resources.getIdentifier(appendSuffix(resName), DEF_TYPE_COLOR, pluginPkgName))
        } catch (e: NotFoundException) {
            e.printStackTrace()
            -1
        }
    }

    fun getColorStateList(resName: String): ColorStateList? {
        return try {
            resources.getColorStateList(
                resources.getIdentifier(
                    appendSuffix(resName),
                    DEF_TYPE_COLOR,
                    pluginPkgName
                )
            )
        } catch (e: NotFoundException) {
            e.printStackTrace()
            null
        }
    }

    /**
     * 追加皮肤后缀。
     */
    private fun appendSuffix(name: String): String {
        var nameWithSuffix = name
        if (!TextUtils.isEmpty(suffix)) nameWithSuffix += "_$suffix"
        return nameWithSuffix
    }

    override fun setImageDrawable(imageView: ImageView, resName: String) {
        val drawable = getDrawable(resName) ?: return
        imageView.setImageDrawable(drawable)
    }

    override fun setBackgroundDrawable(view: View, resName: String) {
        val drawable = getDrawable(resName) ?: return
        view.background = drawable
    }

    companion object {
        private const val DEF_TYPE_DRAWABLE = "drawable"
        private const val DEF_TYPE_COLOR = "color"
    }
}