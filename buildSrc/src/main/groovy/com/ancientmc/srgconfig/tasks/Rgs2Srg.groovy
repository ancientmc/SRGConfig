package com.ancientmc.srgconfig.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

/**
 * Converts the old rgs script files into more modern SRG files. Mainly for older versions that need them.
 * This will replace the old Rgs2Srg program by JamieRocks/LexTeam. More work needs to ensure this version runs
 * smoothly.
 */
class Rgs2Srg extends DefaultTask {
    @InputFile File rgs
    @OutputFile File srg

    @Internal List<String> mappings = new ArrayList<>()
    @Internal Map<String, String> classMap = new HashMap<>()

    @TaskAction
    void convert() {
        FileWriter writer = new FileWriter(srg)
        for(String line : getMappings()) {
            writer.write(line + System.lineSeparator())
        }
        writer.flush()
    }

    List<String> getMappings() {
        Scanner scanner = new Scanner(rgs)
        while(scanner.hasNext()) {
            String line = scanner.nextLine()
            if(line.startsWith(".class_map")) {
                String[] classSplit = line.split(" ")
                String classLine = getClassLine(classSplit)
                mappings.add(classLine)
            } else if(line.startsWith(".field_map")) {
                String[] fieldSplit = line.split(" ")
                String fieldLine = getFieldLine(fieldSplit)
                mappings.add(fieldLine)
            } else if(line.startsWith(".method_map")) {
                String[] methodSplit = line.split(" " )
                String methodLine = getMethodLine(methodSplit)
                mappings.add(methodLine)
            }
        }
        return mappings
    }

    String getClassLine(String[] classSplit) {
        String notchClass = classSplit[1]
        String mcpClass = classSplit[2]
        return String.format("CL: " + notchClass + " " + mcpClass)
    }

    String getFieldLine(String[] fieldSplit) {
        String notchField = fieldSplit[1]
        String srgField = fieldSplit[2]
        String notchClass = notchField.substring(0, notchField.lastIndexOf('/'))
        String mcpClass = getMcpClass(notchClass)
        return String.format("FD: " + notchField + " " + mcpClass + "/" + srgField)
    }

    String getMethodLine(String[] methodSplit) {
        String notchMethod = methodSplit[1]
        String notchClass = notchMethod.substring(0, notchMethod.lastIndexOf('/'))
        String notchSig = methodSplit[2]
        String mcpClass = getMcpClass(notchClass)
        String srgMethod = methodSplit[3]
        String srgSig = getSrgSignature(notchSig)
        return String.format("MD: " + notchMethod + " " + notchSig + " " + mcpClass + "/" + srgMethod + " " + srgSig)
    }

    Map<String, String> getClassMap() {
        Scanner scanner = new Scanner(rgs)
        while(scanner.hasNext()) {
            String line = scanner.nextLine()
            if(line.startsWith(".class_map")) {
                String[] split = line.split(" ")
                String notchClass = split[1]
                String mcpClass = split[2]
                classMap.put(notchClass, mcpClass)
            }
        }
        return classMap
    }

    String getMcpClass(String notchClass) {
        Map.Entry entry = getClassMap().entrySet().find { it.key == notchClass }
        return entry.value
    }

    // This was mostly taken from the original Rgs2Srg
    // https://github.com/Lexteam/rgs2srg/blob/master/src/main/java/uk/jamierocks/rgs2srg/MappingsConverter.java
    String getSrgSignature(String notchSig) {
        String inner = notchSig.substring(notchSig.indexOf('(') + 1, notchSig.indexOf(')'))
        String outer = notchSig.substring(notchSig.indexOf(')') + 1)

        String srgSig = notchSig

        for(String type : inner.split(';')) {
            if(type.startsWith('L')) {
                String innerType = type.substring(1)
                if (getClassMap().containsKey(innerType)) {
                    srgSig = srgSig.replace(innerType, getMcpClass(innerType))
                }
            }
        }

        if(outer.startsWith('L')) {
            String outerType = outer.substring(1, outer.length() - 1)
            if(getClassMap().containsKey(outerType)) {
                srgSig = srgSig.replace(outerType, getMcpClass(outerType))
            }
        }
        return srgSig
    }
}
