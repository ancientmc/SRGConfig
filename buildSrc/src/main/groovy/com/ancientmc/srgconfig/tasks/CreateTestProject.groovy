package com.ancientmc.srgconfig.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

/**
 * Creates runnable test project.
 */
class CreateTestProject extends DefaultTask {
    @Input String version
    @InputFile File extras
    @InputDirectory File libraries
    @InputFiles File[] gradles
    @OutputDirectory File outDir

    @TaskAction
    void exec() {
        gradles.each { grd ->
            def text = grd.text
            def dest = new File(outDir, grd.name)
            text = text.replace('{libraries}', libraries.absolutePath.replace('\\', '/') + '/')
            text = text.replace('{project_name}', 'client-' + version)
            text = text.replace('{jar_assets}', extras.absolutePath.replace('\\', '/'))
            dest.withWriter('UTF-8') {
                it.write(text)
                it.flush()
            }
        }
    }
}
