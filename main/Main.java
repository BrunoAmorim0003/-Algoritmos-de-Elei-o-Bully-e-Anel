package main;

import algoritmos.*;
import modelo.Processo;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        // Roda algoritmo Bully primeiro
        executarCenario("BULLY", new Bully());

        String separator = new String(new char[60]).replace('\0', '=');
        System.out.println("\n" + separator + "\n");

        // Roda algoritmo Anel depois
        executarCenario("ANEL", new Anel());

        System.out.println(">>> SIMULAÇÕES FINALIZADAS COM SUCESSO! <<<");
        System.exit(0);
    }

    public static void executarCenario(String nomeAlgoritmo, AlgoritmoEleicao algoritmo) throws InterruptedException {
        System.out.println(" INICIANDO TESTES: " + nomeAlgoritmo);

        int numProcessos = 5;
        int portaBase = 5000;
        List<Processo> processos = new ArrayList<>();

        // Instancia e liga os processos
        for (int i = 0; i < numProcessos; i++) {
            processos.add(new Processo(i, portaBase));
        }

        for (Processo p : processos) {
            p.setTodosProcessos(processos);
            p.setAlgoritmo(algoritmo);
            p.setCoordenadorAtual(4);
            p.start();
            p.iniciarMonitoramento();
        }

        Thread.sleep(3000);

        // =========================================================
        // CENÁRIO A: Coordenador falha e retorna após eleição
        // =========================================================
        System.out.println("\n--- [CENÁRIO A] DERRUBANDO O COORDENADOR (ID 4) ---");
        processos.get(4).falhar();

        
        Thread.sleep(8000); // Tempo para os processos detectarem a falha

        System.out.println("\n--- [CENÁRIO A] RECUPERANDO O COORDENADOR (ID 4) ---");
        processos.get(4).recuperar();
        processos.get(4).iniciarEleicao(processos.get(4));

        Thread.sleep(8000); 

        // =========================================================
        // CENÁRIO B: Múltiplos processos falham
        // =========================================================
        System.out.println("\n--- [CENÁRIO B] MÚLTIPLAS FALHAS SIMULTÂNEAS ---");
        System.out.println("Derrubando os processos 4 e 3...");
        processos.get(4).falhar();
        processos.get(3).falhar();

        // Espera a eleição acontecer entre os restantes (0, 1 e 2)
        Thread.sleep(10000);

        // =========================================================
        // LIMPEZA: Desliga tudo para a próxima rodada
        // =========================================================
        System.out.println("\n--- ENCERRANDO PROCESSOS DO TESTE " + nomeAlgoritmo + " ---");
        for (Processo p : processos) {
            p.falhar(); // Fecha os sockets para liberar a porta
        }
        Thread.sleep(2000); // Dá tempo para o sistema operacional liberar as portas de rede
    }
}