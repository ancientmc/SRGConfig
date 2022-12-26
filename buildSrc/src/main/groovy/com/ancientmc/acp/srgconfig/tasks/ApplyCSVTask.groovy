package com.ancientmc.acp.srgconfig.tasks

import com.opencsv.CSVReader
import com.opencsv.CSVReaderBuilder
import com.opencsv.exceptions.CsvValidationException
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.TaskAction


class ApplyCSVTask extends DefaultTask {
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