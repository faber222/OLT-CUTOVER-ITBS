package org;

import java.util.ArrayList;

import org.analytics.DataAnaliser;
import org.config.ConfigCutoverGenerator;

public class App {
    public static void main(String[] args) {
        // testando acesso telnet para coleta de Rules
        // Telnet testeRuleG16 = new Telnet("10.1.40.134", 23, "admin", "admin", "Rules");
        // testeRuleG16.oltAccess();
        // // testando acesso telnet para coleta de Lines
        // Telnet testeLineG16 = new Telnet("10.1.40.134", 23, "admin", "admin",
        // "Lines");
        // testeLineG16.oltAccess();
        // // testando acesso telnet para coleta de Vlan
        // Telnet testeVlanG16 = new Telnet("10.1.40.134", 23, "admin", "admin",
        // "Vlan");
        // testeVlanG16.oltAccess();
        DataAnaliser dataAnaliser = new DataAnaliser();
        dataAnaliser.start();
        ArrayList<String> rangePortaDestino = new ArrayList<>();
        ConfigCutoverGenerator cutover = new ConfigCutoverGenerator(dataAnaliser.getData(), "AN6000",
                rangePortaDestino, "5", "10", "2");
        cutover.start();
    }
}
