import java.io.*;
import java.util.*;

public class Sysinfo {

    public static List<SWinfo> local_sw = new ArrayList<SWinfo>();

    private static final String REGQUERY_UTIL = "reg query ";
    private static final String REGSTR_TOKEN = "REG_SZ";
    //private static final String REGDWORD_TOKEN = "REG_DWORD";

    private static final String REG_INSTALLED_PATH_64 = "HKEY_LOCAL_MACHINE\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\";
    private static final String INSTALLED_PATH_64 = REGQUERY_UTIL +
            "\"HKEY_LOCAL_MACHINE\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\"
            + "Uninstall";
    private static final String REG_INSTALLED_PATH_32 = "HKEY_LOCAL_MACHINE\\SOFTWARE\\WOW6432Node\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\";
    private static final String INSTALLED_PATH_32 = REGQUERY_UTIL +
            "\"HKEY_LOCAL_MACHINE\\SOFTWARE\\WOW6432Node\\Microsoft\\Windows\\CurrentVersion\\"
            + "Uninstall";
    private static Scanner input = new Scanner(System.in);

    public static String getName(String path){
        try {
            Process process = Runtime.getRuntime().exec(path);
            StreamReader reader = new StreamReader(process.getInputStream());

            reader.start();
            process.waitFor();
            reader.join();

            String result = reader.getResult();
            int p = result.indexOf(REGSTR_TOKEN);

            if (p == -1)
                return null;

            return result.substring(p + REGSTR_TOKEN.length()).trim();
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }

    public static void getInstalledList(int bit) {
        try {
            String reg_path = bit==64?REG_INSTALLED_PATH_64:REG_INSTALLED_PATH_32;
            String install_path = bit==64?INSTALLED_PATH_64:INSTALLED_PATH_32;
            String str_working = bit==64?"Load Installed Software(64-bit)...":"Load Installed Software(32-bit)...";
            Process process = Runtime.getRuntime().exec(install_path);
            StreamReader reader = new StreamReader(process.getInputStream());
            ProgressBar p_loadSW = new ProgressBar(str_working);

            reader.start();
            process.waitFor();
            reader.join();

            String result = reader.getResult();
            String result_sw = result.replace(reg_path,"");
            String[] tmp_list = result_sw.split("[\\r\\n]+");
            for (int i = 0; i < tmp_list.length; i++) {
                p_loadSW.load(i, tmp_list.length - 1);
                String sw = tmp_list[i];
                String sw_name = getName(install_path + "\\" + sw + "\" /v DisplayName");
                if(sw_name == null) continue;
                sw_name = new String(sw_name.getBytes("iso-8859-1"),"EUC-KR");
                String sw_version = getName(install_path + "\\" + sw + "\" /v DisplayVersion");
                if(sw_version == null) sw_version = "Unknown";
                local_sw.add(new SWinfo(sw_name,sw_version));
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    static class StreamReader extends Thread {
        private InputStream is;
        private StringWriter sw;

        StreamReader(InputStream is) {
            this.is = is;
            sw = new StringWriter();
        }

        public void run() {
            try {
                int c;
                while ((c = is.read()) != -1)
                    sw.write(c);
            }
            catch (IOException e) { ; }
        }

        String getResult() {
            return sw.toString();
        }
    }

    public static void readInfo() {
        int scan = 1;
        if(local_sw.size() > 0){
            scan = 0;
            Cprint.w(" Do you want to load installed software again? (y,n) ","");
            String iss = input.nextLine().toLowerCase();
            if (iss.equals("y")) scan = 1;
        }
        if (scan == 1) {
            getInstalledList(64);
            getInstalledList(32);
            // Sorting
            Ascending ascending = new Ascending();
            Collections.sort(local_sw, ascending);
            Cprint.i(" Loading is complete.");
        }
        System.out.print(" Do you want to print the list? (a:ascending, d:descending, n) ");
        String isp = input.nextLine().toLowerCase();
        if(isp.equals("a")) printInfo(0);
        else if (isp.equals("d")) printInfo(1);
    }

    public static void printInfo(int sort){
        TableList info_sw = new TableList(2, "Name", "Version").sortBy(0,sort).withUnicode(true);
        for(SWinfo installed_sw : local_sw){
            info_sw.addRow(String.format("%-50.50s", installed_sw.name),String.format("%10.10s", installed_sw.version));
            //System.out.printf(" %s (%s)\n",installed_sw.name,installed_sw.version);
        }
        info_sw.print();
    }

    // Sorting Override
    private static class Ascending implements Comparator<SWinfo> {
        @Override
        public int compare(SWinfo s1, SWinfo s2) {
            return s1.name.compareTo(s2.name);
        }
    }
}