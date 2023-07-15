package dora.skin.attr

import android.view.View

/**
 * 要换肤的控件。
 */
class SkinView(
    var view: View?, var attrs: List<SkinAttr>
) {
    fun apply() {
        for (attr in attrs) {
            view?.let {
                attr.apply(it)
            }
        }
    }
}