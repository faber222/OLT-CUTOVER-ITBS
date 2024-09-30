package org.config;

import java.util.ArrayList;

public class ConfigCutoverGenerator {
    private ArrayList<String> data;
    private ArrayList<String> vlans;
    private ArrayList<String> scriptFinal;

    public ConfigCutoverGenerator(ArrayList<String> data) {
        // ITBS5f72cb27;0/9/1;58;1005;veip;TRUE;null;1;1005;1005
        this.data = data;
    }

    public void start() {
        // System.out.println(data);
        for (String lines : this.data) {
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
            String[] splitInicial = lines.split(";");
            String gponSn = splitInicial[0]; // ITBS5f72cb27

            String[] slots = splitInicial[1].split("/"); // 0/9/1
            String slotPortaPon = slots[1]; // 9
            String slotCpe = slots[2]; // 1

            String vlan = splitInicial[3]; // 1005
            String mode = splitInicial[4]; // veip
            String tagging = splitInicial[5]; // TRUE
            String port = splitInicial[6]; // NULL
            String oldVlan = splitInicial[8]; // 1005
            String newVlan = splitInicial[9]; // 1005
            
        }
    }

    private void createScript() {

    }
}
