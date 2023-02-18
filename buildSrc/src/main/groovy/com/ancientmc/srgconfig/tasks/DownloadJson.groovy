package com.ancientmc.srgconfig.tasks

import groovy.json.JsonSlurper
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import de.undercouch.gradle.tasks.download.DownloadAction

class DownloadJson extends DefaultTask {
    @InputFile File manifest
    @Input version
    @OutputFile File json

    @TaskAction
    def exec() {
        def manifestJson = new JsonSlurper().parse(manifest)
        def location = manifestJson.versions.find { it -> it.id == version }
        String url = location.url
        download(url, json)
    }

    def download(String url, File file) {
        def action = new DownloadAction(project, this)
        action.src(url)
        action.dest(file)
        action.execute()
    }
}
