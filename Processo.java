import java.net.ServerSocket;
import java.net.Socket;
import java.io.*;
import java.lang.System.Logger;

public class Processo {
    private int id;
    private int coordenadorAtual;
    private boolean ativo;
    private ServerSocket servidor;
    private int porta;
    
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
            Logger.log("Processo " + id + " iniciado na porta " + porta);
        } catch (IOException e) {
            Logger.log("ERRO: Processo " + id + " não pode iniciar servidor");
        }
    }
    
    public void enviarMensagem(int destino, String mensagem, String tipo) {
        try (Socket socket = new Socket("localhost", destino)) {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println(mensagem);
            Logger.log("[ENVIO] Processo " + id + " -> " + destino + " [" + tipo + "]: " + mensagem);
        } catch (IOException e) {
            Logger.log("[FALHA] Processo " + id + " não conseguiu enviar mensagem para " + destino);
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
        Logger.log("[FALHA] Processo " + id + " falhou!");
    }
    
    public void recuperar() {
        this.ativo = true;
        try {
            if (servidor == null || servidor.isClosed()) {
                servidor = new ServerSocket(porta);
            }
        } catch (IOException e) {
            Logger.log("ERRO: Processo " + id + " não pode recuperar servidor");
        }
        Logger.log("[RECUPERAÇÃO] Processo " + id + " recuperado!");
    }
    
    // Getters e Setters
    public int getId() { return id; }
    public boolean isAtivo() { return ativo; }
    public int getCoordenadorAtual() { return coordenadorAtual; }
    public void setCoordenadorAtual(int coord) { 
        this.coordenadorAtual = coord;
        Logger.log("[COORDENADOR] Processo " + id + " reconhece " + coord + " como líder");
    }
    public int getPorta() { return porta; }
    public ServerSocket getServidor() { return servidor; }
}