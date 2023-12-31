package org.haidash.visual.aco.generator;

import org.haidash.visual.aco.AcoRuntimeException;

import java.io.*;
import java.util.Scanner;


public class IncrementLinkNumber {

    public static void main(final String[] args) {
        writeNewFile(new File("files/by.txt"));
    }

    public static void writeNewFile(final File file) {

        if ((file == null) || !file.exists()) {
            throw new AcoRuntimeException("Input file not fount");
        }

        try (Scanner text = new Scanner(new FileReader(file))) {

            final int graphSize = text.nextInt();

            for (int i = 0; i < graphSize; i++) {
                final int fuel = text.nextInt();
            }

            final int linksSize = text.nextInt();

            final File outputFile = new File("files/new/file" + System.currentTimeMillis() + ".txt");
            outputFile.createNewFile();

            try (BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile));) {

                for (int i = 0; i < linksSize; i++) {
                    final int start = text.nextInt();
                    final int finish = text.nextInt();

                    int fuelCost = text.nextInt();

                    bw.write((start + graphSize) + " " + (finish + graphSize) + " " + fuelCost + "\n");
                }

            } catch (IOException e) {
                throw new AcoRuntimeException("Error via writeHistory file", e);
            }

        } catch (final FileNotFoundException e) {
            throw new AcoRuntimeException("Input file is incorrect", e);
        } catch (IOException e) {
            throw new AcoRuntimeException("Error via writeHistory file", e);
        }
    }
}
