package org;

import org.files.DataLineFilter;
import org.files.DataRuleFilter;
import org.files.DataVlanFilter;
import org.telnet.Telnet;

public class App {
    public static void main(String[] args) {
        // testando acesso telnet para coleta de Rules
        Telnet testeRuleG16 = new Telnet("10.1.40.134", 23, "admin", "admin", "Rules");
        testeRuleG16.oltAccess();
        // testando acesso telnet para coleta de Lines
        Telnet testeLineG16 = new Telnet("10.1.40.134", 23, "admin", "admin", "Lines");
        testeLineG16.oltAccess();
        // testando acesso telnet para coleta de Vlan
        Telnet testeVlanG16 = new Telnet("10.1.40.134", 23, "admin", "admin", "Vlan");
        testeVlanG16.oltAccess();
        DataLineFilter lineFilter = new DataLineFilter();
        DataRuleFilter ruleFilter = new DataRuleFilter();
        DataVlanFilter vlanFilter = new DataVlanFilter();

        vlanFilter.start();
        lineFilter.start();
        ruleFilter.start();
    }
}
