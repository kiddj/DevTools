public class ProgressBar {
    private static String work;

    public ProgressBar(String name){
        work = " " + name;
    }

    public static void load(int remain, int total) {
        if (remain > total) {
            throw new IllegalArgumentException();
        }

        int maxPer = 20;
        int remainPer = (int)(maxPer * (((double)remain / total)));
        char defaultChar = ' ';

        String str_load = "#";
        String str_progress = new String(new char[maxPer]).replace('\0', defaultChar) + "]";
        StringBuilder str_done = new StringBuilder();
        str_done.append(work + " [");

        for (int i = 0; i < remainPer; i++) {
            str_done.append(str_load);
        }

        String str_remain = str_progress.substring(remainPer, str_progress.length());
        System.out.print("\r" + str_done + str_remain + " " + remainPer * (100/maxPer) + "%");

        if (remain == total) {
            System.out.print("\n");
        }
    }
}
