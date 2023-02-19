package com.ancientmc.srgconfig.tasks

import com.ancientmc.srgconfig.utils.GetParamMap
import com.ancientmc.srgconfig.utils.Utils
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
    @OutputFile File tsrgP // output TSRGv2 file with parameters via the getConstructorId method in the Utils class.

    // Descriptor regex for methods
    @Internal Pattern descRgx = Pattern.compile("\\([^)]*\\).+")

    @TaskAction
    void exec() {
        int ctrId // numerical constructor id
        FileWriter writer = new FileWriter(tsrgP)
        tsrg.text.eachLine { line ->
            ctrId++
            String[] split = line.split(" ")
            String methodDesc = split[1]
            Matcher matcher = descRgx.matcher(methodDesc)
            if(matcher.matchesPartially()) {
                String methodName = split[2]
                String params = getParams(methodName, methodDesc, tsrg, jar, ctrId)
                line = line + params
                writer.append(line + "\n")
            } else {
                writer.append(line + "\n")
            }
            writer.flush()
        }
    }

    static String getParams(String methodName, String methodDesc, File tsrg, File jar, int ctrId) {
        List<String> paramList = new ArrayList<>()
        String mappedDesc = Utils.getMappedDesc(tsrg, methodName, methodDesc) // SRG descriptor needed to match with the param map.
        def params = GetParamMap.getMap(jar)
        def entry = params.find { it.key == methodName + ' ' + mappedDesc }
        for(int i = 0; i < entry.value; i++) {
            String methodId
            if(methodName.contains("func_")) {
                String[] mSplit = methodName.split("_")
                methodId = mSplit[1]
            } else if(methodName.contains("init>")) {
                methodId = 'i' + ctrId.toString() // constructor parameters have the format: p_i##_0
            } else methodId = methodName // named parameters have
            String param = "\n\t\t" + i + " o " + "p_" + methodId + "_" + i
            paramList.add(param)
        }
        StringBuilder builder = new StringBuilder()
        paramList.each { builder.append(it) }
        return builder.toString()
    }
}
