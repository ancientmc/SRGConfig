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
    @InputDirectory File backup_src
    @InputDirectory File libraries
    @InputDirectory File natives
    @InputDirectory File patches
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
        map.put('{libraries}', getPath(libraries))
        map.put('{project_name}', 'client-' + version)
        map.put('{natives}', getPath(natives))
        map.put('{mc_dir}', getPath(mc_dir))
        map.put('{backup_src}', getPath(backup_src))
        map.put('{patches}', getPath(patches))

        return map
    }

    static def getPath(File file) {
        return file.absolutePath.replace('\\', '/') + '/'
    }
}
