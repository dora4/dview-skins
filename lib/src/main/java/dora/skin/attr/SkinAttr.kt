package dora.skin.attr

import android.view.View

/**
 * 皮肤属性。
 */
class SkinAttr(

    /**
     * 属性类型。
     */
    var attrType: SkinAttrType,

    /**
     * 资源名。
     */
    var resName: String
) {
    /**
     * 把皮肤的属性应用到View上。
     */
    fun apply(view: View) {
        attrType.apply(view, resName)
    }
}