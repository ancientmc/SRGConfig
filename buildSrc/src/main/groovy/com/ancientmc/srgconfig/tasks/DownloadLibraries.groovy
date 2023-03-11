package com.ancientmc.srgconfig.tasks

import de.undercouch.gradle.tasks.download.DownloadAction
import groovy.json.JsonSlurper
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.internal.os.OperatingSystem

class DownloadLibraries extends DefaultTask {
    @InputFiles File[] jsons
    @OutputDirectory File libraries

    @TaskAction
    void exec() {
        jsons.each { json ->
            def jsonObj = new JsonSlurper().parse(json)
            jsonObj.libraries.each { lib ->
                // Tuple partially from https://github.com/MinecraftForge/MCPConfig/blob/master/buildSrc/src/main/groovy/net/minecraftforge/mcpconfig/tasks/DownloadLibraries.groovy
                def artifacts = (lib.downloads.artifact == null ? [] : [lib.downloads.artifact])
                artifacts.findAll { isAllowed(it.path) }.each { art ->
                    String location = libraries.absolutePath + '/' + art.path
                    download(art.url, location)
                }
            }
        }
    }

    void download(String url, String location) {
        File file = new File(location)
        def action = new DownloadAction(project, this)
        action.src(url)
        action.dest(file)
        action.execute()
    }

    /*
     * The lwjgl versions seem to barely change in the JSONs, remaining similar at least as late as 1.6.4.
     * So I'm just going to say fuck it and hardcode the version numbers in for parsing.
     * It's sloppy, but it works, and I couldn't get anything else to work.
     */
    static def isAllowed(def path) {
        def lwjgl = '2.9.0'
        def lwjglMac = '2.9.1-debug'
        if(!path.contains('org/lwjgl')) return true
        if(OperatingSystem.current().isMacOsX()) {
            if(path.contains(lwjglMac)) {
                return true
            }
        } else {
            if(path.contains(lwjgl)) {
                return true
            }
        }
        return false
    }
}