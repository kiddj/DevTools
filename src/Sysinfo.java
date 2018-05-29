import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Sysinfo {

    private static List<SWinfo> list_sw = new ArrayList<SWinfo>();
    private static final String REGQUERY_UTIL = "reg query ";
    private static final String REGSTR_TOKEN = "REG_SZ";
    private static final String REGDWORD_TOKEN = "REG_DWORD";

    private static final String REG_INSTALLED_PATH_64 = "HKEY_LOCAL_MACHINE\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\";
    private static final String INSTALLED_PATH_64 = REGQUERY_UTIL +
            "\"HKEY_LOCAL_MACHINE\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\"
            + "Uninstall";
    private static final String REG_INSTALLED_PATH_32 = "HKEY_LOCAL_MACHINE\\SOFTWARE\\WOW6432Node\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\";
    private static final String INSTALLED_PATH_32 = REGQUERY_UTIL +
            "\"HKEY_LOCAL_MACHINE\\SOFTWARE\\WOW6432Node\\Microsoft\\Windows\\CurrentVersion\\"
            + "Uninstall";

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
        String loading = new String();
        try {
            String reg_path = bit==64?REG_INSTALLED_PATH_64:REG_INSTALLED_PATH_32;
            String install_path = bit==64?INSTALLED_PATH_64:INSTALLED_PATH_32;
            Process process = Runtime.getRuntime().exec(install_path);
            StreamReader reader = new StreamReader(process.getInputStream());

            reader.start();
            process.waitFor();
            reader.join();

            String result = reader.getResult();
            String result_sw = result.replace(reg_path,"");
//            System.out.printf("Result : %s %d\n",result_sw,bit);
            String[] tmp_list = result_sw.split("[\\r\\n]+");
            for (int i = 0; i < tmp_list.length; i++) {
                String sw = tmp_list[i];
                SWinfo tmp_info = new SWinfo();
                tmp_info.name = getName(install_path + "\\" + sw + "\" /v DisplayName");
                if(tmp_info.name == null) continue;
                tmp_info.version = getName(install_path + "\\" + sw + "\" /v DisplayVersion");
                if(tmp_info.version == null) tmp_info.version = "Unknown version";
                list_sw.add(tmp_info);
//                System.out.println("Add!");
                loading = new String(new char[(int)(100 * ((double)i/ tmp_list.length))]).replace("\0", "#");
                System.out.println(loading);
            }
//            list_sw.addAll(Arrays.asList(tmp_list));
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
        getInstalledList(64);
        getInstalledList(32);
        for(SWinfo installed_sw : list_sw){
            System.out.printf("%s (%s)\n",installed_sw.name,installed_sw.version);
        }
    }
}