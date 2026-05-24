package projet;

import javax.swing.*;


public class Main {  public auc a =new auc();

    public static void main(String[] args) {
     SwingUtilities.invokeLater(new Runnable() { public void run() {
        auc  ui = new auc();
        ui.setVisible(true);}
    });}


}
