package com.ancientmc.srgconfig.tasks

import de.undercouch.gradle.tasks.download.DownloadAction
import groovy.json.JsonSlurper
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

import java.util.stream.Collectors

class ExtractNatives extends DefaultTask {
    @InputFile File json
    @OutputDirectory File nativesDir

    @TaskAction
    void exec() {
        List<File> jars = new ArrayList<>()
        def jsonObj = new JsonSlurper().parse(json)
        jsonObj.libraries.findAll { it.downloads.classifiers != null }.each { lib ->
            def classifiers = lib.downloads.classifiers
            classifiers.findAll { it.key.contains(getOSName()) }.each { cls ->
                def url = cls.value.url
                def path = cls.value.path
                def file = new File(nativesDir, path)
                download(url, file)
                jars.add(file)
            }
        }

        jars = jars.stream().distinct().collect(Collectors.toList())

        jars.each { jar ->
            project.copy { c ->
                c.from(project.zipTree(jar))
                c.into(nativesDir)
            }
        }
    }

    void download(def url, def file) {
        def action = new DownloadAction(project, this)
        action.src(url)
        action.dest(file)
        action.execute()
    }

    // Derived from https://github.com/MinecraftForge/MCPConfig/blob/master/buildSrc/src/main/groovy/net/minecraftforge/mcpconfig/tasks/Utils.groovy
    static String getOSName() {
        String name = System.getProperty('os.name').toLowerCase(Locale.ENGLISH)
        if(name.contains('windows') || name.contains('win')) return 'windows'
        if(name.contains('mac') || name.contains('osx')) return 'osx'
        if(name.contains('linux')) return 'linux'
        return 'unknown'
    }
}
