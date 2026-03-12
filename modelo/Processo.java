package modelo;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import algoritmos.AlgoritmoEleicao;
import java.io.*;
import util.Logger;

public class Processo extends Thread {
    private int id;
    private int coordenadorAtual;
    private boolean ativo;
    private ServerSocket servidor;
    private int porta;
    private List<Processo> todosProcessos;
    private AlgoritmoEleicao algoritmo;

    public Processo(int id, int portaBase) {
        this.id = id;
        this.coordenadorAtual = -1;
        this.ativo = true;
        this.porta = portaBase + id;
        iniciarServidor();
    }

    private void iniciarServidor() {
        try {
            servidor = new ServerSocket(porta);
            Logger.info(this.id, "Processo " + id + " iniciado na porta " + porta);
        } catch (IOException e) {
            Logger.info(this.id, "ERRO: Processo " + id + " não pode iniciar servidor");
        }
    }

    public boolean enviarMensagem(int destino, String mensagem, String tipo) {
        int portaDestino = this.porta - this.id + destino;
        try (Socket socket = new Socket("localhost", portaDestino)) {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println(mensagem);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public void falhar() {
        this.ativo = false;
        try {
            if (servidor != null && !servidor.isClosed()) {
                servidor.close();
            }
        } catch (IOException e) {
            // Ignorar erro ao fechar
        }
        Logger.info(this.id, "[FALHA] Processo " + id + " falhou!");
    }

    public void recuperar() {
        this.ativo = true;
        try {
            if (servidor == null || servidor.isClosed()) {
                servidor = new ServerSocket(porta);
            }
        } catch (IOException e) {
            Logger.info(this.id, "ERRO: Processo " + id + " não pode recuperar servidor");
        }
        Logger.info(this.id, "[RECUPERAÇÃO] Processo " + id + " recuperado!");
    }

    public void iniciarMonitoramento() {
        new Thread(() -> {
            while (ativo) {
                try {
                    Thread.sleep(3000); // Checa a cada 3 segundos
                    if (coordenadorAtual != -1 && coordenadorAtual != this.id) {
                        boolean liderVivo = enviarMensagem(coordenadorAtual, "PING", "CHECK");
                        if (!liderVivo) {
                            util.Logger.info(this.id, "Detectei que o coordenador " + coordenadorAtual + " caiu!");
                            this.algoritmo.iniciarEleicao(this);
                        }
                    }
                } catch (InterruptedException e) {
                    break;
                }
            }
        }).start();
    }

    public void iniciarEleicao(Processo iniciador) {
        if (this.algoritmo != null) {
            this.algoritmo.iniciarEleicao(iniciador);
        } else {
            util.Logger.info(this.id, "ERRO: Nenhum algoritmo de eleição foi definido!");
        }
    }

    @Override
    public void run() {
        while (ativo) {
            try (Socket cliente = servidor.accept();
                    BufferedReader in = new BufferedReader(new InputStreamReader(cliente.getInputStream()))) {
                String msg = in.readLine();
                if (msg != null && algoritmo != null) {
                    algoritmo.lidarComMensagem(this, msg);
                }
            } catch (IOException e) {
                if (ativo)
                    Logger.info(id, "Erro ao receber mensagem: " + e.getMessage());
            }
        }
    }

    // Getters e Setters
    public int getProcessoId() {
        return this.id;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public int getCoordenadorAtual() {
        return coordenadorAtual;
    }

    public void setCoordenadorAtual(int coord) {
        this.coordenadorAtual = coord;
        Logger.info(this.id, "[COORDENADOR] Processo " + id + " reconhece " + coord + " como líder");
    }

    public int getPorta() {
        return porta;
    }

    public ServerSocket getServidor() {
        return servidor;
    }

    public List<Processo> getTodosProcessos() {
        return todosProcessos;
    }

    public void setTodosProcessos(List<Processo> processos) {
        this.todosProcessos = processos;
    }

    public void setAlgoritmo(AlgoritmoEleicao alg) {
        this.algoritmo = alg;
    }

}