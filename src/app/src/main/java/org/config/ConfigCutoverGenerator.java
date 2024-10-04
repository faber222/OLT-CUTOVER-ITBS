package org.config;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.scripts.ScriptsAN5k;
import org.scripts.ScriptsAN6k;

public class ConfigCutoverGenerator {
    private final ArrayList<String> data;
    private List<String> vlansUplink;
    private final List<String> configs;
    private List<String> scriptFinal;
    private List<String> ponAuth;
    private List<String> profileServMode;

    private List<List<String>> configVeip;
    private List<List<String>> configEth;
    private final List<List<String>> configProv;
    private final List<List<String>> configUplinkVlan;

    private final ArrayList<String> rangePortaDestino;
    private final String slotChassiGpon;
    private final String slotChassiUplink;
    private final String slotPortaUplink;
    private final String oltType;

    public ConfigCutoverGenerator(final ArrayList<String> data, final String oltType,
            final ArrayList<String> rangePortaDestino,
            final String slotChassiGpon, final String slotChassiUplink, final String slotPortaUplink) {
        // ITBS5f72cb27;0/9/1;58;1005;veip;TRUE;null;1;1005;1005
        this.data = data;
        this.oltType = oltType;
        this.rangePortaDestino = rangePortaDestino;
        this.slotChassiGpon = slotChassiGpon;
        this.slotChassiUplink = slotChassiUplink;
        this.slotPortaUplink = slotPortaUplink;
        this.vlansUplink = new ArrayList<>(); // Inicializa a lista vlans;
        this.configs = new ArrayList<>();
        this.ponAuth = new ArrayList<>();
        this.profileServMode = new ArrayList<>();
        this.configVeip = new ArrayList<>();
        this.configEth = new ArrayList<>();
        this.configProv = new ArrayList<>();
        this.configUplinkVlan = new ArrayList<>();
    }

    public void setVlans(final List<String> vlans) {
        this.vlansUplink = vlans;
    }

    public void start() {
        final HashSet<String> vlansUnicas = new HashSet<>();
        final HashSet<String> newVlanUnica = new HashSet<>();
        // System.out.println(data);
        for (final String lines : this.data) {
            final String[] splitInicial = lines.split(";");
            final String vlan = splitInicial[3]; // 1005
            final String oldVlan = splitInicial[8]; // 1005
            final String newVlan = splitInicial[9]; // 1005

            if (!vlansUnicas.contains(vlan) && !newVlanUnica.contains(vlan)) {
                vlansUnicas.add(vlan);
                vlansUplink.add(vlan);
            }
            if (!vlansUnicas.contains(oldVlan) && !newVlanUnica.contains(oldVlan)) {
                vlansUnicas.add(oldVlan);
                vlansUplink.add(oldVlan);
            }
            if (!newVlanUnica.contains(newVlan) && !vlansUnicas.contains(newVlan)) {
                newVlanUnica.add(newVlan);
                vlansUplink.add(newVlan);
            }
        }
        createScript();
        // System.out.println(this.data);
    }

