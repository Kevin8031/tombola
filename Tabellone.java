public class Tabellone extends Tabella {
    final private short RIGHE = 9;
    final private short COLONNE = 10;

    Tabellone() {
        tabella = new int[RIGHE * COLONNE];
        generaTabella();
    }

    @Override
    public void generaTabella() {
        for (int i = 0; i < RIGHE * COLONNE; i++)
            tabella[i] = i + 1;
    }
}
