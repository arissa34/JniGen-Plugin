package com.github.arissa34.jnigenplugin.extention

import com.intellij.psi.PsiComment
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiPlainText


fun PsiElement.isJniComment(): Boolean {
    return this.isCommentHighlightingElement() && this.text.startWthiJNI()
}

fun PsiElement.isCommentHighlightingElement(): Boolean {
    return this.isCommentType() || this.isPlainTextHighlight()
}

fun String.startWthiJNI() = this.startsWith("/*JNI")

fun PsiElement.isPlainTextHighlight(): Boolean = this is PsiPlainText

fun PsiElement.isCommentType(): Boolean = this is PsiComment

fun PsiElement.isNativeJniFunctionComment(): Boolean {
    if (this.javaClass.simpleName.contains("PsiComment")) {
        var isNativeFunction = false

        this.parent?.let {
            isNativeFunction = it.text.contains("native")
        }
        return isNativeFunction
    }
    return false
}