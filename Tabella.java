import java.util.Random;

public class Tabella {
    // constants
    final short NUMERO_POSTI = 27;
    final short NUMERO_CELLE = 15;
    final short NUMERO_SPAZI = 12;
    final short LIMITE_COLONNE = 3;
    
    // attributes
    Random random;
    private short[] tabella = new short[NUMERO_POSTI];

    // constructors
    Tabella() {
        random = new Random();
    }

    // methods
    void generaTabella() {
        for(short i = 0; i < tabella.length; i++) {
            
            
        }
    }
}