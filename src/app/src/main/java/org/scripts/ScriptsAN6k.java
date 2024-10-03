package org.scripts;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe que fornece scripts relacionados à configuração de uma OLT AN5000
 */
public class ScriptsAN6k {
        /**
         * Construtor padrão
         */
        public ScriptsAN6k() {
        }

        /**
         * Função usada para criar o script para provisionamento de CPE
         * 
         * @param serialNumberCpe Serial number da CPE, seguir esse padrao FHTT12345678
         * @param slotGpon        Slot da placa no chassi
         * @param slotPortaPon    Porta pon onde a CPE se encontra
         * @param slotCpe         Slot da pon onde desejamos provisionar a CPE
         * @return Lista de strings contendo todo o script de provisionamento de cpes
         */
        public List<String> provisionaCPE(final String serialNumberCpe, final String slotGpon,
                        final String slotPortaPon, final String slotCpe) {
                final List<String> scriptProvisionaCpe = new ArrayList<>();
                // final StringBuilder mgrStringBuilder = new StringBuilder();
                // scriptProvisionaCpe.add("cd onu");
                scriptProvisionaCpe.add(String.format("whitelist add phy-id %s slot %s pon %s onuid %s",
                                serialNumberCpe, slotGpon, slotPortaPon, slotCpe));
                // scriptProvisionaCpe.add("exit");
                return scriptProvisionaCpe;
        }

        /**
         * Função usada para criar o servMode intelbras router para perfil veip
         * 
         * @return Lista de strings contendo a config servMode default intelbras
         */
        public List<String> configProfileServMode() {
                final List<String> scriptServMode = new ArrayList<>();
                scriptServMode.add(
                                "port-service-mode-profile add index 30 name INTELBRAS_ROUTER type unicast cvlan transparent translate disable qinq disable null");
                return scriptServMode;
        }

        /**
         * Função usada para criar o script veip da CPE
         * 
         * @param slotGpon     Slot da placa no chassi
         * @param slotPortaPon Porta pon onde a CPE se encontra
         * @param slotCpe      Slot da pon onde a CPE se encontra
         * @param vlan         Vlan do VEIP
         * @return Lista de strings contendo todo o script para configuração de veip
         */
        public List<String> configVeip(final String slotGpon, final String slotPortaPon, final String slotCpe,
                        final String vlan) {
                final List<String> scriptVeip = new ArrayList<>();
                scriptVeip.add(String.format("interface pon 1/%s/%s", slotGpon, slotPortaPon));
                scriptVeip.add(String.format(
                                "onu veip %s cvlan-tpid  33024 cvlan-id %s cvlan-cos 65535 trans-vlan-tpid 33024 trans-vlan-id 65535 trans-vlan-cos 65535 svlan-tpid 33024 svlan-vid 65535 svlan-cos 65535 tls 0 service-mode-profile INTELBRAS_ROUTER svlan-profile 65535 service-type 1",
                                slotCpe, vlan));
                scriptVeip.add("exit");
                return scriptVeip;
        }

        /**
         * Função usada para criar o script eth bridge da CPE
         * 
         * @param slotGpon     Slot da placa no chassi
         * @param slotPortaPon Porta pon onde a CPE se encontra
         * @param slotCpe      Slot da pon onde a CPE se encontra
         * @param port         Numero da porta eth mapeada
         * @param mode         Modo "tag" tira a vlan, "transparent" mantem a vlan
         * @param vlan         Vlan da eth
         * @return Lista de strings contendo todo o script para configuração de eth
         *         bridge
         */
        public List<String> configEth(final String slotGpon, final String slotPortaPon, final String slotCpe,
                        final String port, final String mode, final String vlan) {
                final List<String> scriptVeip = new ArrayList<>();
                scriptVeip.add(String.format("interface pon 1/%s/%s", slotGpon, slotPortaPon));
                scriptVeip.add(String.format("onu port vlan %s eth %s service count 1", slotCpe, port));
                scriptVeip.add(String.format("onu port vlan %s eth %s service 1 %s priority 0 tpid 33024 vid %s",
                                slotCpe, port, mode, vlan));
                scriptVeip.add("exit");
                return scriptVeip;
        }

        /**
         * Função usada para criar o script que add a vlan na uplink da olt
         * 
         * @param vlan            Vlan da uplink
         * @param slotUplink      Slot da placa no chassi
         * @param slotPortaUplink Slot da porta uplink no chassi
         * @return Lista de strings contendo todo o script para add vlan na uplink
         */
        public List<String> addVlanToUplink(final String vlan, final String slotUplink, final String slotPortaUplink) {
                final List<String> uplink = new ArrayList<>();
                uplink.add(String.format("port vlan %s to %s tag 1/%s %s", vlan, vlan,
                                slotUplink, slotPortaUplink));
                return uplink;
        }
}
