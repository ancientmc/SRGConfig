package com.ancientmc.srgconfig.tasks

import net.minecraftforge.srgutils.IMappingBuilder
import net.minecraftforge.srgutils.IMappingFile
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

class Srg2Tsrg extends DefaultTask {
    @InputFile File srg
    @OutputFile File tsrg

    @TaskAction
    void convert() {
        IMappingFile.load(srg).write(tsrg.toPath(), IMappingFile.Format.TSRG2, false)
    }
}
