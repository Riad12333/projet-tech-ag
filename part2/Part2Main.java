package part2;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;

public class Part2Main {
    public static void main(String[] args) {
        try {
            Runtime rt = Runtime.instance();
            
            // 1. Create Main Container
            Profile pMain = new ProfileImpl();
            pMain.setParameter(Profile.CONTAINER_NAME, "Main-Container");
            pMain.setParameter(Profile.GUI, "true");
            ContainerController mc = rt.createMainContainer(pMain);
            
            // 2. Create Container 1 and Seller 1
            Profile p1 = new ProfileImpl();
            p1.setParameter(Profile.MAIN_HOST, "localhost");
            p1.setParameter(Profile.CONTAINER_NAME, "Container-1");
            ContainerController c1 = rt.createAgentContainer(p1);
            AgentController seller1 = c1.createNewAgent("Seller1", "part2.SellerAgent", new Object[]{"1000", "5", "8"});
            seller1.start();

            // 3. Create Container 2 and Seller 2
            Profile p2 = new ProfileImpl();
            p2.setParameter(Profile.MAIN_HOST, "localhost");
            p2.setParameter(Profile.CONTAINER_NAME, "Container-2");
            ContainerController c2 = rt.createAgentContainer(p2);
            AgentController seller2 = c2.createNewAgent("Seller2", "part2.SellerAgent", new Object[]{"1200", "2", "9"});
            seller2.start();

            // 4. Start Mobile Buyer on Main Container
            AgentController buyer = mc.createNewAgent("MobileBuyer", "part2.MobileBuyerAgent", null);
            buyer.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
