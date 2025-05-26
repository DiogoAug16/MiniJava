package com.minijava.semantic;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class SemanticLogger {

    private BufferedWriter writer;

    public SemanticLogger() {
        try {
            File dir = new File("output/log");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            writer = new BufferedWriter(new FileWriter("output/log/log.txt", false));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void log(String message) {
        try {
            writer.write(message);
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            if (writer != null)
                writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
