package org.ssh;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Properties;

import javax.swing.JOptionPane;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class SSHClient implements Runnable {

    private Session session;
    private ChannelExec channelExec;
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
     * @param host O endereço IP ou nome do host para a conexão SSH.
     * @param port A porta para a conexão SSH.
     * @param user O nome de usuário para autenticação SSH.
     * @param pwd  A senha para autenticação SSH.
     */
    public SSHClient(final String host, final int port, final String user, final String pwd) {
        this.host = host;
        this.port = port;
        this.username = user;
        this.password = pwd;
    }

    public void oltAccess() {
        try {
            // Configura a sessão SSH
            JSch jsch = new JSch();
            session = jsch.getSession(username, host, port);
            session.setPassword(password);

            // Desativa a verificação de chave de host
            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);

            // Estabelece a conexão SSH
            System.out.println("Establishing Connection...");
            session.connect(5000); // Tempo limite de 5 segundos
            System.out.println("Connection established.");

            // Cria o canal para execução de comandos
            channelExec = (ChannelExec) session.openChannel("exec");

            // Configuração de leitura de dados
            OutputStream inputStream = channelExec.getOutputStream();
            out = new PrintWriter(inputStream, true);

            // Início da Leitura em tempo real
            active = true;
            thread = new Thread(this);
            thread.start();

            // Login e envio de comandos
            applyCommands();

            // // Espera até que o processamento dos comandos esteja completo
            thread.join(); // Aguarda o término da thread de leitura (run)

            // Finaliza a conexão
            finalMessage();
        } catch (JSchException e) {
            System.err.println("Erro na conexão SSH.");
            JOptionPane.showMessageDialog(null, "Erro ao tentar conectar via SSH.", "Aviso!",
                    JOptionPane.INFORMATION_MESSAGE);
            System.exit(0);
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    /**
     * Implementação do método run() da interface Runnable.
     * Este método é executado em uma thread separada para permitir a leitura
     * em tempo real das respostas do dispositivo de rede.
     */
    public void run() {
        String prompt = "(config)#"; // Prompt que indica o fim da saída
        try (BufferedWriter fileWriter = new BufferedWriter(new FileWriter("dados.txt", true))) {
            BufferedReader in = new BufferedReader(new InputStreamReader(channelExec.getInputStream()));
            String answer;
            while (active && !Thread.currentThread().isInterrupted()) {
                if ((answer = in.readLine()) != null) {
                    fileWriter.write(answer);
                    fileWriter.newLine();
                    // System.out.println(answer); // Mostra o retorno no console
                    // Verifica se o prompt final foi recebido
                    if (answer.trim().endsWith(prompt)) {
                        active = false; // Termina a leitura
                        break;
                    }
                }
            }
        } catch (Exception e) {
            if (active) {
                System.err.println("Erro de comunicação.");
            }
        }
    }

    /**
     * Envia os comandos para o servidor SSH
     * 
     * @throws JSchException
     */
    private void applyCommands() throws InterruptedException, JSchException {
        channelExec.connect(1000);
        out.println(password);
        Thread.sleep(100);
        out.println("en");
        Thread.sleep(100);
        out.println("conf t");
        Thread.sleep(100);
        out.println("screen-rows per-page 0");
        Thread.sleep(100);
        out.println("sh run");
        Thread.sleep(100);
        out.println(""); // Envia um enter para finalizar o comando
        Thread.sleep(100);
    }

    /**
     * Finaliza a sessão e fecha o canal.
     */
    private void finalMessage() {
        // Interrompe a thread, caso esteja esperando
        if (thread != null) {
            thread.interrupt();
        }
        if (channelExec != null && channelExec.isConnected()) {
            channelExec.disconnect();
        }
        if (session != null && session.isConnected()) {
            session.disconnect();
            System.out.println("Connection closed.");
        }
    }
}
