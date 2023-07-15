package dora.skin.exception

class ApplySkinException(skinPath: String, skinPkgName: String, suffix: String?)
    : IllegalArgumentException("skinPath:$skinPath,skinPkgName:$skinPkgName,suffix:$suffix")