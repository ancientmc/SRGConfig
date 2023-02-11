package com.ancientmc.srgconfig.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

/**
 * Removes unwanted lines from the RGS file.
 */
class RemoveUnwantedLines extends DefaultTask {
    @InputFile File input
    @OutputFile File output


    @TaskAction
    void exec() {
        String currentLine
        String paulscodePkg = "paulscode/"
        String jcraftPkg = "com/jcraft/"
        BufferedReader reader = new BufferedReader(new FileReader(input))
        BufferedWriter writer = new BufferedWriter(new FileWriter(output))

        while((currentLine = reader.readLine()) != null) {
            String t = currentLine.trim()
            if(t.contains(paulscodePkg) || t.contains(jcraftPkg)) {
                continue
            }
            writer.write(currentLine + System.getProperty("line.separator"))
        }
        writer.close()
        reader.close()
    }
}