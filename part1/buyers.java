package projet;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.messaging.TopicManagementHelper;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.Random;
public class buyers extends Agent {


    private int prix_max;
    private Random r = new Random();
    private static final long serialVersionUID = 1L;
    private Product p = new Product();
    private ACLMessage m;
    private int k = 0;

 //public buyers(int prix_max) {


protected void setup() {
    comunication a = new comunication(getLocalName());
    // a.setVisible(true); // Log dynamically to the main dashboard instead of popping up windows
    Object[] args=getArguments();
    if(args!=null) {
        this.prix_max = Integer.valueOf((String)(args[0])).intValue();
        a.buyer1txt(" mon prix max "+this.prix_max  );



    }
    try {
        TopicManagementHelper topicHelper = (TopicManagementHelper) getHelper(TopicManagementHelper.SERVICE_NAME);
        final AID topic1 = topicHelper.createTopic("add");
        final AID topic2 = topicHelper.createTopic("winner");
        final AID topic3 = topicHelper.createTopic("no winner");
        final AID topic4 = topicHelper.createTopic("end");
        final AID topic5 = topicHelper.createTopic("stop");
        final AID topic6 = topicHelper.createTopic("buy");

        topicHelper.register(topic1);
        topicHelper.register(topic2);
        topicHelper.register(topic3);
        topicHelper.register(topic4);

        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                try {
                    ACLMessage add = receive(MessageTemplate.MatchTopic(topic1));
                    ACLMessage win = receive(MessageTemplate.MatchTopic(topic2));
                    ACLMessage noWin = receive(MessageTemplate.MatchTopic(topic3));
                    ACLMessage end = receive(MessageTemplate.MatchTopic(topic4));

                    if (add != null) {
                        Product o = (Product) add.getContentObject();
                        m = new ACLMessage(ACLMessage.INFORM);

                      if (o.name.equals("seller")){
                      a.buyer1txt("first  price :"+o.price);}else  {a.buyer2txt(o.name+" bided and win : "+o.price);}

                        System.out.println(o.name+" bided and win : "+o.price);
                        if (o.price < prix_max) {
                            p.price = r.nextInt(prix_max - (int) o.price) + (int) o.price + 1;
                            p.name = getLocalName();
                            p.message = "buy";
                            m.addReceiver(topic6);
                            m.setContentObject(p);
                            m.setLanguage("JavaSerialization");
                            send(m);
                            a.buyer1txt("I bide :"+p.price );

                            System.out.println(p.name +"bide "+p.price );

                        } else {
                            if (k == 0) {
                                p.price = prix_max;
                                p.name = getLocalName();
                                p.message = "buy";
                                m.addReceiver(topic6);
                                m.setContentObject(p);
                                m.setLanguage("JavaSerialization");
                                send(m);
                                p.message = "stop";
                                m.addReceiver(topic5);
                                m.setContentObject(p);
                                m.setLanguage("JavaSerialization");
                                a.buyer1txt("I STOP at "+p.price );

                                send(m);
                                k++;
                                System.out.println(p.name +" STOP "+p.price );

                            }
                        }
                    }

                    if (noWin != null) {

                      a.buyer2txt("No winner .");

                        System.out.println("No winner message received.");
                    }
                    if (win != null) {
                        Product o = (Product) win.getContentObject();
                        if (o.name.equals(getLocalName())) {
                            System.out.println("You are the winner!");

                           a.buyer2txt("You are the winner!");
                        } else {
                            System.out.println(o.name + " is the winner.");
                           a.buyer2txt(o.name + " is the winner.");
                        }
                    }

                    if (end != null) {
                        a.buyer2txt(" end");

                        doDelete();

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    } catch (Exception e) {
        e.printStackTrace();
    }
}
}