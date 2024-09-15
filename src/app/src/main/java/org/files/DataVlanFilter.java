package org.files;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataVlanFilter {
    public static void main(String[] args) {
        // Hashtable onde a chave é o "line" e o valor é uma lista de strings no formato
        // "XXXX-FFFFFFFF;0/x/y"
        HashMap<Integer, ArrayList<TableEntry2>> dataMap = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader("dadosVlan.txt"))) {
            String line;
            int aim = 0; // Variável para armazenar o valor do aim
            while ((line = br.readLine()) != null) {
                // Regex para capturar o aim no formato 0/x/y
                Pattern aimPattern = Pattern.compile("aim\\s+(\\d+)");
                Matcher aimMatcher = aimPattern.matcher(line);
                if (aimMatcher.find()) {
                    aim = Integer.parseInt(aimMatcher.group(1)); // Captura o aim encontrado
                }

                // Regex para capturar o string-hex genérico e o valor do line
                Pattern hexPattern = Pattern
                        .compile("translate\\s+old-vlan\\s+(\\d+)\\s+new-vlan\\s+(\\d+)");
                Matcher hexMatcher = hexPattern.matcher(line);
                if (hexMatcher.find()) {
                    int oldVlan = Integer.parseInt(hexMatcher.group(1)); // Captura o valor do old-vlan
                    int newVlan = Integer.parseInt(hexMatcher.group(2)); // Captura o valor do new-vlan

                    // Adiciona o valor na hashMap, agrupando por chave (line)
                    TableEntry2 entry = new TableEntry2(oldVlan, newVlan);
                    dataMap.computeIfAbsent(aim, k -> new ArrayList<>()).add(entry);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Exibe os dados do HashMap
        dataMap.forEach((aim, entryList) -> {
            System.out.println("Aim: " + aim);
            for (TableEntry2 entry : entryList) {
                System.out.println(entry);
            }
        });
    }
}

// Classe para armazenar os dados da tabela
class TableEntry2 {
    int oldVlan;
    int newVlan;

    public TableEntry2(int oldVlan, int newVlan) {
        this.oldVlan = oldVlan;
        this.newVlan = newVlan;
    }

    @Override
    public String toString() {
        return "DOWNLINK: " + oldVlan +
                ", UPLINK: " + newVlan;
    }
}
