import java.util.Arrays;
import java.util.Random;

public class Tabella {
    // constants
    final short RIGHE = 3;
    final short COLONNE = 9;
    final short NUMERO_CELLE = 15;
    int numPresentiRighe[];
    
    // attributes
    private Random random;
    protected int[] tabella;
    private int[] doppioni;

    // constructors
    Tabella() {
        random = new Random();
        numPresentiRighe = new int[RIGHE];
        tabella = new int[RIGHE * COLONNE];
        doppioni = new int[NUMERO_CELLE];
        Arrays.fill(tabella, -1);
    }

    // methods
    // TODO: per ogni riga della tabella, devono esserci solo e soltanto 5 numeri
    public void generaTabella() {
        Arrays.fill(tabella, -1);
        Arrays.fill(doppioni, 0);

        short celle = NUMERO_CELLE;
        short index = 0;
        short j = 0;
        while (celle > 0) {
            if(index < RIGHE * COLONNE - 1) {
                int num = random.nextInt(3 + 1);
                
                if(celle - num <= 0)
                    num = celle;
                
                int i = num;
                while(i > 0) {
                    // if(tabella[index] == -1) {
                    //     for(int y = 0; y < RIGHE; y++)
                    //         for(int k = 0; k < COLONNE; k++)
                    //         {
                    //             tabella[y + RIGHE * k];
                    //         }
                        int numeroGen = GeneraNumero(index);
                        if(!Duplicato(numeroGen)) {
                            numPresentiRighe[num - i]++;
                            tabella[index++] = numeroGen;
                            doppioni[j++] = numeroGen;
                            celle--;
                            i--;
                        }
                    // }
                        else {
                            i--;
                            num--;
                        }
                }

                index += RIGHE - num;        //was index += RIGHE - offset - num;

                Arrays.sort(tabella, index -3 , index);
                // if(index == RIGHE * COLONNE - 1)
                //     index = 0;
            }
            else {
                index = 0;
            }
        }
    }

    private int GeneraNumero(int num) {
        Random random = new Random();

        if(num < 3)
            return random.nextInt(10) + 1;
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
            return random.nextInt(10 + 1) + 80;
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

    public int getTabella(int index) {
        return tabella[index];
    }

    public void setTabella(int[] tabella) {
        this.tabella = tabella;
    }
}