import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        ArrayList<Integer> a = new ArrayList<Integer>();
        System.out.println(a.indexOf(5));

        Scanner scanner = new Scanner(System.in);

        if(scanner.nextInt() == 1)
            new Gioco(Gioco.playerType.giocatore);
        else
            new Gioco(Gioco.playerType.tabellone);
    }
}