package com.ancientmc.srgconfig.tasks

import net.minecraftforge.srgutils.IMappingFile
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

/**
 * Replaces template exceptions file with SRG names into one with notch method/class names and descriptors.
 */
class UpdateExc extends DefaultTask {
    @InputFile File excOld // exceptions file with SRG names
    @InputFile File srg // SRG file
    @OutputFile File excNew // new exceptions file with notch names

    @TaskAction
    void exec() {
        FileWriter writer = new FileWriter(excNew)
        excOld.text.eachLine { line ->
            String[] split = line.split(" ")
            writer.write(newLine(line, split) + "\n")
            writer.flush()
        }
    }

    // split[0] = name of class + method
    // split[1] = desc of method
    String newLine(String line, String[] split) {
        IMappingFile mapping = IMappingFile.load(srg)
        String clazz = split[0].substring(0, split[0].lastIndexOf('/'))
        String method = split[0].substring(split[0].lastIndexOf('/') + 1)

        String originalClass = getOriginalClass(mapping, clazz)
        String originalMethod = getOriginalMethod(mapping, originalClass, method)
        String originalDesc = getOriginalDesc(mapping, originalClass, method)

        line = line.replace(split[0], originalClass + '/' + originalMethod)
        line = line.replace(split[1], originalDesc)

        return line
    }

    String getOriginalClass(IMappingFile mapping, String mappedClass) {
        def clazz = mapping.classes.find { it.mapped == mappedClass }
        if(clazz != null) return clazz.original
    }

    String getOriginalMethod(IMappingFile mapping, String originalClass, String mappedMethod) {
        def clazz = mapping.getClass(originalClass)
        def method = clazz.methods.find {it.mapped == mappedMethod }
        if(method != null) return method.original
    }

    String getOriginalDesc(IMappingFile mapping, String originalClass, String mappedMethod) {
        def clazz = mapping.getClass(originalClass)
        def method = clazz.methods.find { it.mapped == mappedMethod }
        if(method != null) return method.descriptor
    }
}
