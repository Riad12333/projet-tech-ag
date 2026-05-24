package projet;//import jade.core.AID;
//import jade.core.Agent;
//import jade.core.behaviours.CyclicBehaviour;
//import jade.lang.acl.ACLMessage;
//
//import java.io.IOException;
//
//public class seller extends Agent {
//    int nb ;
//    int price=0;
//    String sender;
//    int reserve_price;
//    int dure;
//    int agent=0;
//    int maxprice=0;
//    int s=0;
//
//    seller(int nb, int dure, int reserve_price ){
//        this.nb=nb;
//        this.dure=dure;
//        this.reserve_price=reserve_price;
//
//    }
//
//    protected void setup() {
//        TopicManagementHelper topicHelper=(TopicManagementHelper)getHelper(TopicManagementHelper.SERVICE_NAME);
//        final AID topic1=topicHelper.createTopic("add");
//        final AID topic2=topicHelper.createTopic("winner");
//        final AID topic3=topicHelper.createTopic("no winner");
//        final AID topic4=topicHelper.createTopic("end");
//
//        final AID topic5=topicHelper.createTopic("stop");
//        topicHelper.register(topic5);
//        final AID topic6=topicHelper.createTopic("buy");
//        topicHelper.register(topic6);
//
//        topicHelper.register(topic4);
//        general_info.setStart();
//        produt p;
//        p.price=reserve_price;
//        p.name=getLocalName();
//        p.message="add";
//        ACLMessage m = new ACLMessage(ACLMessage.INFORM);
//       // m.setContent("give the first price:");
//        m.setContentObject(p);
//        int n=this.nb;
//        m.addReceiver(add);
////        for (int i=0 ; i<this.nb; i++){
////        m.addReceiver(new AID("Agent"+i, AID.ISLOCALNAME));}
//
//        send(m);
//        addBehaviour(new CyclicBehaviour() {
//            @Override
//            product P;
//            product o;
//
//
//
//            public void action() {
//                ACLMessage buy=receive(MessageTemplate.MatchTopic(topic6));
//                ACLMessage stop=receive(MessageTemplate.MatchTopic(topic5));
//        //        buy= receive() ;
//
//                if(buy!= null)
//                {
//                    o=(product) buy.getContentObject();
////                    if (o.message.equals("stop")){s++;}else{
//                    price =o.price;
//                    agent ++;
//                    if (price>maxprice){P=o;}}
//                if(stop!=null){s++;}
//                    if (agent==this.nb-s ) {
//                        agent=0;
//                        //price=Integer.parseInt(msg.getContent());
//                        //sender = msg.getSender().getLocalName();
//                       ACLMessage m = new ACLMessage(ACLMessage.INFORM);
////                        m.addReceiver(new AID("auction", AID.ISLOCALNAME));
////                        try {
////                            m.setContentObject(P);
////
////                            m.setLanguage("JavaSerialization");
////                            send(m);
////                        } catch (IOException e) {
////                            throw new RuntimeException(e);
////                        }
//
//
//
////                        for (int i = 0; i < n; i++) {
//                          m.addReceiver(add);
//                                  //new AID("Agent" + i, AID.ISLOCALNAME));
////                        }
//                        m.setContentObject(P);
//
//                        m.setLanguage("JavaSerialization");
//                        send(m);
//                    }
//
//                }
//                if (dure== general_info.getTimeSinceStart()|| s==this.nb){
//
//                    ACLMessage m2 = new ACLMessage(ACLMessage.INFORM);
//                    product l =new product();
//                    if(P.price>=reserve_price){
//                        l.price="";
//                        l.name="";
//                        l.message="winner";
//                        m.setContentObject(P);
//                        }else{
//                        l=P ;
//                        l.message="winner";
//                    }
//                    m.setContentObject(P);
//
//                    for (int i=0 ; i<n; i++){
//                        m2.addReceiver(new AID("Agent"+i, AID.ISLOCALNAME));
//                        m.setLanguage("JavaSerialization");
//                        send(m);}
//
//
//                    ACLMessage m3 = new ACLMessage(ACLMessage.INFORM);
//
//                        m3.setContent("doDelete");
//
//                    for (int i=0 ; i<n; i++){
//                        m3.addReceiver(new AID("Agent"+i, AID.ISLOCALNAME));}
//                    doDelete();
//
//                }
//
//            }
//        });
//    }}
//
//
//

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.messaging.TopicManagementHelper;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class seller extends Agent {
    private int nb;
    private int price = 0;
    private String sender;
    private int reserve_price;
    private int dure;
    private int agent = 0;
    private int maxprice = 0;
    private int s = 0;
    private Product P =null;
    private int first_price;

//    public seller(int nb, int dure, int reserve_price) {
//        this.nb = nb;
//        this.dure = dure;
//        this.reserve_price = reserve_price;
//    }

    protected void setup() {
        comunication a = new comunication("seller");
        a.setVisible(true);
        Object[] args=getArguments();
        if(args!=null) {
            this.dure = Integer.valueOf((String)(args[1])).intValue();;
            this.reserve_price=Integer.valueOf((String)(args[3])).intValue();
            this.first_price=Integer.valueOf((String)(args[2])).intValue();
            this.nb = Integer.valueOf((String)(args[0])).intValue();

    System.out.println("first  "+ this.nb +this.dure);
        }
        try {
            TopicManagementHelper topicHelper = (TopicManagementHelper) getHelper(TopicManagementHelper.SERVICE_NAME);
            final AID topic1 = topicHelper.createTopic("add");
            final AID topic2 = topicHelper.createTopic("winner");
            final AID topic3 = topicHelper.createTopic("no winner");
            final AID topic4 = topicHelper.createTopic("end");
            final AID topic5 = topicHelper.createTopic("stop");
            final AID topic6 = topicHelper.createTopic("buy");

            topicHelper.register(topic5);
            topicHelper.register(topic6);
            topicHelper.register(topic4);

            GeneralInfo.setStart();
            Product p = new Product();
            p.price = first_price;
            p.name = getLocalName();
            p.message = "add";

            ACLMessage m = new ACLMessage(ACLMessage.INFORM);
            m.setContentObject(p);
            m.addReceiver(topic1);
            send(m);

            a.sellertxt("start auction ");


            addBehaviour(new CyclicBehaviour() {
                @Override
                public void action() {
                    try {
                        ACLMessage buy = receive(MessageTemplate.MatchTopic(topic6));
                        ACLMessage stop = receive(MessageTemplate.MatchTopic(topic5));

                        if (buy != null) {
                            Product o = (Product) buy.getContentObject();
                            price = o.price;
                            agent++;
                            if (price > maxprice) {
                                P = o;
                                maxprice = price;
                            }
                            a.buyer2txt(o.name+" bide "+o.price);
                            System.out.println(o.name+" bide "+o.price);
                        }
                        if (stop != null) {
                            Product o = (Product) stop.getContentObject();

                            s++;
//                            if (o.name.equals("Agent2")){
//
//                            a.buyer2txt(o.name+"stopped");}else{a.buyer1txt(o.name+"stopped");
//                            }
                            a.buyer2txt(o.name+"stopped"+" :"+"  "+o.price);
                            System.out.println(o.name+"stopped"+" :"+o.price);
                        }
                        if (agent == nb - s) {
                            agent = 0;
                            ACLMessage m = new ACLMessage(ACLMessage.INFORM);
                            m.addReceiver(topic1);
                            m.setContentObject(P);
                            m.setLanguage("JavaSerialization");
                            send(m);
                        }

                        if (dure <= GeneralInfo.getTimeSinceStart() || s == nb || nb-s==1) {
                            ACLMessage m2 = new ACLMessage(ACLMessage.INFORM);
                            Product l = new Product();

                            if (P.price >= reserve_price ) {
                                l.price = P.price;
                                l.name = P.name;
                                l.message = "winner";
                                m2.setContentObject(l);
                                m2.setLanguage("JavaSerialization");
                                m2.addReceiver(topic2);
                                send(m2);

                                a.sellertxt("winner "+l.name);

                            } else {
                                l = P;
                                l.message = "no winner";

                                a.sellertxt("no winner ");
                                m2.setContentObject(l);
                                m2.setLanguage("JavaSerialization");

                                m2.addReceiver(topic3);
                                send(m2);



                        }

                                ACLMessage m3 = new ACLMessage(ACLMessage.INFORM);
                            m3.setContentObject(l);
                            m3.setLanguage("JavaSerialization");

                            m3.addReceiver(topic4);
                            send(m3);
                            if(dure <= GeneralInfo.getTimeSinceStart() ){
                                a.sellertxt("end time of auction ");

                            }

                            a.sellertxt("end auction  ");

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