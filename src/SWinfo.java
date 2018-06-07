public class SWinfo {

    public String name, version, insPath, reference, details, template;

    public SWinfo(String n, String v){
        name = n;
        version = v;
    }

    public SWinfo(String n, String v, String in, String ref, String dt){
        name = n;
        version = v;
        insPath = in;
        reference = ref;
        details = dt;
//        template = tmp;
    }
}
