package dora.skin

import android.view.View
import android.widget.ImageView

/**
 * 这个接口提供了一组从皮肤资源包中加载和显示图片和颜色资源的API。
 */
interface ISkinLoader {

    /**
     * @see ImageView.setImageDrawable
     */
    fun setImageDrawable(imageView: ImageView, resName: String)

    /**
     * @see View.setBackgroundDrawable
     */
    fun setBackgroundDrawable(view: View, resName: String)
}