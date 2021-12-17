public class Gioco {
    enum giocatore {
        giocatore,
        tabellone
    }

    Gioco(giocatore a) {
        if(a == giocatore.giocatore) {
            new Giocatore();
        }
        else {
            new Master();
        }
    }

    
}