    private boolean writeScript() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("scriptMigracao.txt"))) {
            if (this.ponAuth != null) {
                for (final String lines : this.ponAuth) {
                    writer.write(lines);
                    writer.newLine();
                }
                writer.newLine();
            }

            if (this.profileServMode != null) {
                for (final String lines : this.profileServMode) {
                    writer.write(lines);
                    writer.newLine();
                }
                writer.newLine();
            }
            for (List<String> list : this.configUplinkVlan) {
                for (String lines : list) {
                    writer.write(lines);
                    writer.newLine();
                }
            }
            writer.newLine();

            for (List<String> list : this.configProv) {
                for (final String lines : list) {
                    writer.write(lines);
                    writer.newLine();
                }
            }
            writer.newLine();

            if (this.configVeip != null) {
                for (List<String> list : this.configVeip) {
                    for (String lines : list) {
                        writer.write(lines);
                        writer.newLine();
                    }
                }
                writer.newLine();
            }

            if (this.configEth != null) {
                for (List<String> list : this.configEth) {
                    for (String lines : list) {
                        writer.write(lines);
                        writer.newLine();
                    }
                }
                writer.newLine();
            }
            writer.newLine();

            return true;
        } catch (final IOException e) {
            return false;
        }
    }

    private void createScript() {

        for (String teste : this.data) {
            // System.out.println(teste);
        }
        final ScriptsAN5k scriptsAN5k = new ScriptsAN5k();
        final ScriptsAN6k scriptsAN6k = new ScriptsAN6k();

        final HashSet<String> cpe = new HashSet<>();
        Map<String, Integer> gponSnCountMap = new HashMap<>();
        if (this.oltType.equals("AN6000")) {
            this.ponAuth = null;
            for (final String each : this.vlansUplink) {
                configUplinkVlan.add(scriptsAN6k.addVlanToUplink(each, this.slotChassiUplink, this.slotPortaUplink));
            }

            for (final String lines : this.data) {
                /*
                 * [0] "ITBS5f72cb27",
                 * [1] "0/9/1",
                 * [2] "58", INUTIL
                 * [3] "1005",
                 * [4] "veip",
                 * [5] "TRUE",
                 * [6] "NULL",
                 * [7] "1", INUTIL
                 * [8] "1005",
                 * [9] "1005"
                 */
                final String[] splitInicial = lines.split(";");
                final String gponSn = splitInicial[0]; // ITBS5f72cb27

                final String[] slots = splitInicial[1].split("/"); // 0/9/1
                final String slotPortaPon = slots[1]; // 9
                final String slotCpe = slots[2]; // 1

                final String vlan = splitInicial[3]; // 1005
                final String mode = splitInicial[4]; // veip
                final String tagging = splitInicial[5]; // TRUE
                final String port = splitInicial[6]; // NULL
                final String oldVlan = splitInicial[8]; // 1005

                // Verifica se o gponSn já existe no map de contagem
                int count = gponSnCountMap.getOrDefault(gponSn, 0);
                count++; // Incrementa a contagem para o gponSn atual
                gponSnCountMap.put(gponSn, count); // Atualiza a contagem no mapa

                // Atualiza o valor do index com base na contagem
                String index = String.valueOf(count);

                if (!cpe.contains(gponSn)) {
                    configProv.add(scriptsAN6k.provisionaCPE(gponSn, this.slotChassiGpon, slotPortaPon, slotCpe));
                    cpe.add(gponSn);
                }

                if (mode.equals("veip")) {
                    // configEth = null;
                    this.profileServMode = scriptsAN6k.configProfileServMode();
                    if (tagging.equals("TRUE")) {
                        configVeip.add(scriptsAN6k.configVeip(this.slotChassiGpon, slotPortaPon, slotCpe, vlan));
                    } else {
                        configVeip.add(
                                scriptsAN6k.configVeip(this.slotChassiGpon, slotPortaPon, slotCpe, oldVlan));
                    }
                } else {
                    // configVeip = null;
                    this.profileServMode = null;
                    final String downStreamVlan = tagging.equals("TRUE") ? "transparent" : "tag";
                    configEth.add(scriptsAN6k.configEth(
                            this.slotChassiGpon, slotPortaPon, slotCpe, port,
                            downStreamVlan, index, vlan));

                }
            }
        } else {
            this.ponAuth = (scriptsAN5k.setPonAuth(this.slotChassiUplink, this.slotPortaUplink));

            for (final String each : this.vlansUplink) {
                configUplinkVlan.add(scriptsAN5k.addVlanToUplink(each, this.slotChassiUplink, this.slotPortaUplink));
            }

            for (final String lines : this.data) {
                final String[] splitInicial = lines.split(";");
                final String gponSn = splitInicial[0]; // ITBS5f72cb27

                final String[] slots = splitInicial[1].split("/"); // 0/9/1e
                final String slotPortaPon = slots[1]; // 9
                final String slotCpe = slots[2]; // 1

                final String vlan = splitInicial[3]; // 1005
                final String mode = splitInicial[4]; // veip
                final String tagging = splitInicial[5]; // TRUE
                final String port = splitInicial[6]; // NULL
                final String oldVlan = splitInicial[8]; // 1005

                // Verifica se o gponSn já existe no map de contagem
                int count = gponSnCountMap.getOrDefault(gponSn, 0);
                count++; // Incrementa a contagem para o gponSn atual
                gponSnCountMap.put(gponSn, count); // Atualiza a contagem no mapa

                // Atualiza o valor do index com base na contagem
                String index = String.valueOf(count);

                if (!cpe.contains(gponSn)) {
                    configProv.add(scriptsAN5k.provisionaCPE(gponSn, this.slotChassiGpon, slotPortaPon, slotCpe));

                    cpe.add(gponSn);
                }
                if (mode.equals("veip")) {
                    // configEth = null;
                    this.profileServMode = scriptsAN5k.configProfileServMode();
                    if (tagging.equals("TRUE")) {
                        configVeip.add(scriptsAN5k.configVeip(this.slotChassiGpon, slotPortaPon, slotCpe, index, vlan));
                    } else {
                        configVeip
                                .add(scriptsAN5k.configVeip(this.slotChassiGpon, slotPortaPon, slotCpe, index,
                                        oldVlan));
                    }
                } else {
                    // configVeip = null;
                    this.profileServMode = null;
                    System.out.println(tagging);
                    final String downStreamVlan = tagging.equals("TRUE") ? "transparent" : "tag";
                    System.out.println("downStream: " + downStreamVlan);
                    configEth.add(scriptsAN5k.configEth(
                            this.slotChassiGpon, slotPortaPon, slotCpe, port,
                            downStreamVlan, index, vlan));

                }
            }
        }
        writeScript();
    }
}
