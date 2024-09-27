package org.filters;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataLineFilter {
    private HashMap<Integer, ArrayList<String>> dataMap2;

    public static Matcher matcherLine(String ln, String[] regexes) {

        // Itera sobre o array de regexes
        for (String regex : regexes) {
            Pattern pattern = Pattern.compile(regex);
            Matcher mather = pattern.matcher(ln);

            // Retorna o primeiro Matcher válido
            if (mather.find()) {
                return mather;
            }
        }

        // Se nenhum regex for válido, retorna null
        return null;
    }

    public DataLineFilter() {
        this.dataMap2 = new HashMap<>();
    }

    public void start() { // HashMap onde a chave é o aim e o valor é uma lista de objetos contendo
                          // os
        // dados da tabela
        String[] flows = {
                "flow\\s+\\d+\\s+port\\s+(eth)\\s+(\\d+)\\s+default\\s+vlan\\s+(\\d+)",
                "flow\\s+\\d+\\s+port\\s+(eth)\\s+(\\d+)\\s+vlan\\s+(\\d+)\\s+keep",
                "flow\\s+\\d+\\s+port\\s+(veip)\\s+default\\s+vlan\\s+(\\d+)",
                "flow\\s+\\d+\\s+port\\s+(veip)\\s+vlan\\s+(\\d+)\\s+keep"
        };

        String[] mappings = {
                "mapping\\s+\\d+\\s+port\\s+(eth)\\s+(\\d+)\\s+vlan\\s+(\\d+)\\s+gemport\\s+\\d+",
                "mapping\\s+\\d+\\s+port\\s+(veip)\\s+vlan\\s+(\\d+)\\s+gemport\\s+\\d+"
        };
        // ArrayList para armazenar os valores de vlan-profile sem repetição
        // HashSet<Integer> vlanProfileSet = new HashSet<>();

        try (BufferedReader br = new BufferedReader(new FileReader("dadosLines.txt"))) {
            String line;
            Integer currentAim = null; // Variável para armazenar o aim atual
            Integer vlanProfile = null; // Variável para armazenar o vlan-profile
            String mode = null; // Variável para armazenar o tipo de porta (ETH ou VEIP)
            String port = null; // Variável para armazenar o número da porta
            String vlan = null; // Variável para armazenar o valor da vlan
            boolean tagging = false; // Variável para o valor de tagging (true ou false)

            while ((line = br.readLine()) != null) {
                // Captura o aim
                Pattern aimPattern = Pattern.compile("aim\\s+(\\d+)");
                Matcher aimMatcher = aimPattern.matcher(line);
                if (aimMatcher.find()) {
                    currentAim = Integer.valueOf(aimMatcher.group(1));
                    continue;
                }

                // Captura o vlan-profile
                Pattern vlanProfilePattern = Pattern.compile("vlan-profile\\s+(\\d+)");
                Matcher vlanProfileMatcher = vlanProfilePattern.matcher(line);
                if (vlanProfileMatcher.find()) {
                    vlanProfile = Integer.valueOf(vlanProfileMatcher.group(1));
                    // vlanProfileSet.add(vlanProfile); // Adiciona ao conjunto (sem repetição)
                    continue;
                }

                // Captura o mapping (porta e vlan)
                Matcher mappingMatcher = matcherLine(line, mappings);
                if (mappingMatcher != null) { // Verifique se não é nulo
                    mode = mappingMatcher.group(1); // Porta (ETH ou VEIP)
                    if (mode.equals("eth")) {
                        port = mappingMatcher.group(2); // Número da porta (ETH)
                        vlan = mappingMatcher.group(3); // VLAN associada
                    } else {
                        vlan = mappingMatcher.group(2); // VLAN associada no caso de VEIP
                    }
                    continue;
                }

                // Captura o flow e define o valor de tagging
                Matcher flowMatcher = matcherLine(line, flows);
                if (flowMatcher != null) { // Verifique se não é nulo
                    String portType = flowMatcher.group(1); // Captura o tipo de porta (ETH ou VEIP)
                    vlan = flowMatcher.group(2); // Captura o valor da VLAN
                    if (portType.equals("eth")) {
                        port = flowMatcher.group(2); // Número da porta (ETH)
                        vlan = flowMatcher.group(3); // Captura o valor da VLAN
                    }
                    String flowType = flowMatcher.group(0); // Captura o tipo de fluxo (default ou keep)

                    // Define o valor de tagging baseado no flow
                    tagging = flowType.contains("keep");

                    // Adiciona os dados no HashMap
                    if (currentAim != null && vlan != null) {
                        // Preenche os dados da tabela com base no aim
                        String entry2 = currentAim + ";" + vlan + ";" + mode + ";" +
                                (tagging ? "TRUE" : "FALSE") + ";" + port + ";" + vlanProfile;
                        // Adiciona os dados no HashMap, agrupando por aim
                        this.dataMap2.computeIfAbsent(currentAim, k -> new ArrayList<>()).add(entry2);
                    }
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public HashMap<Integer, ArrayList<String>> getDataMap() {
        return dataMap2;
    }
}