package part2;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class SellerAgent extends Agent {
    private double price;
    private double deliveryTime;
    private double quality;

    protected void setup() {
        Object[] args = getArguments();
        if (args != null && args.length == 3) {
            price = Double.parseDouble((String)args[0]);
            deliveryTime = Double.parseDouble((String)args[1]);
            quality = Double.parseDouble((String)args[2]);
        } else {
            price = 1000; deliveryTime = 5; quality = 8;
        }

        System.out.println("Seller " + getLocalName() + " ready in " + here().getName());

        addBehaviour(new CyclicBehaviour(this) {
            public void action() {
                ACLMessage msg = myAgent.receive();
                if (msg != null) {
                    if (msg.getPerformative() == ACLMessage.CFP) {
                        ACLMessage reply = msg.createReply();
                        reply.setPerformative(ACLMessage.PROPOSE);
                        reply.setContent(price + "," + deliveryTime + "," + quality);
                        myAgent.send(reply);
                    } else if (msg.getPerformative() == ACLMessage.ACCEPT_PROPOSAL) {
                        System.out.println(getLocalName() + ": My offer was accepted!");
                    }
                } else {
                    block();
                }
            }
        });
    }
}
