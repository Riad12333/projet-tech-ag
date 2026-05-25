package projet;

public class comunication {
    private String agentName;

    public comunication(String agentName) {
        this.agentName = agentName;
    }

    public void buyer1txt(String text) {
        if (auc.instance != null) {
            auc.instance.buyer1txt("[" + agentName + "] " + text);
        }
    }

    public void buyer2txt(String text) {
        if (auc.instance != null) {
            auc.instance.buyer2txt("[" + agentName + "] " + text);
        }
    }

    public void sellertxt(String text) {
        if (auc.instance != null) {
            auc.instance.sellertxt("[" + agentName + "] " + text);
        }
    }
}
