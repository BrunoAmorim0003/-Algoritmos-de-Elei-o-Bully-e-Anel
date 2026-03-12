package util;

public class Logger {
    public static void info(int id, String mensagem) {
        System.out.println("[Processo " + id + "] " + mensagem);
    }

}