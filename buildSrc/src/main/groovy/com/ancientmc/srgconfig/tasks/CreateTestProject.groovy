package com.ancientmc.srgconfig.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

/**
 * Creates runnable test project.
 */
class CreateTestProject extends DefaultTask {
    @Input String version
    @InputFiles File[] templates
    @InputDirectory File mc_dir
    @InputDirectory File libraries
    @InputDirectory File natives
    @OutputDirectory File outDir

    @TaskAction
    void exec() {
        templates.each { tmp ->
            def text = tmp.text
            def path = tmp.canonicalPath.substring(tmp.canonicalPath.lastIndexOf('templates\\') + 10)
            def dest = new File(outDir, path)
            if(!dest.exists()) dest.parentFile.mkdirs()
            getMap().each { text = text.replace(it.key, it.value) }
            dest.withWriter('UTF-8') {
                it.write(text)
                it.flush()
            }
        }
    }

    @Internal
    def getMap() {
        Map<String, String> map = new HashMap<>()
        map.put('{libraries}', libraries.absolutePath.replace('\\', '/') + '/')
        map.put('{project_name}', 'client-' + version)
        map.put('{natives}', natives.absolutePath.replace('\\', '/') + '/')
        map.put('{mc_dir}', mc_dir.absolutePath.replace('\\', '/') + '/')

        return map
    }
}
