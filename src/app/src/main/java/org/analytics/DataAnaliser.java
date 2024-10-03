package org.analytics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.filters.DataLineFilter;
import org.filters.DataRuleFilter;
import org.filters.DataVlanFilter;

public class DataAnaliser {
    private ArrayList<String> vlans;
    private ArrayList<String> lines;
    private ArrayList<String> rules;

    private ArrayList<String> data;

    private HashMap<Integer, ArrayList<String>> dataMapRules;
    private HashMap<Integer, ArrayList<String>> dataMapLines;
    private HashMap<Integer, ArrayList<String>> dataMapVlans;

    public DataAnaliser() {
        this.vlans = new ArrayList<>();
        this.lines = new ArrayList<>();
        this.rules = new ArrayList<>();
        this.data = new ArrayList<>();
    }

    public void start() {
        DataRuleFilter ruleFilter = new DataRuleFilter();
        DataLineFilter lineFilter = new DataLineFilter();
        DataVlanFilter vlanFilter = new DataVlanFilter();

        ruleFilter.start();
        lineFilter.start();
        vlanFilter.start();
        this.dataMapRules = ruleFilter.getDataMap();
        this.dataMapLines = lineFilter.getDataMap();
        this.dataMapVlans = vlanFilter.getDataMap();

        dataRuleSorter();

    }

    public ArrayList<String> getData() {
        return data;
    }

    public ArrayList<String> dataSorter(int x, int key) {
        // Inicia um switch com base no valor da variável 'key'
        switch (key) {
            // Caso o valor de 'key' seja 0
            case 0:
                // Percorre o mapa 'dataMapLines', que mapeia um Integer para uma
                // ArrayList<String>
                for (Map.Entry<Integer, ArrayList<String>> entry : this.dataMapLines.entrySet()) {
                    // Se o valor de 'x' for igual à chave da entrada atual do mapa
                    if (x == entry.getKey()) {
                        // Retorna o valor associado a essa chave (ArrayList<String>)
                        return entry.getValue();
                    }
                }
                break;

            // Caso o valor de 'key' seja 1
            case 1:
                // Percorre o mapa 'dataMapVlans', que também mapeia um Integer para uma
                // ArrayList<String>
                for (Map.Entry<Integer, ArrayList<String>> entry : this.dataMapVlans.entrySet()) {
                    // Se o valor de 'x' for igual à chave da entrada atual do mapa
                    if (x == entry.getKey()) {
                        // Retorna o valor associado a essa chave (ArrayList<String>)
                        return entry.getValue();
                    }
                }
                break;

            // Se 'key' não for nem 0 nem 1, o código não faz nada (default case)
            default:
                break;
        }

        // Se nenhuma correspondência for encontrada nos mapas, retorna uma lista vazia
        return new ArrayList<>();
    }

    @SuppressWarnings("null")
    public void dataRuleSorter() {

        // this.dataMapRules.forEach((lineKey, valueList) -> {
        //     System.out.println("Line Key: " + lineKey + " -> Value List: " + valueList);
        // });
        // System.out.println(dataMapRules);
        this.dataMapRules.forEach((lineKey, valueList) -> {
            // recebe o retorno do dataSorter se não for nulo
            this.lines = (lineKey != null) ? dataSorter(lineKey, 0) : null;

            // itera os valores obtidos do rule
            System.out.println("----------");
            for (String eachRules : valueList) {
                String local = null;

                // itera os valores obtidos do line
                for (String eachLines : this.lines) {
                    // captura o ultimo item do array, que é a chave da hash vlan
                    String[] arrayEachVlan = eachLines.split(";");
                    int vlanKey = Integer.parseInt(arrayEachVlan[arrayEachVlan.length - 1]);

                    // recebe o retorno do dataSorter se não for nulo
                    this.vlans = (vlanKey != 0) ? dataSorter(vlanKey, 1) : null;
                    System.out.println(this.vlans);

                    // itera os valores obtidos do vlan
                    for (String eachVlan : this.vlans) {
                        local = eachRules + ";" + eachLines + ";" + eachVlan;

                        // ITBS-5f72cb27;0/9/1;58;1005;veip;TRUE;null;1;1005;1005
                    }
                    this.data.add(local);
                }
            }
        });
        System.out.println(this.data);
    }
}
