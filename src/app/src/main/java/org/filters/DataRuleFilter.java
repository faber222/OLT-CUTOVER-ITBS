package org.filters;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataRuleFilter {

    private HashMap<Integer, ArrayList<String>> dataMap;

    public DataRuleFilter() {
        this.dataMap = new HashMap<>();
    }

    public void start() {
        // Hashtable onde a chave é o "line" e o valor é uma lista de strings no formato
        // "XXXX-FFFFFFFF;0/x/y"

        try (BufferedReader br = new BufferedReader(new FileReader("dadosRules.txt"))) {
            String line;
            String aim = null; // Variável para armazenar o valor do aim
            while ((line = br.readLine()) != null) {
                // Regex para capturar o aim no formato 0/x/y
                Pattern aimPattern = Pattern.compile("aim\\s(\\d+/\\d+/\\d+)");
                Matcher aimMatcher = aimPattern.matcher(line);
                if (aimMatcher.find()) {
                    aim = aimMatcher.group(1); // Captura o aim encontrado
                }

                // Regex para capturar o string-hex genérico e o valor do line
                Pattern hexPattern = Pattern
                        .compile("([A-Za-z0-9]{4}-[0-9a-fA-F]{8})\\s+line\\s+(\\d+)");
                Matcher hexMatcher = hexPattern.matcher(line);
                if (hexMatcher.find()) {
                    String stringHex = hexMatcher.group(1).replace("-", ""); // Captura o valor do string-hex (XXXX-XXXXXXXX)
                    int lineKey = Integer.parseInt(hexMatcher.group(2)); // Captura o valor do line como chave

                    // Formata o valor como "XXXX-XXXXXXXX;0/x/y"
                    String value = stringHex + ";" + aim;

                    // Adiciona o valor na hashMap, agrupando por chave (line)
                    this.dataMap.computeIfAbsent(lineKey, k -> new ArrayList<>()).add(value);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Criando um ArrayList dos números "line"
        ArrayList<Integer> lineNumbers = new ArrayList<>(dataMap.keySet());

        // Exibe o conteúdo do ArrayList dos números "line"
        // System.out.println("Line Numbers: " + lineNumbers);
        // Exibe o conteúdo da hashMap
        this.dataMap.forEach((lineKey, valueList) -> {
            // System.out.println("Line " + lineKey + ": " + valueList);
        });
    }

    public HashMap<Integer, ArrayList<String>> getDataMap() {
        return dataMap;
    }

    public void setDataMap(HashMap<Integer, ArrayList<String>> dataMap) {
        this.dataMap = dataMap;
    }

    
}
