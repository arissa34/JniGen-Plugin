package com.github.arissa34.jnigenplugin.services

import com.intellij.openapi.project.Project
import com.github.arissa34.jnigenplugin.MyBundle

class MyProjectService(project: Project) {

    init {
        println(MyBundle.message("projectService", project.name))
    }
}
