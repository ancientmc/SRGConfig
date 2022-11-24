package com.entropy.rcp.srgconfig.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.TaskAction

class RemoveUnusedLinesTask extends DefaultTask {
    @InputFile File scriptFile

    @TaskAction
    void exec() {
        File tempFile = new File("data\\temp.srg")
        String currentLine
        String paulscodePkg = "paulscode/"
        String jcraftPkg = "com/jcraft/"
        String valuesMethods = "_values"
        BufferedReader reader = new BufferedReader(new FileReader(scriptFile))
        BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))

        while((currentLine = reader.readLine()) != null) {
            String t = currentLine.trim()
            if(t.contains(paulscodePkg) || t.contains(jcraftPkg) || t.contains(valuesMethods)) {
                continue
            }
            writer.write(currentLine + System.getProperty("line.separator"))
        }
        writer.close()
        reader.close()
        scriptFile.delete()
        tempFile.renameTo(scriptFile)
    }
}