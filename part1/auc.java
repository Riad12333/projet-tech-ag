package projet;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import jade.core.Runtime;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

public class auc extends JFrame {
    private JTextField nb;
    private JTextField durée;
    private JTextField start;
    private JTextField reserv;
    private JTextArea seller;
    private JButton startAuctionButton;
    private JPanel mainpannel;
    private JButton button1;
    private JTextArea buyer1;
    private JTextArea buyer3;

    public auc() {
        setTitle("Knapsack Problem Solver");
        setSize(900, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(true);
        setLocationRelativeTo(null); // Center the window

        // Initialize components manually to avoid null pointers when compiling without IntelliJ
        mainpannel = new JPanel();
        mainpannel.setLayout(new BoxLayout(mainpannel, BoxLayout.Y_AXIS));

        JPanel inputsPanel = new JPanel();
        nb = new JTextField("3", 5);
        durée = new JTextField("10", 5);
        start = new JTextField("100", 5);
        reserv = new JTextField("200", 5);
        inputsPanel.add(new JLabel("Nb Buyers:")); inputsPanel.add(nb);
        inputsPanel.add(new JLabel("Duration:")); inputsPanel.add(durée);
        inputsPanel.add(new JLabel("Start Price:")); inputsPanel.add(start);
        inputsPanel.add(new JLabel("Reserve Price:")); inputsPanel.add(reserv);

        button1 = new JButton("Start Auction");
        inputsPanel.add(button1);

        mainpannel.add(inputsPanel);

        JPanel textPanel = new JPanel();
        seller = new JTextArea(15, 20); textPanel.add(new JScrollPane(seller));
        buyer1 = new JTextArea(15, 20); textPanel.add(new JScrollPane(buyer1));
        buyer3 = new JTextArea(15, 20); textPanel.add(new JScrollPane(buyer3));
        
        mainpannel.add(textPanel);
        setContentPane(mainpannel);

        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addNewComponent();
            }
        });
    }
    private void addNewComponent() {
        int  nbn=Integer.parseInt( nb.getText().trim());
        int  duré=Integer.parseInt( durée.getText().trim());
        int  starte=Integer.parseInt( start.getText().trim());
        int  reserve=Integer.parseInt( reserv.getText().trim());


        try {
            Runtime rt=Runtime.instance();
            ProfileImpl p= new ProfileImpl();
            p.setParameter(Profile.LOCAL_HOST, "localhost");
            p.setParameter(Profile.LOCAL_PORT, "1099");
            p.setParameter(Profile.GUI, "true");
            p.setParameter(Profile.SERVICES, "jade.core.messaging.TopicManagementService");
            ContainerController mc=rt.createMainContainer(p);
            AgentController ag1;

            Random r = new Random();
            for(int i =1 ; i<=nbn;i++){
                int ag= r.nextInt(5000)+starte;
                String h= ag+"";
                Object[] arg={h};
                String name  ="Agent"+i;
                ag1 = mc.createNewAgent(name , buyers.class.getName(), arg);
                ag1.start();
            }
            Object[] arg1={nb.getText(),durée.getText(),start.getText(),reserv.getText()};
            ag1 = mc.createNewAgent("seller" ,projet.seller.class.getName(), arg1);
            ag1.start();

        } catch (StaleProxyException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
    public void buyer1txt(String text){
        buyer1.append(text);
        buyer1.append("\n");
        buyer1.setVisible(true);
    }
    public void buyer2txt(String text){
        buyer3.append(text);
        buyer3.append("\n");
        buyer3.setVisible(true);

    }
    public void sellertxt(String text){
        seller.append(text);
        seller.append("\n");
        seller.setVisible(true);

    }

}
