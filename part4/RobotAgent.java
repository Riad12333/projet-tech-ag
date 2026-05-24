package part4;

import jade.core.Agent;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class RobotAgent extends Agent {
    private String myTask;

    protected void setup() {
        Object[] args = getArguments();
        if (args != null && args.length > 0) {
            myTask = (String) args[0];
        } else {
            myTask = "MoveBox";
        }

        System.out.println("Robot " + getLocalName() + " is ready. My task: " + myTask);

        // Send task to Central Planner
        addBehaviour(new OneShotBehaviour() {
            public void action() {
                ACLMessage req = new ACLMessage(ACLMessage.REQUEST);
                req.addReceiver(new AID("CentralPlanner", AID.ISLOCALNAME));
                req.setContent(myTask);
                send(req);
                System.out.println(getLocalName() + " requested task: " + myTask);
            }
        });

        // Wait for plan
        addBehaviour(new CyclicBehaviour(this) {
            public void action() {
                ACLMessage msg = myAgent.receive();
                if (msg != null && msg.getPerformative() == ACLMessage.INFORM) {
                    System.out.println(getLocalName() + " received plan: " + msg.getContent());
                    executePlan(msg.getContent());
                    doDelete();
                } else {
                    block();
                }
            }
        });
    }

    private void executePlan(String plan) {
        String[] actions = plan.split("; ");
        for (String action : actions) {
            System.out.println(getLocalName() + " executing action: " + action);
            try {
                Thread.sleep(1000); // Simulate execution time
            } catch (InterruptedException e) {}
        }
        System.out.println(getLocalName() + " finished plan.");
    }
}
