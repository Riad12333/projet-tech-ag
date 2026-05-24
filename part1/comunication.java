package projet;

import javax.swing.*;

public class comunication extends JFrame {
    private JTextArea output;
    private JPanel pan;
    public comunication(String s) {
        pan = new JPanel();
        output = new JTextArea(30, 70);
        pan.add(new JScrollPane(output));
        
        setContentPane(pan);
        setTitle(s);
        setSize(900, 600);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(true);
        setLocationRelativeTo(null); // Center the window

    }
    public void buyer1txt(String text){
        output.append(text);
        output.append("\n");
        output.setVisible(true);
    }
    public void buyer2txt(String text){
        output.append(text);
        output.append("\n");
        output.setVisible(true);

    }
    public void sellertxt(String text){
        output.append(text);
        output.append("\n");
        output.setVisible(true);

    }
}
