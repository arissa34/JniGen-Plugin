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
    if (!this.javaClass.simpleName.contains("PsiMethod")) return false
    var isNativeFunction = false
    var hasComment = false
    for (children in this.children) {
        if (children.javaClass.simpleName.contains("PsiModifierList") && children.text.contains("static native")) {
            isNativeFunction = true
        }
        if (isNativeFunction && children.javaClass.simpleName.contains("PsiComment") && children.text.contains("/*\n")) {
            return true
        }
    }
    return isNativeFunction && hasComment
}

fun PsiElement.getCommentText(): PsiElement? {
    var isNativeFunction = false
    for (children in this.children) {
        if (children.javaClass.simpleName.contains("PsiModifierList") && children.text.contains("static native")) {
            isNativeFunction = true
        }
        if (isNativeFunction && children.javaClass.simpleName.contains("PsiComment") && children.text.contains("/*\n")) {
            return children
        }
    }
    return null
}