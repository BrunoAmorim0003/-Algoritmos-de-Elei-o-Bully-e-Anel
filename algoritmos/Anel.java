package algoritmos;

import modelo.Processo;
import util.Logger;
import java.util.*;
import java.util.stream.Collectors;

public class Anel implements AlgoritmoEleicao {

    @Override
    public void iniciarEleicao(Processo p) {
        Logger.info(p.getProcessoId(), "Iniciando eleição via ANEL...");
        // Inicia a lista de eleição com o próprio ID
        String listaIds = String.valueOf(p.getProcessoId());
        enviarParaProximo(p, "ANEL_ELEICAO:" + listaIds);
    }

    @Override
    public void lidarComMensagem(Processo p, String msg) {
        if (msg.startsWith("ANEL_ELEICAO:")) {
            String conteudo = msg.split(":")[1];

            // Converte a string "1,2,3" em uma lista de números
            List<Integer> idsNaLista = Arrays.stream(conteudo.split(","))
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());

            // Verificação de volta completa
            if (idsNaLista.contains(p.getProcessoId())) {
                int vencedor = Collections.max(idsNaLista);
                Logger.info(p.getProcessoId(), "Volta completa! O vencedor é: " + vencedor);
                anunciarCoordenador(p, vencedor);
            } else {
                Logger.info(p.getProcessoId(), "Adicionando meu ID à lista e passando adiante...");
                String novaLista = conteudo + "," + p.getProcessoId();
                enviarParaProximo(p, "ANEL_ELEICAO:" + novaLista);
            }
        }
        // Se a mensagem for o anúncio final do novo líder
        else if (msg.startsWith("COORDENADOR:")) {
            int novoLider = Integer.parseInt(msg.split(":")[1]);
            p.setCoordenadorAtual(novoLider);
            Logger.info(p.getProcessoId(), "Reconhecido novo líder: " + novoLider);
        }
    }

    private void enviarParaProximo(Processo p, String mensagem) {
        int total = p.getTodosProcessos().size();
        int meuId = p.getProcessoId();

        // Tenta encontrar os processos
        for (int i = 1; i <= total; i++) {
            int proximoId = (meuId + i) % total;
            // Se conseguir enviar, para de procurar
            if (p.enviarMensagem(proximoId, mensagem, "TOKEN_ANEL")) {
                return;
            }
        }
    }

    private void anunciarCoordenador(Processo p, int vencedor) {
        p.setCoordenadorAtual(vencedor);
        for (Processo outro : p.getTodosProcessos()) {
            if (outro.getProcessoId() != p.getProcessoId()) {
                p.enviarMensagem(outro.getProcessoId(), "COORDENADOR:" + vencedor, "ANUNCIO_FINAL");
            }
        }
    }
}