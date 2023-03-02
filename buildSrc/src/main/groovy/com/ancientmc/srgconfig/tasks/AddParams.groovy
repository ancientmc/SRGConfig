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
    @InputFile File csv // CSV file with Class ids
    @OutputFile File newTsrg // output TSRGv2 file with parameters via the getConstructorId method in the Utils class.

    // Descriptor regex for methods
    @Internal Pattern descRgx = Pattern.compile("\\([^)]*\\).+")

    @TaskAction
    void exec() {
        List<String> lines = new ArrayList<>()
        String className
        int iterator // iterator for constructors in a class
        tsrg.text.eachLine { line ->
            if(line.contains('net/minecraft') && !line.contains(";")) {
                String[] split = line.split(' ')
                className = split[1] // store current class being parsed
                iterator = 0 // reset iterator for each class
            }
            String[] split = line.split(' ')
            String methodDesc = split[1]
            Matcher matcher = descRgx.matcher(methodDesc)
            if(matcher.matchesPartially()) {
                String methodName = split[2]
                if(methodName.contains('init>')) iterator = iterator + 1 // increase iterator for each instance of a constructor (even ones w/o params)
                String mappedDesc = Utils.getMappedDesc(tsrg, methodName, methodDesc) // SRG descriptor needed to match with the param map.
                String params = getParams(className, methodName, mappedDesc, jar, csv, iterator)
                line = line + params
            }
            lines.add(line)
        }
        newTsrg.withWriter('UTF-8') { writer ->
            lines.each { writer.write(it + '\n') }
        }
    }

    static String getParams(String className, String methodName, String mappedDesc, File jar, File csv, int iterator) {
        List<String> paramList = new ArrayList<>()
        def params = GetParamMap.getMap(jar)
        def entry = params.find { it.key == methodName + ' ' + mappedDesc }
        for(int i = 0; i < entry.value; i++) {
            String methodId
            if(methodName.contains("func_")) {
                String[] mSplit = methodName.split("_")
                methodId = mSplit[1] // normal method params just use their SRG ids (func_113_c would have params p_113_x)
            } else if(methodName.contains("init>")) { // constructor params have an id based on the class id
                def classIdMap = Utils.getClassIdMap(csv)
                def classEntry = classIdMap.find { it.value == className }
                methodId = 'i' + (classEntry.key.toInteger() + iterator).toString()
            } else methodId = methodName // named parameters have
            String param = "\n\t\t" + i + " o " + "p_" + methodId + "_" + i
            paramList.add(param)
        }
        StringBuilder builder = new StringBuilder()
        paramList.each { builder.append(it) }
        return builder.toString()
    }
}