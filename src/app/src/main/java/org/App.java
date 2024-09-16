package org;

import org.files.DataLineFilter;
import org.files.DataRuleFilter;
import org.files.DataVlanFilter;

public class App {
    public static void main(String[] args) {
        DataLineFilter lineFilter = new DataLineFilter();
        DataRuleFilter ruleFilter = new DataRuleFilter();
        DataVlanFilter vlanFilter = new DataVlanFilter();

        vlanFilter.start();
        lineFilter.start();
        ruleFilter.start();
    }
}
