public class ProgressBar {

    private static String work;

    public ProgressBar(String name){
        work = name;
    }

    public static void load(int remain, int total) {
        if (remain > total) {
            throw new IllegalArgumentException();
        }
        int maxBareSize = 20; // 10unit for 100%
        int remainProcent = (int)(maxBareSize * (((double)remain / total)));
        char defaultChar = ' ';
        String icon = "#";
        String bare = new String(new char[maxBareSize]).replace('\0', defaultChar) + "]";
        StringBuilder bareDone = new StringBuilder();
        bareDone.append(work + " [");
        for (int i = 0; i < remainProcent; i++) {
            bareDone.append(icon);
        }
        String bareRemain = bare.substring(remainProcent, bare.length());
        System.out.print("\r" + bareDone + bareRemain + " " + remainProcent * (100/maxBareSize) + "%");
        if (remain == total) {
            System.out.print("\n");
        }
    }
}
