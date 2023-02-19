package com.ancientmc.srgconfig.tasks

import com.ancientmc.srgconfig.utils.Utils
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

/**
 * Adds the constructor info to a SRG file. This task merely appends all the constructor mappings to the end of the file.
 * Upon converting the mapping file to TSRGv2, the constructor data will be properly sorted with the rest of the mappings.
 */
class AddConstructors extends DefaultTask {
    @InputFile File srg
    @InputFile File constructors
    @OutputFile File newSrg

    @TaskAction
    void add() {
        Map<String, String> map = Utils.getClassMap(srg)
        List<String> lines = new ArrayList<>()
        constructors.text.eachLine { line ->
            String[] split = line.split(" ")

            String mcpMethod = split[0] + '/' + split[1]
            String mcpDesc = split[2]
            String notchClass = getNotchClass(map, split[0])
            String notchDesc = makeNotchDesc(map, mcpDesc)

            String newLine = ('MD: ' + notchClass + '/' + split[1] + ' ' + notchDesc + ' ' + mcpMethod + ' ' + mcpDesc)
            lines.add(newLine)
        }

        newSrg.withWriter { writer ->
            srg.text.eachLine { writer.write(it + '\n') }
            lines.each { writer.write(it + '\n') }
            writer.flush()
        }
    }

    String getNotchClass(Map<String, String> map, String mappedClass) {
        def entry = map.find { it.value == mappedClass }
        return entry.key
    }

    // Once again based on some code in the old Rgs2Srg program by LexTeam.
    // https://github.com/Lexteam/rgs2srg/blob/master/src/main/java/uk/jamierocks/rgs2srg/MappingsConverter.java
    String makeNotchDesc(Map<String, String> map, String mappedDesc) {
        String inner = mappedDesc.substring(mappedDesc.indexOf('(') + 1, mappedDesc.indexOf(')'))
        String outer = mappedDesc.substring(mappedDesc.indexOf(')') + 1)

        String notchDesc = mappedDesc

        for(String type : inner.split(';')) {
            if(type.contains('L')) {
                String innerType = type.substring(type.indexOf('L') + 1)
                if(map.containsValue(innerType)) {
                    notchDesc = notchDesc.replaceFirst(innerType, getNotchClass(map, innerType))
                }
            }
        }

        if(outer.startsWith('L')) {
            String outerType = outer.substring(1, outer.length() - 1)
            if(map.containsValue(outerType)) {
                notchDesc = notchDesc.replace(outerType, getNotchClass(map, outerType))
            }
        }

        return notchDesc
    }
}
