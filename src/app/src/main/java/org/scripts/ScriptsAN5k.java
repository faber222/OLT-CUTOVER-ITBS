package org.scripts;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe que fornece scripts relacionados à configuração de uma OLT AN5000
 */
public class ScriptsAN5k {
        /**
         * Construtor padrão
         */
        public ScriptsAN5k() {
        }

        /**
         * Função usada para selecionar o modo de autorização das portas Pon
         * 
         * @param slotGpon     Slot da placa no chassi
         * @param slotPortaPon Porta pon onde a cpe se encontra
         * @return Lista de Strings contendo todo o script de autorização das portas
         */
        public List<String> setPonAuth(final String slotGpon, final String slotPortaPon) {
                final List<String> ponAuth = new ArrayList<>();
                ponAuth.add("cd card");
                ponAuth.add(String.format("set pon_auth slot %s pon %s mode phy_id", slotGpon, slotPortaPon));
                ponAuth.add("cd ..");
                return ponAuth;
        }

        /**
         * Função usada para criar o script para provisionamento de CPE
         * 
         * @param serialNumberCpe Serial number da CPE, seguir esse padrao FHTT12345678
         * @param slotGpon        Slot da placa no chassi
         * @param slotPortaPon    Porta pon onde a CPE se encontra
         * @param slotCpe         Slot da pon onde desejamos provisionar a CPE
         * @param cpeType         Capability da CPE
         * @return Lista de strings contendo todo o script de provisionamento de cpes
         */
        public List<String> provisionaCPE(final String serialNumberCpe, final String slotGpon,
                        final String slotPortaPon,final String slotCpe) {
                final List<String> scriptProvisionaCpe = new ArrayList<>();
                // final StringBuilder mgrStringBuilder = new StringBuilder();
                scriptProvisionaCpe.add("cd onu");
                scriptProvisionaCpe.add(String.format(
                                "set whitelist phy_addr address %s password null action add slot %s pon %s onu %s",
                                serialNumberCpe, slotGpon, slotPortaPon, slotCpe));
                scriptProvisionaCpe.add("cd ..");
                return scriptProvisionaCpe;
        }

        /**
         * Função usada para criar o servMode intelbras router para perfil veip
         * 
         * @return Lista de strings contendo a config servMode default intelbras
         */
        public List<String> configProfileServMode() {
                final List<String> scriptServMode = new ArrayList<>();
                scriptServMode.add("cd profile");
                scriptServMode.add(
                                "add servmode profile index 30 name ITBS_ROUTER_PROF type unicast cvlan transparent translate disable qinq disable null");
                scriptServMode.add("cd ..");
                return scriptServMode;
        }

        /**
         * Função usada para criar o script veip da CPE
         * 
         * @param slotGpon     Slot da placa no chassi
         * @param slotPortaPon Porta pon onde a CPE se encontra
         * @param slotCpe      Slot da pon onde a CPE se encontra
         * @param index        Index do veip
         * @param vlan         Vlan do VEIP
         * @return Lista de strings contendo todo o script para configuração de veip
         */
        public List<String> configVeip(final String slotGpon, final String slotPortaPon, final String slotCpe, final String index,
                        final String vlan) {
                final List<String> scriptVeip = new ArrayList<>();
                scriptVeip.add("cd onu");
                scriptVeip.add("cd lan");
                scriptVeip.add(String.format(
                                "set epon slot %s pon %s onu %s port 1 onuveip %s 33024 %s 65535 33024 65535 65535 33024 65535 65535 0 30 65535",
                                slotGpon, slotPortaPon, slotCpe, index, vlan));
                scriptVeip.add("cd ..");
                scriptVeip.add("cd ..");
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
                final List<String> scriptEth = new ArrayList<>();
                scriptEth.add("cd onu");
                scriptEth.add("cd lan");
                scriptEth.add(String.format("set slot %s pon %s onu %s port %s service number 1", slotGpon,
                                slotPortaPon, slotCpe, port));
                scriptEth.add(String.format("set slot %s pon %s onu %s port %s service 1 vlan_mode %s 0 33024 %s",
                                slotGpon, slotPortaPon, slotCpe, port, mode, vlan));
                scriptEth.add("cd ..");
                scriptEth.add("cd ..");
                return scriptEth;
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
                uplink.add("cd vlan");
                uplink.add(String.format("add vlan vlan_begin %s vlan_end %s tag uplink slot %s port %s", vlan, vlan,
                                slotUplink, slotPortaUplink));
                uplink.add(String.format("add vlan vlan_begin %s vlan_end %s tag allslot 0", vlan, vlan));
                uplink.add("cd ..");
                return uplink;
        }
}
