package dora.skin

import android.view.View
import android.widget.ImageView
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes

/**
 * 这个接口提供了一组从皮肤资源包中加载和显示图片和颜色资源的API。
 */
interface ISkinLoader {

    /**
     * @see ImageView.setImageDrawable
     */
    fun setImageDrawable(imageView: ImageView, resName: String)

    /**
     * @see ImageView.setImageDrawable
     */
    fun setImageDrawable(imageView: ImageView, resName: String?, @DrawableRes fallback: Int)

    /**
     * @see View.setBackgroundDrawable
     */
    fun setBackgroundDrawable(view: View, resName: String)

    /**
     * @see View.setBackgroundDrawable
     */
    fun setBackgroundDrawable(view: View, resName: String?, @DrawableRes fallback: Int)

    /**
     * @see View.setBackgroundColor
     */
    fun setBackgroundColor(view: View, resName: String)

    /**
     * @see View.setBackgroundColor
     */
    fun setBackgroundColor(view: View, resName: String?, @ColorInt fallback: Int)
}