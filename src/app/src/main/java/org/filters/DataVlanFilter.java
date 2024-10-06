package org.filters;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataVlanFilter {
    private final HashMap<Integer, ArrayList<String>> dataMap2;
    private final String path;

    public DataVlanFilter() {
        this.dataMap2 = new HashMap<>();
        // this.path = "dadosVlan.txt";
        this.path = "dados.txt";
    }

    public void start() {
        // Hashtable onde a chave é o "line" e o valor é uma lista de strings no formato
        // "XXXX-FFFFFFFF;0/x/y"

        try (BufferedReader br = new BufferedReader(new FileReader(this.path))) {
            String line;
            int aim = 0; // Variável para armazenar o valor do aim
            while ((line = br.readLine()) != null) {
                // Regex para capturar o aim no formato 0/x/y
                final Pattern aimPattern = Pattern.compile("aim\\s+(\\d+)");
                final Matcher aimMatcher = aimPattern.matcher(line);
                if (aimMatcher.find()) {
                    aim = Integer.parseInt(aimMatcher.group(1)); // Captura o aim encontrado
                }

                // Regex para capturar o string-hex genérico e o valor do line
                final Pattern hexPattern = Pattern
                        .compile("translate\\s+old-vlan\\s+(\\d+)\\s+new-vlan\\s+(\\d+)");
                final Matcher hexMatcher = hexPattern.matcher(line);
                if (hexMatcher.find()) {
                    final int oldVlan = Integer.parseInt(hexMatcher.group(1)); // Captura o valor do old-vlan
                    final int newVlan = Integer.parseInt(hexMatcher.group(2)); // Captura o valor do new-vlan

                    // Adiciona o valor na hashMap, agrupando por chave (line)
                    final String entry2 = oldVlan + ";" + newVlan;
                    this.dataMap2.computeIfAbsent(aim, K -> new ArrayList<>()).add(entry2);
                }
            }
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    public HashMap<Integer, ArrayList<String>> getDataMap() {
        return dataMap2;
    }

}
