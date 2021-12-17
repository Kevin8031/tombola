import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        if(scanner.nextInt() == 1)
            new Gioco(Gioco.playerType.giocatore);
        else
            new Gioco(Gioco.playerType.tabellone);
    }
}