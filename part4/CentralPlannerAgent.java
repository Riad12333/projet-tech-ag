package part4;

import jade.core.Agent;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.ArrayList;
import java.util.List;

public class CentralPlannerAgent extends Agent {
    
    private List<String> receivedTasks = new ArrayList<>();
    private List<AID> registeredRobots = new ArrayList<>();

    protected void setup() {
        System.out.println("Central Planner " + getLocalName() + " is ready.");

        addBehaviour(new CyclicBehaviour(this) {
            public void action() {
                ACLMessage msg = myAgent.receive();
                if (msg != null) {
                    if (msg.getPerformative() == ACLMessage.REQUEST) {
                        // Robot sends a task
                        String task = msg.getContent();
                        System.out.println("Planner received task: " + task + " from " + msg.getSender().getLocalName());
                        receivedTasks.add(task);
                        if (!registeredRobots.contains(msg.getSender())) {
                            registeredRobots.add(msg.getSender());
                        }
                        
                        // If we have 2 tasks (for 2 robots), generate and distribute plan
                        if (receivedTasks.size() == 2) {
                            generateAndDistributePlan();
                        }
                    }
                } else {
                    block();
                }
            }
        });
    }

    private void generateAndDistributePlan() {
        System.out.println("Generating centralized plan for " + receivedTasks.size() + " tasks...");
        
        // Simple Centralized Planning: 
        // We know Robot1 wants to do TaskA, Robot2 wants to do TaskB.
        // The plan ensures they don't collide.
        // Plan for Robot1: Wait 1 sec, Execute TaskA
        // Plan for Robot2: Execute TaskB, Wait 1 sec
        
        for (int i = 0; i < registeredRobots.size(); i++) {
            AID robot = registeredRobots.get(i);
            ACLMessage reply = new ACLMessage(ACLMessage.INFORM);
            reply.addReceiver(robot);
            
            if (i == 0) {
                reply.setContent("WAIT; " + receivedTasks.get(0));
            } else {
                reply.setContent(receivedTasks.get(1) + "; WAIT");
            }
            send(reply);
            System.out.println("Sent plan to " + robot.getLocalName() + ": " + reply.getContent());
        }
        
        receivedTasks.clear();
        registeredRobots.clear();
    }
}
