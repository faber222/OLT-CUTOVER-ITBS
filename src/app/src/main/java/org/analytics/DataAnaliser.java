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
        this.vlans = new ArrayList<String>();
        this.lines = new ArrayList<String>();
        this.rules = new ArrayList<String>();
        this.data = new ArrayList<String>();
    }

    public void start() {
        DataLineFilter lineFilter = new DataLineFilter();
        DataRuleFilter ruleFilter = new DataRuleFilter();
        DataVlanFilter vlanFilter = new DataVlanFilter();

        vlanFilter.start();
        lineFilter.start();
        ruleFilter.start();
        this.dataMapVlans = vlanFilter.getDataMap();
        this.dataMapLines = lineFilter.getDataMap();
        this.dataMapRules = ruleFilter.getDataMap();

        dataRuleSorter();

    }

    public ArrayList<String> dataLineSorter(int x) {
        for (Map.Entry<Integer, ArrayList<String>> entry : this.dataMapLines.entrySet()) {
            if (x == entry.getKey()) {
                return entry.getValue();
            }
        }
        return new ArrayList<>(); // Retorna uma lista vazia se não encontrar a chave
    }

    public void dataRuleSorter() {
        this.dataMapRules.forEach((lineKey, valueList) -> {
            ArrayList<String> lines = (lineKey != null) ? dataLineSorter(lineKey) : null;

            for (String each : valueList) {
                for (String val : lines) {
                    // ITBS-5f72cb27;0/9/1;58;1005;veip;TRUE;null
                    String local = each + ";" + val;
                    System.out.println("Local: " + local);
                    this.data.add(local);
                }
            }
        });
    }

    // public void saveDataToCSV(String filePath) {
    // try (FileWriter writer = new FileWriter(filePath)) {
    // // Cabeçalho do CSV
    // writer.write("ID,Type,Data\n");

    // // Salva os dados de 'dataMapRules'
    // saveMapToCSV(writer, dataMapRules, "Rules");

    // // Salva os dados de 'dataMapLines'
    // saveMapToCSV(writer, dataMapLines, "Lines");

    // // Salva os dados de 'dataMapVlans'
    // saveMapToCSV(writer, dataMapVlans, "Vlans");

    // System.out.println("Dados salvos em: " + filePath);
    // } catch (IOException e) {
    // e.printStackTrace();
    // }
    // }

    // private void saveMapToCSV(FileWriter writer, HashMap<Integer,
    // ArrayList<String>> dataMap, String dataType)
    // throws IOException {
    // for (Map.Entry<Integer, ArrayList<String>> entry : dataMap.entrySet()) {
    // Integer id = entry.getKey();
    // ArrayList<String> dataList = entry.getValue();
    // for (String data : dataList) {
    // // Escreve a linha no formato: ID, Tipo de dado, Valor
    // writer.write(id + "," + dataType + "," + data + "\n");
    // }
    // }
    // }
}
