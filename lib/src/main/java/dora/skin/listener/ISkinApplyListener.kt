package dora.skin.listener

import dora.skin.exception.ApplySkinException

interface ISkinApplyListener {

    fun onStart()
    fun onError(e: ApplySkinException)
    fun onComplete()

    class DefaultSkinApplyListener : ISkinApplyListener {
        override fun onStart() {}
        override fun onError(e: ApplySkinException) {}
        override fun onComplete() {}
    }

    companion object {
        val DEFAULT = DefaultSkinApplyListener()
    }
}