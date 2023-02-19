package com.ancientmc.srgconfig.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

import java.util.stream.Collectors

class CreateParamsCsv extends DefaultTask {
    @InputFile File tsrg
    @OutputFile File csv

    @TaskAction
    void create() {
        List<String> params = new ArrayList<>()
        tsrg.text.eachLine {
            if(it.contains('p_')) {
                String[] split = it.split(' ')
                params.add(split[2])
            }
        }

        // Sort the list
        Collections.sort(params)

        // Remove duplicates
        params = params.stream().distinct().collect(Collectors.toList())

        csv.withWriter('UTF-8') { writer ->
            writer.write('SRG,MCP' + '\n')
            params.each { writer.write(it + ',' + '\n') }
        }
    }
}
