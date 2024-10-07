package org;

import java.util.ArrayList;
import java.util.Scanner;

import org.analytics.DataAnaliser;
import org.config.ConfigCutoverGenerator;
import org.telnet.Telnet;
import org.telnet.TelnetFhtt;

public class App {
    private String ipOrigem;
    private String userOrigem;
    private String passOrigem;
    private String portOrigem;
    private String oltType;
    private String ipDestino;
    private String userDestino;
    private String passDestino;
    private String portDestino;

    public App() {
        this.ipOrigem = new String();
        this.userOrigem = new String();
        this.passOrigem = new String();
        this.portOrigem = new String();
        this.ipDestino = new String();
        this.userDestino = new String();
        this.passDestino = new String();
        this.portDestino = new String();
    }

    public String getIpDestino() {
        return ipDestino;
    }

    public String getUserDestino() {
        return userDestino;
    }

    public String getPassDestino() {
        return passDestino;
    }

    public String getPortDestino() {
        return portDestino;
    }

    public String getIpOrigem() {
        return ipOrigem;
    }

    public String getUserOrigem() {
        return userOrigem;
    }

    public String getPassOrigem() {
        return passOrigem;
    }

    public String getPortOrigem() {
        return portOrigem;
    }

    public String getOltType() {
        return oltType;
    }

    public void createMessage(Scanner scanner) {
        System.out.print("Digite o ip da OLT de origem: ");
        this.ipOrigem = scanner.nextLine();
        System.out.print("Digite o usuario: ");
        this.userOrigem = scanner.nextLine();
        System.out.print("Digite a senha: ");
        this.passOrigem = scanner.nextLine();
        System.out.print("Digite a porta telnet:");
        this.portOrigem = scanner.nextLine();
        System.out.println("Para qual olt deseja migrar?");
        System.out.println("1 - AN5000");
        System.out.println("2 - AN6000");
        System.out.print("> ");
        final String oltKey = scanner.nextLine();
        this.oltType = "AN5000";
        switch (oltKey) {
            case "1":
                oltType = "AN5000";
                break;
            case "2":
                oltType = "AN6000";
                break;
            default:
                System.out.println("Numero invalido!");
                System.exit(0);
        }

    }

    public void sendMessage(Scanner scanner) {
        System.out.print("Digite o ip da OLT de origem: ");
        this.ipDestino = scanner.nextLine();
        System.out.print("Digite o usuario: ");
        this.userDestino = scanner.nextLine();
        System.out.print("Digite a senha: ");
        this.passDestino = scanner.nextLine();
        System.out.print("Digite a porta telnet:");
        this.portDestino = scanner.nextLine();

    }

    public static void main(final String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("O que deseja fazer?");
            System.out.println("1 - Criar script de migração!");
            System.out.println("2 - Enviar script de migração!");
            System.out.println("3 - Criar e Enviar script de migração!");
            System.out.println("4 - Sair!");
            System.out.print("> ");
            final String prompt = scanner.nextLine();

            App app = new App();
            ArrayList<String> rangePortaDestino = new ArrayList<>();

            switch (prompt) {
                case "1":
                    app.createMessage(scanner);
                    Telnet telnet = new Telnet(app.getIpOrigem(), Integer.parseInt(app.getPortOrigem()),
                            app.getUserOrigem(), app.getPassOrigem());
                    telnet.oltAccess();

                    DataAnaliser dataAnaliser = new DataAnaliser();
                    dataAnaliser.start();
                    ConfigCutoverGenerator cutover = new ConfigCutoverGenerator(dataAnaliser.getData(),
                            app.getOltType(),
                            rangePortaDestino, "1", "4", "2");
                    cutover.start();
                    break;
                case "2":
                    app.sendMessage(scanner);
                    TelnetFhtt tesTelnetFhtt = new TelnetFhtt(app.getIpDestino(),
                            Integer.parseInt(app.getPortDestino()), app.getUserDestino(), app.getPassDestino(),
                            app.getOltType());
                    tesTelnetFhtt.oltAccess("scriptMigracao.txt");
                    break;
                case "3":
                    app.createMessage(scanner);
                    Telnet telnet2 = new Telnet(app.getIpOrigem(), Integer.parseInt(app.getPortOrigem()),
                            app.getUserOrigem(), app.getPassOrigem());
                    telnet2.oltAccess();

                    DataAnaliser dataAnaliser2 = new DataAnaliser();
                    dataAnaliser2.start();
                    ConfigCutoverGenerator cutover2 = new ConfigCutoverGenerator(dataAnaliser2.getData(),
                            app.getOltType(), rangePortaDestino, "1", "4", "2");
                    cutover2.start();

                    app.sendMessage(scanner);
                    tesTelnetFhtt = new TelnetFhtt(app.getIpDestino(),
                            Integer.parseInt(app.getPortDestino()), app.getUserDestino(), app.getPassDestino(),
                            app.getOltType());
                    tesTelnetFhtt.oltAccess("scriptMigracao.txt");

                    break;
                case "4":
                    scanner.close();
                    System.out.println("Saindo...!");
                    System.exit(0);
                    break;

                default:
                    scanner.close();
                    System.out.println("Numero invalido!");
                    System.exit(0);
                    break;
            }
            scanner.close();

        }
    }
}
