package org;

import java.util.ArrayList;
import java.util.Scanner;

import org.analytics.DataAnaliser;
import org.config.ConfigCutoverGenerator;
import org.telnet.Telnet;
import org.telnet.TelnetFhtt;

public class App {
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Digite o ip da OLT de origem: ");
            String ipOrigem = scanner.nextLine();
            System.out.print("Digite o usuario: ");
            String userOrigem = scanner.nextLine();
            System.out.print("Digite a senha: ");
            String passOrigem = scanner.nextLine();
            System.out.print("Digite a porta telnet:");
            String portOrigem = scanner.nextLine();
            System.out.println("Para qual olt deseja migrar?");
            System.out.println("1 - AN5000");
            System.out.println("2 - AN6000");
            System.out.print("> ");
            String oltKey = scanner.nextLine();
            String oltType = "AN5000";
            switch (oltKey) {
                case "1":
                    oltType = "AN5000";
                    break;
                case "2":
                    oltType = "AN6000";
                    break;
                default:
                    System.out.println("Numero invalido");
                    System.exit(0);
            }

            Telnet telnet = new Telnet(ipOrigem, Integer.parseInt(portOrigem), userOrigem, passOrigem);
            telnet.oltAccess();

            DataAnaliser dataAnaliser = new DataAnaliser();
            dataAnaliser.start();
            ArrayList<String> rangePortaDestino = new ArrayList<>();
            ConfigCutoverGenerator cutover = new ConfigCutoverGenerator(dataAnaliser.getData(), oltType,
                    rangePortaDestino, "1", "4", "2");
            cutover.start();

            System.out.print("Digite o ip da OLT de origem: ");
            String ipDestino = scanner.nextLine();
            System.out.print("Digite o usuario: ");
            String userDestino = scanner.nextLine();
            System.out.print("Digite a senha: ");
            String passDestino = scanner.nextLine();
            System.out.print("Digite a porta telnet:");
            String portDestino = scanner.nextLine();
            TelnetFhtt tesTelnetFhtt = new TelnetFhtt(ipDestino, Integer.parseInt(
                    portDestino), userDestino, passDestino, oltType);
            tesTelnetFhtt.oltAccess("scriptMigracao.txt");
        }
    }
}
