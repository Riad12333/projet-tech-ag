package part4;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;

public class Part4Main {
    public static void main(String[] args) {
        try {
            Runtime rt = Runtime.instance();
            Profile p = new ProfileImpl();
            p.setParameter(Profile.GUI, "true");
            ContainerController mc = rt.createMainContainer(p);

            // Create Planner
            AgentController planner = mc.createNewAgent("CentralPlanner", "part4.CentralPlannerAgent", null);
            planner.start();

            // Create Robots
            AgentController robot1 = mc.createNewAgent("Robot1", "part4.RobotAgent", new Object[]{"MoveBoxA"});
            robot1.start();

            AgentController robot2 = mc.createNewAgent("Robot2", "part4.RobotAgent", new Object[]{"MoveBoxB"});
            robot2.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
