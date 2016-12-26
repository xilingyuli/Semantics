import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by xilingyuli on 2016/11/6.
 */
public class SymbolTable {

    public String name;
    public SymbolTable parent;
    public Map<String,SymbolTableItem> table;

    SymbolTable(String name, SymbolTable parent)
    {
        this.name = name;
        this.parent = parent;
        table = new LinkedHashMap<>();
    }

    public SymbolTableItem getSymbol(String symbol)
    {
        SymbolTable temp = this;
        while (temp!=null){
            if(temp.table.containsKey(symbol))
                return temp.table.get(symbol);
            temp = temp.parent;
        }
        return null;
    }
}
