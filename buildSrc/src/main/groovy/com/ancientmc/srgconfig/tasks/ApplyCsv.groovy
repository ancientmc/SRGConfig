package com.ancientmc.srgconfig.tasks

import com.opencsv.CSVReader
import com.opencsv.CSVReaderBuilder
import com.opencsv.exceptions.CsvValidationException
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

class ApplyCsv extends DefaultTask {
    @InputFile File script
    @InputFile File csv

    @TaskAction
    void exec() throws CsvValidationException, IOException {
        List<String> lines = new ArrayList<>()
        Map<String, String> map = getMap()
        script.text.eachLine { line ->
            def matches = map.findAll { line.substring(line.lastIndexOf(' ') + 1) == it.key }
            matches.each { line = line.replaceAll(it.key, it.value) }
            lines.add(line)
        }

        script.withWriter('UTF-8') { writer ->
            lines.each {
                writer.write(it + '\n')
                writer.flush()
            }
        }
    }

    @Internal
    Map<String, String> getMap() {
        Map<String, String> map = new HashMap<>()
        String[] nextLine
        int rowNumber = 0
        CSVReader csvReader = new CSVReaderBuilder(new FileReader(csv)).withSkipLines(1).build()
        while((nextLine = csvReader.readNext()) != null) {
            rowNumber++
            for (int i = 1; i < nextLine.length; i++) {
                String srgName = nextLine[0]
                String mcpName = nextLine[1]
                map.put(srgName, mcpName)
            }
        }
        return map
    }
}