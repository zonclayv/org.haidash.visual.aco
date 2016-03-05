package org.haidash.visual.aco.algorithm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.*;

/**
 * Created by haidash on 05.03.16.
 */
public class BFS {

    private int n; //количество вершин в орграфе
    private int m; //количество дуг в орграфе
    private List<Integer>[] adj; //список смежности
    private boolean used[]; //массив для хранения информации о пройденных и не пройденных вершинах
    private Queue<Integer> queue; //очередь для добавления вершин при обходе в ширину

    private BufferedReader cin;
    private PrintWriter cout;
    private StringTokenizer tokenizer;

    //процедура обхода в ширину
    private void bfs(int v) {
        if (used[v]) { //если вершина является пройденной, то не производим из нее вызов процедуры
            return;
        }
        queue.add(v); //начинаем обход из вершины v
        used[v] = true; //помечаем вершину как пройденную
        while (!queue.isEmpty()) {
            v = queue.poll(); //извлекаем вершину из очереди
            cout.print((v + 1) + " ");
            //запускаем обход из всех вершин, смежных с вершиной v
            for (int i = 0; i < adj[v].size(); ++i) {
                int w = adj[v].get(i);
                //если вершина уже была посещена, то пропускаем ее
                if (used[w]) {
                    continue;
                }
                queue.add(w); //добавляем вершину в очередь обхода
                used[w] = true; //помечаем вершину как пройденную
            }
        }
    }

    //процедура считывания входных данных с консоли
    private void readData() throws IOException {
        cin = new BufferedReader(new InputStreamReader(System.in));
        cout = new PrintWriter(System.out);
        tokenizer = new StringTokenizer(cin.readLine());

        n = Integer.parseInt(tokenizer.nextToken()); //считываем количество вершин графа
        m = Integer.parseInt(tokenizer.nextToken()); //считываем количество ребер графа

        //инициализируем список смежности графа размерности n
        adj = new ArrayList[n];
        for (int i = 0; i < n; ++i) {
            adj[i] = new ArrayList<>();
        }

        //считываем граф, заданный списком ребер
        for (int i = 0; i < m; ++i) {
            tokenizer = new StringTokenizer(cin.readLine());
            int v = Integer.parseInt(tokenizer.nextToken());
            int w = Integer.parseInt(tokenizer.nextToken());
            v--;
            w--;
            adj[v].add(w);
            adj[w].add(v);
        }

        used = new boolean[n];
        Arrays.fill(used, false);

        queue = new LinkedList<>();
    }

    private void run() throws IOException {
        readData();
        for (int v = 0; v < n; ++v) {
            bfs(v);
        }

        cin.close();
        cout.close();
    }

    public static void main(String[] args) throws IOException {
        BFS solution = new BFS();
        solution.run();
    }

}
