public class Gioco {
    Giocatore giocatore;
    Master master;

    enum playerType {
        giocatore,
        tabellone
    }

    Gioco(playerType a) {
        if(a == playerType.giocatore) {
            giocatore = new Giocatore();
        }
        else {
            master = new Master();
        }
    }

    
}
