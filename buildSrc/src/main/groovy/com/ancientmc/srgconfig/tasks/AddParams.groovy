package com.ancientmc.srgconfig.tasks

import com.ancientmc.srgconfig.utils.GetParamMap
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * Adds parameters to the inputted TSRG file by reading a corresponding, already mapped JAR.
 */
class AddParams extends DefaultTask {
    @InputFile File tsrg // input TSRGv2 file without parameters.
    @InputFile File jar // remapped jar
    @OutputFile File tsrgP // output TSRGv2 file with parameters.

    // Descriptor regex for methods
    @Internal Pattern descRgx = Pattern.compile("\\([^)]*\\)[A-Za-z0-9]+")

    @TaskAction
    void exec() {
        FileWriter writer = new FileWriter(tsrgP)
        tsrg.text.eachLine { line ->
            String[] split = line.split(" ")
            String methodDesc = split[1]
            Matcher matcher = descRgx.matcher(methodDesc)
            if(matcher.matchesPartially()) {
                String methodName = split[2]
                String params = getParams(methodName, jar)
                line = line + params
                writer.append(line + "\n")
            } else {
                writer.append(line + "\n")
            }
            writer.flush()
        }
    }

    static String getParams(String methodName, File jar) {
        List<String> paramList = new ArrayList<>()
        def params = GetParamMap.getMap(jar)
        def entry = params.find { it.key == methodName }
        for(int index : entry.value) {
            String methodId
            if(methodName.contains("func_")) {
                String[] mSplit = methodName.split("_")
                methodId = mSplit[1]
            } else methodId = methodName
            String param = "\n\t\t" + index + " o " + "p_" + methodId + "_" + index
            paramList.add(param)
        }
        StringBuilder builder = new StringBuilder()
        paramList.each { builder.append(it) }
        return builder.toString()
    }
}
