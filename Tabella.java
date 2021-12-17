import java.util.Arrays;
import java.util.Random;

public class Tabella {
    // constants
    final short RIGHE = 3;
    final short COLONNE = 9;
    final short NUMERO_CELLE = 15;
    
    // attributes
    Random random;
    private int[] tabella;
    private int[] doppioni;

    // constructors
    Tabella() {
        random = new Random();
        tabella = new int[RIGHE * COLONNE];
        doppioni = new int[NUMERO_CELLE];
        Arrays.fill(tabella, -1);

        // generaTabella();
    }

    // methods
    public void generaTabella() {
        short celle = NUMERO_CELLE;
        short index = 0;
        short j = 0;
        while (celle > 0) {
            if(index < RIGHE * COLONNE) {
                    int num = random.nextInt(3 + 1);
                    // int offset = 0;
                    // if (num < 3) {
                    //     offset = random.nextInt(RIGHE - num);
                    //     index += offset;
                    // }
                    for(int i = 0; i < num;) {
                        int numeroGen = GeneraNumero(index);
                        if(!Duplicato(numeroGen)) {
                            if(tabella[index] == -1) {
                                tabella[index++] = numeroGen;
                                doppioni[j++] = numeroGen;
                                celle--;
                                i++;
                            }
                            else {
                                index++;
                                i++;
                            }
                        }
                    }
                    index += RIGHE - num;        //was index += RIGHE - offset - num;

                    Arrays.sort(tabella, index -3 , index);
                    if(index == RIGHE * COLONNE)
                        index = 0;
            }
            else {
                index = 0;
            }
        }
    }

    private int GeneraNumero(int num) {
        Random random = new Random();

        if(num < 3)
            return random.nextInt(10 + 1);
        else if(num < 6)
            return random.nextInt(10) + 10;
        else if(num < 9)
            return random.nextInt(10) + 20;
        else if(num < 12)
            return random.nextInt(10) + 30;
        else if(num < 15)
            return random.nextInt(10) + 40;
        else if(num < 18)
            return random.nextInt(10) + 50;
        else if(num < 21)
            return random.nextInt(10) + 60;
        else if(num < 24)
            return random.nextInt(10) + 70;
        else
            return random.nextInt(11) + 80;
    }
    
    private boolean Duplicato(int num) {
        for (int i : doppioni) {
            if(i == num)
                return true;
        }

        return false;
    }

    // getters and setters
    public int[] getTabella() {
        return tabella;
    }

    public void setTabella(int[] tabella) {
        this.tabella = tabella;
    }
    
}