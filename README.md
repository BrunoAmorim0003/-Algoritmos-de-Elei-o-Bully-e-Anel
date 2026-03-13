# Algoritmos de Eleição em Sistemas Distribuídos (Bully & Anel)

Este projeto simula a coordenação de processos em sistemas distribuídos através da implementação dos algoritmos de eleição **Bully** e **Anel**. Desenvolvido para a disciplina de Sistemas Distribuídos do IFBA.

## 🚀 Tecnologias e Conceitos
- **Linguagem:** Java.
- **Comunicação:** Sockets TCP/IP (localhost).
- **Concorrência:** Threads independentes para cada processo.
- **Arquitetura:** Padrão Strategy para alternância dinâmica de algoritmos.

## 🏗️ Estrutura do Projeto
O sistema simula um ecossistema de 5 nós autônomos. Cada nó atua como um processo P2P com seu próprio servidor de escuta em portas sequenciais (5000-5004).

### Algoritmos Implementados
1. **Bully:** Baseado na premissa de que o processo com maior ID deve liderar. Possui característica preemptiva em caso de recuperação de nós superiores.
2. **Anel:** Organização lógica circular onde a eleição ocorre via passagem de um token contendo a lista de IDs ativos.

## 🛠️ Otimizações Técnicas
- **Monitoramento (Heartbeat):** Mecanismo assíncrono que detecta falhas via exceções de Socket e dispara eleições automaticamente.
- **Jitter:** Introdução de atraso aleatório no ciclo de monitoramento para evitar tempestades de mensagens durante falhas simultâneas.
- **Graceful Shutdown:** Gerenciamento seguro do ciclo de vida das threads para evitar processos "zumbis" após a simulação.

## 🧪 Cenários de Teste
A classe `Main` orquestra automaticamente dois cenários críticos:
- **Cenário A:** Falha e recuperação do coordenador (ID 4).
- **Cenário B:** Falha simultânea de múltiplos nós (IDs 4 e 3).

## 📖 Como Executar
1. Compile os arquivos Java.
2. Execute a classe `main.Main`.