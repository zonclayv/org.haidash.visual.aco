package org.haidash.visual.aco.oop;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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

			File outputFile = new File("files/new/file" + System.currentTimeMillis() + ".txt");
			outputFile.createNewFile();

			try (BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile));) {

			for (int i = 0; i < linksSize; i++) {
				final int start = text.nextInt();
				final int finish = text.nextInt();

				int fuelCost = text.nextInt();
					bw.write((start + graphSize) + " " + (finish + graphSize) + " " + fuelCost + "\n");
				}

			} catch (IOException e) {
				throw new AcoRuntimeException("Error via write file", e);
			}

		} catch (final FileNotFoundException e) {
			throw new AcoRuntimeException("Input file is inccorect", e);
		} catch (IOException e) {
			throw new AcoRuntimeException("Error via write file", e);
		}
	}
}
