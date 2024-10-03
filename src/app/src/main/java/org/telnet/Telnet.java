/**
 * @author faber222
 * @since 2024
*/
package org.telnet;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class Telnet implements Runnable {

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private Thread thread;
    private boolean active;

    // Atributos para oltAccess
    private final String host;
    private final int port;
    private final String username;
    private final String password;
    private final String type;

    /**
     * Construtor padrão que inicializa os atributos com os valores fornecidos.
     *
     * @param host O endereço IP ou nome do host para a conexão Telnet.
     * @param port A porta para a conexão Telnet.
     * @param user O nome de usuário para autenticação Telnet.
     * @param pwd  A senha para autenticação Telnet.
     */
    public Telnet(final String host, final int port, final String user, final String pwd, final String type) {
        // Inicialização dos atributos com os valores fornecidos
        this.host = host;
        this.port = port;
        this.username = user;
        this.password = pwd;
        this.type = type;
    }

    public void oltAccess() {
        try {
            // Configuração do socket e streams de entrada/saída
            socket = new Socket(host, port);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            // Início da Leitura em tempo real
            active = true;
            thread = new Thread(this);
            thread.start();

            // Login
            out.println(username);
            Thread.sleep(1000);
            out.println(password);
            Thread.sleep(1000);

            applyCommands();
            finalMessage();
        } catch (final UnknownHostException exception) {
            System.err.println("Host " + host + " desconhecido");
            // JOptionPane.showMessageDialog(null, "Host " + host
            // + " desconhecido", "Aviso!",
            // JOptionPane.INFORMATION_MESSAGE);
        } catch (final IOException exception) {
            System.err.println("Erro na entrada.");
            // JOptionPane.showMessageDialog(null,
            // "Erro na entrada.", "Aviso!",
            // JOptionPane.INFORMATION_MESSAGE);
        } catch (InterruptedException ex) {
        }
    }

    /**
     * Implementação do método run() da interface Runnable.
     * Este método é executado em uma thread separada para permitir a leitura
     * em tempo real das respostas do dispositivo de rede.
     */
    public void run() {
        try (BufferedWriter fileWriter = new BufferedWriter(
                new FileWriter("dados" + this.type + ".txt", false))) {
            String answer;
            while (active && !Thread.currentThread().isInterrupted()) {
                if ((answer = in.readLine()) != null) {
                    fileWriter.write(answer);
                    fileWriter.newLine(); // Adiciona uma nova linha após cada resposta
                }
            }
        } catch (final IOException exception) {
            if (active) {
                System.err.println("Erro de comunicacao.");
            }
        }
    }

    private void applyCommands() throws InterruptedException {
        out.println("en");
        Thread.sleep(100);
        out.println("conf t");
        Thread.sleep(100);
        out.println("line width 256");
        Thread.sleep(100);
        switch (this.type) {
            case "Rules":
                out.println("sh run deploy-profile-rule");
                Thread.sleep(1000);
                out.println("");
                break;
            case "Lines":
                out.println("sh run deploy-profile-line");
                Thread.sleep(1000);
                out.println("");
                break;
            case "Vlan":
                out.println("sh run deploy-profile-vlan");
                Thread.sleep(1000);
                out.println("");
                break;
            default:
                throw new AssertionError();
        }
        out.println("exit");
        Thread.sleep(100);
    }

    /**
     * Mensagem de alerta ao usuário
     */
    private void finalMessage() {
        // Define active como false para encerrar a thread
        active = false;

        // Interrompe a thread, caso esteja esperando
        if (thread != null) {
            try {
                socket.close();
            } catch (final IOException e) {
                e.printStackTrace();
            }
            thread.interrupt();
        }
    }
}
