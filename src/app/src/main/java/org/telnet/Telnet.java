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
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;

import javax.swing.JOptionPane;

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

    /**
     * Construtor padrão que inicializa os atributos com os valores fornecidos.
     *
     * @param host O endereço IP ou nome do host para a conexão Telnet.
     * @param port A porta para a conexão Telnet.
     * @param user O nome de usuário para autenticação Telnet.
     * @param pwd  A senha para autenticação Telnet.
     */
    public Telnet(final String host, final int port, final String user, final String pwd) {
        // Inicialização dos atributos com os valores fornecidos
        this.host = host;
        this.port = port;
        this.username = user;
        this.password = pwd;
    }

    public void oltAccess() {
        try {
            // Configuração do socket e streams de entrada/saída
            int timeout = 5000; // 5 segundos
            SocketAddress socketAddress = new InetSocketAddress(host, port);
            socket = new Socket();

            // Tenta conectar dentro do tempo limite especificado
            socket.connect(socketAddress, timeout);

            // Verifica se o socket foi realmente conectado
            if (socket.isConnected()) {
                System.out.println("Socket conectado com sucesso ao host " + host + " na porta " + port);
            }

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

            // Espera até que o processamento do comando (sh run) esteja completo
            thread.join(); // Aguarda o término da thread de leitura (run)

            // Somente após a leitura completa, finalMessage é chamado
            finalMessage();
        } catch (final UnknownHostException exception) {
            System.err.println("Erro na entrada.");
            JOptionPane.showMessageDialog(null,
                    "Erro na entrada.", "Aviso!",
                    JOptionPane.INFORMATION_MESSAGE);
            System.err.println("Host desconhecido: " + host);
        } catch (final IOException exception) {
            System.err.println("Erro de I/O ao tentar conectar ao host " + host + " na porta " + port);
            JOptionPane.showMessageDialog(null, "Erro de I/O ao tentar conectar ao host " + host + " na porta "
                    + port, "Aviso!",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (InterruptedException ex) {
        }
    }

    /**
     * Implementação do método run() da interface Runnable.
     * Este método é executado em uma thread separada para permitir a leitura
     * em tempo real das respostas do dispositivo de rede.
     */
    public void run() {
        String prompt = "(config)#"; // Prompt que indica o fim da saída
        try (BufferedWriter fileWriter = new BufferedWriter(
                new FileWriter("dados.txt", false))) {
            String answer;
            while (active && !Thread.currentThread().isInterrupted()) {
                if ((answer = in.readLine()) != null) {
                    fileWriter.write(answer);
                    fileWriter.newLine();
                    // Verifique se o prompt final foi recebido
                    if (answer.trim().endsWith(prompt)) {
                        active = false; // Termina a leitura
                        break;
                    }
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
        out.println("screen-rows per-page 0");
        Thread.sleep(100);
        out.println("sh run ");
        Thread.sleep(100);
        out.println("");
        Thread.sleep(100);
    }

    /**
     * Mensagem de alerta ao usuário
     */
    private void finalMessage() {

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
