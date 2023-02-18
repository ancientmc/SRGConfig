package com.ancientmc.srgconfig.tasks

import de.undercouch.gradle.tasks.download.DownloadAction
import groovy.json.JsonSlurper
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

class DownloadJar extends DefaultTask {
    @InputFile File json
    @OutputFile File jar

    @TaskAction
    void exec() {
        def jsonObj = new JsonSlurper().parse(json)
        String url = jsonObj.downloads.client.url
        download(url, jar)
    }

    def download(String url, File file) {
        def action = new DownloadAction(project, this)
        action.src(url)
        action.dest(file)
        action.execute()
    }
}
