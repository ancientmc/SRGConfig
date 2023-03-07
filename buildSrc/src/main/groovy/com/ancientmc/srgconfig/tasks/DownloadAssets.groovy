package com.ancientmc.srgconfig.tasks

import de.undercouch.gradle.tasks.download.DownloadAction
import groovy.json.JsonSlurper
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

class DownloadAssets extends DefaultTask {
    @InputFile File json
    @OutputDirectory File buildDir
    @OutputDirectory File resources

    @TaskAction
    void exec() {
        def jsonObj = new JsonSlurper().parse(json)
        def indexUrl = jsonObj.assetIndex.url
        def path = indexUrl.toString().substring(indexUrl.lastIndexOf('/') + 1)
        def file = new File(buildDir.absolutePath + '/game_data/', path)
        download(indexUrl, file)
        getAssets(file)
    }

    void getAssets(def index) {
        def indexObj = new JsonSlurper().parse(index)
        Map<Object, Object> assets = new HashMap<>()
        indexObj.objects.each { asset ->
            def name = asset.key
            def hash = asset.value.hash
            assets.put(name, hash)
        }

        assets.each { asset ->
            def path = asset.value.take(2) + '/' + asset.value
            def url = 'https://resources.download.minecraft.net/' + path
            def file = new File(resources, asset.key.toString())
            if(!file.exists()) file.parentFile.mkdirs()
            project.logger.lifecycle('Download ' + asset.key.toString())
            writeToFile(new URL(url).openStream(), new FileOutputStream(file))
        }
    }

    void download(def url, def file) {
        def action = new DownloadAction(project, this)
        action.src(url)
        action.dest(file)
        action.execute()
    }

    static void writeToFile(InputStream input, OutputStream output) throws IOException {
        byte[] b = new byte[1024]
        int len
        while((len = input.read(b)) > 0)
            output.write(b, 0, len)

        input.close()
        output.close()
    }
}
