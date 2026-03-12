package algoritmos;

import modelo.Processo;
import util.Logger;

public class Bully implements AlgoritmoEleicao {
    @Override
    public void iniciarEleicao(Processo p) {
        // Envia mensagem de eleição para processos com ID maior
        Logger.info(p.getProcessoId(), "Iniciando eleição Bully...");
        boolean maiorAtivo = false;
        for (Processo outro : p.getTodosProcessos()) {
            if (outro.getProcessoId() > p.getProcessoId()) {
                boolean enviouComSucesso = p.enviarMensagem(outro.getProcessoId(), "ELEICAO", "ELECTION_START");
                if (enviouComSucesso) {
                    maiorAtivo = true; 
                }
            }
        }
        if (!maiorAtivo) declararVencedor(p);
    }

    @Override
    public void lidarComMensagem(Processo p, String msg) {
        // Se receber uma mensagem de eleição, responde com OK e inicia sua própria eleição
        if (msg.equals("ELEICAO")) {
            p.enviarMensagem(p.getProcessoId(), "OK", "ANSWER_OK");
            iniciarEleicao(p);
        } else if (msg.startsWith("COORDENADOR:")) {
            int novoLider = Integer.parseInt(msg.split(":")[1]);
            p.setCoordenadorAtual(novoLider);
            Logger.info(p.getProcessoId(), "Novo líder reconhecido: " + novoLider);
        }
    }

    private void declararVencedor(Processo p) {
        Logger.info(p.getProcessoId(), "EU SOU O NOVO LÍDER!");
        for (Processo outro : p.getTodosProcessos()) {
            p.enviarMensagem(outro.getProcessoId(), "COORDENADOR:" + p.getProcessoId(), "COORDINATOR_ANNOUNCE");
        }
    }
}