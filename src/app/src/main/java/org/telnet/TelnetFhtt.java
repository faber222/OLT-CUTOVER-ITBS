package org.telnet;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class TelnetFhtt implements Runnable {

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private Thread thread;
    private volatile boolean active;

    // Atributos para oltAccess
    private final String host;
    private final int port;
    private final String username;
    private final String password;
    private final String oltName;

    public TelnetFhtt(final String host, final int port, final String user, final String pwd, final String oltName) {
        this.host = host;
        this.port = port;
        this.username = user;
        this.password = pwd;
        this.oltName = oltName;
    }

    public void oltAccess(final String nomeArq) throws InterruptedException {
        try {
            // Configuração do socket e streams de entrada/saída
            socket = new Socket(host, port);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            
            active = true;
            thread = new Thread(this);
            thread.start();
            // Início da Leitura em tempo real

            // Login
            out.println(username);
            Thread.sleep(100);
            out.println(password);
            Thread.sleep(100);
            out.println("enable");
            Thread.sleep(100);
            out.println(password);
            Thread.sleep(100);
            out.println("config");
            Thread.sleep(100);

            // Ler comandos do arquivo
            readCommandsFromFile(nomeArq);

            // Mostra a mensagem que deu tudo certo e alerta o usuario para salvar as
            // configurações
            finalMessage();
        } catch (final UnknownHostException exception) {
            System.err.println("Host " + host + " desconhecido");
            // JOptionPane.showMessageDialog(null, "Host " + host
            //         + " desconhecido", "Aviso!",
            //         JOptionPane.INFORMATION_MESSAGE);
        } catch (final IOException exception) {
            System.err.println("Erro na entrada.");
            // JOptionPane.showMessageDialog(null,
            //         "Erro na entrada.", "Aviso!",
            //         JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void run() {
        try (BufferedWriter fileWriter = new BufferedWriter(new FileWriter(
                "log" + this.oltName + "Telnet.txt", true))) {
            String answer;
            while (active && !Thread.currentThread().isInterrupted()) {
                if ((answer = in.readLine()) != null) {
                    fileWriter.write(answer);
                    // System.out.println(answer);
                    fileWriter.newLine(); // Adiciona uma nova linha após cada resposta
                }
            }
        } catch (final IOException exception) {
            if (active) {
                System.err.println("Erro de comunicacao.");
                // JOptionPane.showMessageDialog(null,
                //         "Erro de comunicacao.", "Aviso!",
                //         JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    private void readCommandsFromFile(final String filename) throws IOException {
        final File file = new File(filename);
        final long totalBytes = file.length();
        long bytesRead = 0;

        try (BufferedReader fileReader = new BufferedReader(new FileReader(filename))) {
            String command;
            while ((command = fileReader.readLine()) != null) {
                out.println(command);

                // Atualiza bytes lidos
                bytesRead += command.getBytes().length + System.lineSeparator().getBytes().length;

                // Exibe a barra de progresso
                final int progressPercentage = (int) (((double) bytesRead / totalBytes) * 100);
                System.out.print("Progresso " + this.oltName + ": " + progressPercentage + "%\r");
                try {
                    // Adiciona um atraso de 100ms após cada out.println
                    Thread.sleep(100);
                } catch (final InterruptedException e) {
                    Thread.currentThread().interrupt(); // Respeita a interrupção
                }
            }
        }
        System.out.println("\nTodos os comandos da OLT " + this.oltName + " foram enviados!");
    }

    private void finalMessage() {
        // JOptionPane.showMessageDialog(null, "Comandos aplicados com sucesso!", "Aviso!",
        //         JOptionPane.INFORMATION_MESSAGE);
        // JOptionPane.showMessageDialog(null, "NAO ESQUECA DE VALIDAR E SALVAR AS CONFIGURACOES!", "Aviso!",
        //         JOptionPane.INFORMATION_MESSAGE);

        // Interrompe a thread, caso esteja esperando
        if (thread != null) {
            try {
                // Define active como false para encerrar a thread
                socket.close();
            } catch (final IOException e) {
                e.printStackTrace();
            }
            thread.interrupt();
            active = false;
        }
    }
}
