package org.haidash.visual.aco.oop;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.haidash.visual.aco.oop.entity.Graph;
import org.haidash.visual.aco.oop.entity.Link;
import org.haidash.visual.aco.oop.entity.Node;
import org.haidash.visual.aco.oop.entity.Properties;



public class InputFileReader {

	public Graph readGraph(final Graph graph, final Properties properties, final File file) {
		if ((file == null) || !file.exists()) {
			throw new AcoRuntimeException("Input file not fount");
		}

		try (Scanner text = new Scanner(new FileReader(file))) {

			final int graphSize = text.nextInt();
			final List<Node> nodes = new ArrayList<>(graphSize);

			graph.setGraphSize(graphSize);
			graph.setNodes(nodes);
			graph.setFuelLevels(new ArrayList<>(graphSize));

			for (int i = 0; i < graphSize; i++) {
				final int fuel = text.nextInt();

				graph.getFuelLevels().add(fuel);
				nodes.add(new Node(i, fuel));
			}

			final int linksSize = text.nextInt();
			final List<Link> links = new ArrayList<>(linksSize);

			graph.setLinks(links);

			for (int i = 0; i < linksSize; i++) {
				final int start = text.nextInt();
				final int finish = text.nextInt();

				final Node startNode = nodes.get(start);
				final Node finishNode = nodes.get(finish);

				Link link = new Link(text.nextInt());
				link.setFirst(startNode);
				link.setSecond(finishNode);

				links.add(link);
				startNode.getOutgoingLinks().add(link);
			}

			properties.setMaxFuelLevels(text.nextInt());

			graph.setStartNode(nodes.get(text.nextInt()));
			graph.setTargetNode(nodes.get(text.nextInt()));

		} catch (final FileNotFoundException e) {
			throw new AcoRuntimeException("Input file is inccorect", e);
		}

		return graph;
	}
}
