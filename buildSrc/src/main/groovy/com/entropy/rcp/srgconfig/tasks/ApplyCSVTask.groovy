package com.entropy.rcp.srgconfig.tasks

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.TaskAction

class FixClassesTask {
    @InputFile File scriptFile
    @InputFile File csvFile
    CSVReader reader
    String[] nextLine
    int rowNumber = 0

    @TaskAction
    void exec() {

    }

    void applytoFile(File file, String srgName, String mcpName) throws IOException {
        Scanner scanner = new Scanner(file)
        StringBuffer buffer = new StringBuffer()
        while(scanner.hasNextLine()) {
            buffer.append(scanner.nextLine() + System.lineSeparator())
        }
        String contents = buffer.toString()
        scanner.close();

    }



}
