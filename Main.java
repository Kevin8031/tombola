public class Main {
    public static void main(String[] args) {
        Tabella tab = new Tabella();

        System.out.print("Tab 1");
        tab.generaTabella();
        for(int i = 0; i < tab.getTabella().length; i++) {
            if(i % 3 == 0)
                System.out.println("");
            System.out.print(tab.getTabella()[i] + " ");
        }
        tab.generaTabella();

        System.out.println("\nTab 2");
        for(int i = 0; i < tab.getTabella().length; i++) {
            if(i % 3 == 0)
                System.out.println("");
            System.out.print(tab.getTabella()[i] + " ");
        }
    }
}