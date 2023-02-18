package com.ancientmc.srgconfig.tasks

import groovy.json.JsonSlurper
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

/**
 * Dumps a text file containing a list of all constructors for each class.
 * This text file is formatted in a similar way to how the constructors.txt file was in old versions of MCInjector: {class & method} {desc}
 * However, ACP itself won't be using a constructors.txt file during runtime; it'll only be used here for setting up the TSRG file.
 */
class DumpConstructors extends DefaultTask {
    @InputFile File json
    @OutputFile File constructors

    @TaskAction
    void dump() {
        constructors.withWriter('UTF-8') { writer ->
            getList().each { writer.write(it + '\n') }
        }
    }

    @Internal
    List<String> getList() {
        List<String> lines = new ArrayList<>()
        def jsonObj = new JsonSlurper().parse(json)
        jsonObj.each { clazz,data ->
            String className = data.name
            Collection<String> methods = data.methods?.keySet()?.findAll { (it.toString().contains('<init>') || it.toString().contains('<clinit>')) }
            methods.each { lines.add(className + " " + it as String) }
        }
        return lines
    }
}
