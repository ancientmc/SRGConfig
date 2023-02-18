package com.ancientmc.srgconfig.tasks

import de.undercouch.gradle.tasks.download.DownloadAction
import groovy.json.JsonSlurper
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

class DownloadLibraries extends DefaultTask {
    @InputFile File json
    @OutputDirectory File libraries
    @OutputFile File list

    @TaskAction
    void exec() {
        def jsonObj = new JsonSlurper().parse(json)
        List<String> locs = new ArrayList<>()
        jsonObj.libraries.each { lib ->
            // Tuple partially from https://github.com/MinecraftForge/MCPConfig/blob/master/buildSrc/src/main/groovy/net/minecraftforge/mcpconfig/tasks/DownloadLibraries.groovy
            def artifacts = (lib.downloads.artifact == null ? [] : [lib.downloads.artifact])
            artifacts.each { art ->
                String location = libraries.absolutePath + '/' + art.path
                download(art.url, location)
                locs.add(location)
            }
        }
        writeList(locs)
    }

    void download(String url, String location) {
        File file = new File(location)
        def action = new DownloadAction(project, this)
        action.src(url)
        action.dest(file)
        action.execute()
    }

    void writeList(List<String> locs) {
        def writer = new FileWriter(list)
        locs.each { loc ->
            def line = '-e=' + loc + '\n'
            writer.write(line)
            writer.flush()
        }
    }
}
