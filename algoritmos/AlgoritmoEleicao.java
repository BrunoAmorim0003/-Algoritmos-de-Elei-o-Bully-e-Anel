package algoritmos;

import modelo.Processo;

public interface AlgoritmoEleicao {
    void iniciarEleicao(Processo processoIniciador);
    void lidarComMensagem(Processo processoAtual, String mensagem);
}
