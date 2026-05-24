package projet;

//public class general_info {
//    public static int stop  = 0;
//    private static long startTime = System.currentTimeMillis();
//    public static long duréeauc =0
//            public long n=0;
//
//    public static long  setStart(startTime = System.currentTimeMillis())
//    public static long getTimeSinceStart() {
//        return System.currentTimeMillis() - startTime;
//    }
//
//}
class GeneralInfo {

    private static long startTime;

    public static void setStart() {
        startTime = System.currentTimeMillis();
    }

    public static long getTimeSinceStart() {
        return System.currentTimeMillis() - startTime;
    }
}