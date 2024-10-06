package org.filters;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataLineFilter {
    public static Matcher matcherLine(final String ln, final String[] regexes) {

        // Itera sobre o array de regexes
        for (final String regex : regexes) {
            final Pattern pattern = Pattern.compile(regex);
            final Matcher mather = pattern.matcher(ln);

            // Retorna o primeiro Matcher válido
            if (mather.find()) {
                return mather;
            }
        }

        // Se nenhum regex for válido, retorna null
        return null;
    }
    private final HashMap<Integer, ArrayList<String>> dataMap2;

    private final String path;

    public DataLineFilter() {
        this.dataMap2 = new HashMap<>();
        // this.path = "dadosLines.txt";
        this.path = "dados.txt";
    }

    public void start() { // HashMap onde a chave é o aim e o valor é uma lista de objetos contendo
                          // os

        // dados da tabela
        final String[] flows = {
                "flow\\s+\\d+\\s+port\\s+(eth)\\s+(\\d+)\\s+default\\s+vlan\\s+(\\d+)",
                "flow\\s+\\d+\\s+port\\s+(eth)\\s+(\\d+)\\s+vlan\\s+(\\d+)\\s+keep",
                "flow\\s+\\d+\\s+port\\s+(veip)\\s+default\\s+vlan\\s+(\\d+)",
                "flow\\s+\\d+\\s+port\\s+(veip)\\s+vlan\\s+(\\d+)\\s+keep"
        };

        // String[] mappings = {
        // "mapping\\s+\\d+\\s+port\\s+(eth)\\s+(\\d+)\\s+vlan\\s+(\\d+)\\s+gemport\\s+\\d+",
        // "mapping\\s+\\d+\\s+port\\s+(veip)\\s+vlan\\s+(\\d+)\\s+gemport\\s+\\d+"
        // };
        // // dados da tabela
        // String[] flows = {
        // "flow\\s+\\d+\\s+port\\s+(eth)\\s+(\\d+)\\s+default\\s+vlan\\s+(\\d+)", //
        // eth com default
        // "flow\\s+\\d+\\s+port\\s+(eth)\\s+(\\d+)\\s+vlan\\s+(\\d+)\\s+keep", // eth
        // com keep
        // "flow\\s+\\d+\\s+port\\s+(veip)\\s+default\\s+vlan\\s+(\\d+)", // veip com
        // default
        // "flow\\s+\\d+\\s+port\\s+(veip)\\s+vlan\\s+(\\d+)\\s+keep" // veip com keep
        // };

        // String[] mappings = {
        // "mapping\\s+\\d+\\s+port\\s+(eth)\\s+(\\d+)\\s+vlan\\s+(\\d+)\\s+gemport\\s+\\d+",
        // // eth
        // "mapping\\s+\\d+\\s+port\\s+(veip)\\s+vlan\\s+(\\d+)\\s+gemport\\s+\\d+" //
        // veip
        // };

        // ArrayList para armazenar os valores de vlan-profile sem repetição
        // HashSet<Integer> vlanProfileSet = new HashSet<>();

        try (BufferedReader br = new BufferedReader(new FileReader(this.path))) {
            String line;
            Integer currentAim = null; // Variável para armazenar o aim atual
            Integer vlanProfile = null; // Variável para armazenar o vlan-profile
            String mode = null; // Variável para armazenar o tipo de porta (ETH ou VEIP)
            String port = null; // Variável para armazenar o número da porta
            String vlan = null; // Variável para armazenar o valor da vlan
            boolean tagging = false; // Variável para o valor de tagging (true ou false)

            while ((line = br.readLine()) != null) {
                // Captura o aim
                final Pattern aimPattern = Pattern.compile("aim\\s+(\\d+)");
                final Matcher aimMatcher = aimPattern.matcher(line);
                if (aimMatcher.find()) {
                    currentAim = Integer.valueOf(aimMatcher.group(1));
                    continue;
                }

                // Captura o vlan-profile
                final Pattern vlanProfilePattern = Pattern.compile("vlan-profile\\s+(\\d+)");
                final Matcher vlanProfileMatcher = vlanProfilePattern.matcher(line);
                if (vlanProfileMatcher.find()) {
                    vlanProfile = Integer.valueOf(vlanProfileMatcher.group(1));
                    // vlanProfileSet.add(vlanProfile); // Adiciona ao conjunto (sem repetição)
                    continue;
                }

                // // Captura o mapping (porta e vlan)
                // Matcher mappingMatcher = matcherLine(line, mappings);
                // if (mappingMatcher != null) { // Verifique se não é nulo
                // mode = mappingMatcher.group(1); // Porta (ETH ou VEIP)
                // System.out.println("mode: " + mode);
                // if (mode.equals("eth")) {
                // port = mappingMatcher.group(2); // Número da porta (ETH)
                // testPort.add(port);
                // // System.out.println("port: " + port);
                // vlan = mappingMatcher.group(3); // VLAN associada
                // testVlanMapping.add(vlan);
                // // System.out.println("vlan: " + vlan);
                // } else {
                // vlan = mappingMatcher.group(2); // VLAN associada no caso de VEIP
                // testVlanMapping.add(vlan);
                // // System.out.println("vlan veip: " + vlan);
                // }
                // continue;
                // }

                // Captura o flow e define o valor de tagging
                final Matcher flowMatcher = matcherLine(line, flows);
                if (flowMatcher != null) { // Verifique se não é nulo
                    mode = flowMatcher.group(1); // Captura o tipo de porta (ETH ou VEIP)
                    vlan = flowMatcher.group(2); // Captura o valor da VLAN
                    if (mode.equals("eth")) {
                        port = flowMatcher.group(2); // Número da porta (ETH)
                        vlan = flowMatcher.group(3); // Captura o valor da VLAN
                    }

                    final String flowType = flowMatcher.group(0); // Captura o tipo de fluxo (default ou keep)

                    // Define o valor de tagging baseado no flow
                    tagging = flowType.contains("keep");
                    // Adiciona os dados no HashMap
                    if (currentAim != null && vlan != null) {
                        // Preenche os dados da tabela com base no aim
                        final String entry2 = currentAim + ";" + vlan + ";" + mode + ";" +
                                (tagging ? "TRUE" : "FALSE") + ";" + port + ";" + vlanProfile;
                        // Adiciona os dados no HashMap, agrupando por aim
                        this.dataMap2.computeIfAbsent(currentAim, k -> new ArrayList<>()).add(entry2);
                    }
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