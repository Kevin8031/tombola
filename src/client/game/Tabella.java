package client.game;
import java.io.Serializable;
import java.util.*;

public class Tabella implements Serializable{
	// constants
	public static final short RIGHE = 3;
	public static final short COLONNE = 9;
	private final short NUMERO_CELLE = 15;
	
	// attributes
	private Random random;
	protected int[] tabella;
	private int numPresentiRighe[];
	protected ArrayList<Integer> numeriEstratti;
	private int[] numeriDuplicati;
	private boolean[] combo;

	// constructors
	public Tabella() {
		random = new Random();
		tabella = new int[RIGHE * COLONNE];
		numPresentiRighe = new int[RIGHE];
		numeriEstratti = new ArrayList<Integer>(90);
		numeriDuplicati = new int[NUMERO_CELLE];
		combo = new boolean[4];
		Arrays.fill(tabella, -1);
	}

	// methods
	public void generaTabella() {
		Arrays.fill(tabella, -1);
		Arrays.fill(numeriDuplicati, 0);

		int celle = NUMERO_CELLE;
		int numeroGen = 0;

		int k = 0;
		while (celle > 0) {
			for(int i = 0; i < RIGHE; i++) {
				for(int j = 0; j < COLONNE; j++) {
					if(numPresentiRighe[i] < 5)
						if(tabella[i + RIGHE * j] == -1)
							if(random.nextInt(2) == 1) {
								do
									numeroGen = GeneraNumero(i + RIGHE * j);
								while(Duplicato(numeroGen));

								tabella[i + RIGHE * j] = numeroGen;
								numPresentiRighe[i]++;
								numeriDuplicati[k++] = numeroGen;
								celle--;
							}
				}
			}
		}
		Sort();
	}

	private int GeneraNumero(int num) {
		Random random = new Random();

		if(num < 3)
			return random.nextInt(9) + 1;
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
		for (int i : numeriDuplicati) {
			if(i == num)
				return true;
		}
		return false;
	}

	private void Sort() {
		int numColona = 0;
		int i = 0;
		int j;

		int posNum1 = -1;
		int posNum2 = -1;

		while (i < RIGHE * COLONNE) {

			do {
				if(tabella[i++] != -1)
					numColona++;
			}
			while(i % 3 != 0);

			switch (numColona) {
				case 3:
					Arrays.sort(tabella, i - 3, i);
					break;
				case 2:
					j = i - 3;
					while (j < i) {
						if(tabella[j] != -1) {
							if(posNum1 == -1)
								posNum1 = j;
							else
								posNum2 = j;
						}
						j++;
					}

					if(tabella[posNum1] > tabella[posNum2]) {
						int num = tabella[posNum1];
						tabella[posNum1] = tabella[posNum2];
						tabella[posNum2] = num;
					}
					posNum1 = -1;
					posNum2 = -1;
					break;

				default:
					break;
			}

			numColona = 0;
		}
	}

	public Combo CheckCombo() {
		int numeriRiga[] = new int[3];
		for(int i = 0; i < RIGHE; i++)
			for(int j = 0; j < COLONNE; j++) {
				for (int num : numeriEstratti) {
					if(tabella[i + RIGHE * j] == num)
						numeriRiga[i]++;
				}
			}

		for (int num : numeriRiga) {
			switch (num) {
				case 2:
					combo[0] = true;
					return Combo.Ambo;
				case 3:
					combo[1] = true;
					return Combo.Terno;
				case 4:
					combo[2] = true;
					return Combo.Quaterna;
				case 5:
					combo[3] = true;
					return Combo.Cinquina;
			}
		}
		return Combo.invalid;
	}

	public void AddNumber(int num) {
		numeriEstratti.add(num);
	}
	@Override
	public String toString() {
		String s = new String();

		for (int i : tabella) {
			s += String.valueOf(i) + ' ';
		}

		return s;
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

	public void Reset() {
		numeriEstratti.clear();
		generaTabella();
	}

	public ArrayList<Integer> getNumeriEstratti() {
		return numeriEstratti;
	}

	public void setNumeriEstratti(ArrayList<Integer> numeriEstratti) {
		this.numeriEstratti = numeriEstratti;
	}
}