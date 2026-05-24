package part2;

import jade.core.Agent;
import jade.core.AID;
import jade.core.ContainerID;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.HashMap;
import java.util.Map;

public class MobileBuyerAgent extends Agent {
    private String[] containers = {"Container-1", "Container-2"};
    private int currentContainerIndex = -1;
    private Map<AID, Double> offers = new HashMap<>();

    protected void setup() {
        System.out.println("Buyer " + getLocalName() + " started in " + here().getName());
        
        // Start migration process
        addBehaviour(new MigrationBehaviour());
    }

    protected void afterMove() {
        System.out.println("Buyer " + getLocalName() + " arrived at " + here().getName());
        
        if (here().getName().equals("Main-Container")) {
            // Evaluated all, now make decision
            evaluateOffers();
        } else {
            // Ask sellers in this container
            askSellers();
        }
    }

    private void askSellers() {
        ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
        if (here().getName().equals("Container-1")) {
            cfp.addReceiver(new AID("Seller1", AID.ISLOCALNAME));
        } else if (here().getName().equals("Container-2")) {
            cfp.addReceiver(new AID("Seller2", AID.ISLOCALNAME));
        }
        cfp.setContent("I need a Laptop");
        send(cfp);

        addBehaviour(new CyclicBehaviour(this) {
            public void action() {
                MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.PROPOSE);
                ACLMessage msg = myAgent.receive(mt);
                if (msg != null) {
                    // Content format: "Price,DeliveryTime,Quality"
                    String[] parts = msg.getContent().split(",");
                    double price = Double.parseDouble(parts[0]);
                    double deliveryTime = Double.parseDouble(parts[1]);
                    double quality = Double.parseDouble(parts[2]);

                    // Multi-criteria decision function
                    // Maximize quality, minimize price and delivery time
                    double score = (quality * 10) - (price * 0.1) - (deliveryTime * 5);
                    offers.put(msg.getSender(), score);
                    System.out.println("Received offer from " + msg.getSender().getLocalName() + " Score: " + score);
                    
                    // Move to next container
                    myAgent.addBehaviour(new MigrationBehaviour());
                    myAgent.removeBehaviour(this);
                } else {
                    block();
                }
            }
        });
    }

    private void evaluateOffers() {
        AID bestSeller = null;
        double bestScore = -Double.MAX_VALUE;

        for (Map.Entry<AID, Double> entry : offers.entrySet()) {
            if (entry.getValue() > bestScore) {
                bestScore = entry.getValue();
                bestSeller = entry.getKey();
            }
        }

        if (bestSeller != null) {
            System.out.println("Best offer is from " + bestSeller.getLocalName() + " with score " + bestScore);
            ACLMessage accept = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
            accept.addReceiver(bestSeller);
            accept.setContent("Accepted");
            send(accept);
        } else {
            System.out.println("No offers received.");
        }
        doDelete();
    }

    private class MigrationBehaviour extends OneShotBehaviour {
        public void action() {
            currentContainerIndex++;
            if (currentContainerIndex < containers.length) {
                ContainerID destination = new ContainerID();
                destination.setName(containers[currentContainerIndex]);
                System.out.println("Migrating to " + destination.getName() + "...");
                myAgent.doMove(destination);
            } else {
                // Go back to main
                ContainerID destination = new ContainerID();
                destination.setName("Main-Container");
                System.out.println("Migrating back to Main-Container to decide...");
                myAgent.doMove(destination);
            }
        }
    }
}
