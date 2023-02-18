package com.ancientmc.srgconfig.tasks

import com.opencsv.CSVReader
import com.opencsv.CSVReaderBuilder
import com.opencsv.exceptions.CsvValidationException
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.TaskAction

// TODO: Make this way faster because it's really slow and I hate it. It's probably because of my shitty older code, but it also
// TODO: may be because of how Java parses text in general. Forge keeps things contained in ZIP files when parsing CSVs, and I'm beginning
// TODO: to understand why. Rewrite will be soon, probably using Groovy stuff. Also, can you not do multi-line TODOs?
class ApplyCsv extends DefaultTask {
    @InputFile File scriptFile
    @InputFile File csvFile

    @TaskAction
    void exec() throws CsvValidationException, IOException {
        String srgName = ""
        String mcpName = ""
        String[] nextLine
        int rowNumber = 0
        CSVReader csvReader = new CSVReaderBuilder(new FileReader(csvFile)).withSkipLines(1).build()

        while((nextLine = csvReader.readNext()) != null) {
            rowNumber++
            for (int i = 1; i < nextLine.length; i++) {
                srgName = nextLine[0]
                mcpName = nextLine[1]
                Scanner scanner = new Scanner(scriptFile)
                StringBuffer buffer = new StringBuffer()
                while (scanner.hasNextLine()) {
                    buffer.append(scanner.nextLine() + System.lineSeparator())
                }
                String contents = buffer.toString()
                scanner.close()
                contents = contents.replaceAll(srgName, mcpName)
                FileWriter writer = new FileWriter(scriptFile)
                writer.append(contents)
                writer.flush()
            }
        }
    }
}