/**
 * Created by xilingyuli on 2016/11/6.
 */
public class SymbolTableItem {
    SymbolTableItem(String name, String kind, String type, int addr, Object others)
    {
        this.name = name;
        this.kind = kind;
        this.type = type;
        this.addr = addr;
        this.others = others;
    }
    public String print()
    {
        return name+"\t"+kind+"\t"+type+"\t"+addr+"\n";
    }
    public String name;
    public String kind;
    public String type;
    public int addr;
    public Object others;
}
