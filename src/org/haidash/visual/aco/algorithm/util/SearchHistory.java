package org.haidash.visual.aco.algorithm.util;

import com.carrotsearch.hppc.IntArrayList;
import org.haidash.visual.aco.algorithm.agent.Agent;
import org.haidash.visual.aco.algorithm.graph.entity.Link;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SearchHistory {

    private Solution firstSolution;
    private Solution bestSolution;
    private String graphName;
    private int graphSize;
    private int startNode;
    private int targetNode;
    private int alpha;
    private int beta;
    private int q;
    private int antCount;
    private int generationCount;
    private long runTime;

    public void addSolution(Solution solution) {

        if (this.firstSolution == null) {
            setFirstSolution(solution);
            setBestSolution(solution);
        } else {
            setBestSolution(solution);
        }
    }

    public boolean isPathFound() {
        return firstSolution != null;
    }

    public long getRunTime() {
        return runTime;
    }

    public void setRunTime(long runTime) {
        this.runTime = runTime;
    }

    public Solution getFirstSolution() {
        return firstSolution;
    }

    public void setFirstSolution(Solution firstSolution) {
        this.firstSolution = firstSolution;
    }

    public Solution getBestSolution() {
        return bestSolution;
    }

    public void setBestSolution(Solution bestSolution) {
        this.bestSolution = bestSolution;
    }

    public int getGraphSize() {
        return graphSize;
    }

    public void setGraphSize(int graphSize) {
        this.graphSize = graphSize;
    }

    public int getStartNode() {
        return startNode;
    }

    public void setStartNode(int startNode) {
        this.startNode = startNode;
    }

    public int getTargetNode() {
        return targetNode;
    }

    public void setTargetNode(int targetNode) {
        this.targetNode = targetNode;
    }

    public int getAlpha() {
        return alpha;
    }

    public void setAlpha(int alpha) {
        this.alpha = alpha;
    }

    public int getBeta() {
        return beta;
    }

    public void setBeta(int beta) {
        this.beta = beta;
    }

    public int getQ() {
        return q;
    }

    public void setQ(int q) {
        this.q = q;
    }

    public int getAntCount() {
        return antCount;
    }

    public void setAntCount(int antCount) {
        this.antCount = antCount;
    }

    public int getGenerationCount() {
        return generationCount;
    }

    public void setGenerationCount(int generationCount) {
        this.generationCount = generationCount;
    }

    public String getGraphName() {
        return graphName;
    }

    public void setGraphName(String graphName) {
        this.graphName = graphName;
    }
}
