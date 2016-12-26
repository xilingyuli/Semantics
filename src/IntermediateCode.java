import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by xilingyuli on 2016/11/6.
 */
public class IntermediateCode {
    public static ArrayList<String> error = new ArrayList<String>();
    public static ArrayList<String> result = new ArrayList<String>();
    public static ArrayList<String[]> result2 = new ArrayList<String[]>();
    public static SymbolTable global;
    public static int offset,index;
    public static String printSymbolTable()
    {
        String str = "";
        LinkedList<SymbolTable> list = new LinkedList<SymbolTable>();
        list.add(global);
        while (!list.isEmpty()){
            SymbolTable table = list.pop();
            str += table.name+":\n";
            for(SymbolTableItem item : table.table.values())
            {
                if(item.others instanceof SymbolTable)
                    list.add((SymbolTable) item.others);
                str += item.print();
            }
            str += "\n";
        }
        return str;
    }
    public static String printResult()
    {
        String str = "";
        for(int i=0;i<result.size();i++)
        {
            str += i+":\t"+ Arrays.toString(result2.get(i))+"\t\t"+result.get(i)+"\n";
        }
        return str;
    }
    public static String printError()
    {
        String str = "";
        for(String e : error)
            str += e+"\n";
        return str;
    }
    public static void analysis(TreeNode tree)
    {
        error.clear();
        result.clear();
        result2.clear();
        offset = 0;
        index = 0;
        global = new SymbolTable("global",null);
        if(tree.children.get(0).getChar().equals("D"))
            makeD(tree.children.get(0),global);
        else
            makeS(tree.children.get(0),global);
        result.add("");
        result2.add(new String[4]);
        System.out.println();
    }
    public static void makeD(TreeNode tree, SymbolTable table)
    {
        //D -> ε
        if(tree.info[0].isEmpty())
            return;

        //D -> proc id ( Plist ) ; D' S' D'
        if(tree.children.get(0).getChar().equals("proc"))
        {
            //建立新符号表
            String name = tree.children.get(1).info[2];
            SymbolTable subTable = new SymbolTable(name, table);
            if(table.table.containsKey(name)) {
                error.add("Error at Line "+tree.info[0]+"：重复的函数声明" + name);
            }
            table.table.put(name, new SymbolTableItem(name, "函数", null, offset, subTable));
            makePlist(tree.children.get(3),subTable);
            makeD(tree.children.get(6), subTable);
            makeS(tree.children.get(7),subTable);
            makeD(tree.children.get(8),table);
        }
        //D' -> D D'
        else if(tree.children.get(0).getChar().equals("D"))
        {
            makeD(tree.children.get(0),table);
            makeD(tree.children.get(1),table);
        }
        //D -> T id ; D'
        else if(tree.children.get(0).getChar().equals("T"))
        {
            makeT(tree.children.get(0),table,tree.children.get(1).info[2]);
            makeD(tree.children.get(3),table);
        }

    }
    public static void makePlist(TreeNode tree, SymbolTable table)
    {
        //Plist -> ε Plist' -> ε
        if(tree.info[0].isEmpty())
            return;
        //Plist -> T id Plist'
        if(tree.children.get(0).getChar().equals("T"))
        {
            makeT(tree.children.get(0), table, tree.children.get(1).info[2]);
            makePlist(tree.children.get(2),table);
        }
        //Plist' -> , T id Plist'
        else if(tree.children.get(0).getChar().equals(","))
        {
            makeT(tree.children.get(1), table, tree.children.get(2).info[2]);
            makePlist(tree.children.get(3),table);
        }
    }
    public static void makeT(TreeNode tree, SymbolTable table, String id)
    {
        //T -> record D
        if(tree.children.get(0).getChar().equals("record"))
        {
            SymbolTable subTable = new SymbolTable(id, null);
            int tempOffset = offset;
            makeD(tree.children.get(1), subTable);  //添加所有记录项
            if(table.table.containsKey(id)) {
                error.add("Error at Line "+tree.info[0]+"：重复的记录声明" + id);
            }
            table.table.put(id,new SymbolTableItem(id,"记录","record", tempOffset, subTable));
        }
        //T -> X C
        else if(tree.children.get(0).getChar().equals("X"))
        {
            if(!tree.children.get(1).info[0].isEmpty())  //C不取空，是数组
            {
                if(table.table.containsKey(id)) {
                    error.add("Error at Line "+tree.info[0]+"：重复的数组声明" + id);
                }
                int[] level = makeC(tree.children.get(1), new int[0]);
                String type = tree.children.get(0).children.get(0).getChar();
                table.table.put(id, new SymbolTableItem(id, "数组", type, offset, level));
                //计算偏移
                int total;
                if(type.equals("int"))
                    total = 4;
                else
                    total = 8;
                for(int i : level)
                    total *= i;
                offset+=total;
            }
            else
            {
                if(table.table.containsKey(id)) {
                    error.add("Error at Line "+tree.info[0]+"：重复的变量声明" + id);
                }
                String type = tree.children.get(0).children.get(0).getChar();
                table.table.put(id, new SymbolTableItem(id, "变量", type, offset, null));
                if(type.equals("int"))
                    offset += 4;
                else
                    offset += 8;
            }
        }
    }
    public static int[] makeC(TreeNode tree, int[] level)
    {
        //C -> ε
        if(tree.info[0].isEmpty())
            return level;
        //C -> [ intdigit ] C
        int[] newLevel = new int[level.length+1];
        for(int i=0;i<level.length;i++)
            newLevel[i] = level[i];
        newLevel[newLevel.length-1] = Integer.parseInt(tree.children.get(1).info[2]);
        return makeC(tree.children.get(3),newLevel);
    }
    public static void makeS(TreeNode tree, SymbolTable table)
    {
        //S' -> ε
        if(tree.info[0].isEmpty())
            return;
        //S' -> S S'
        if(tree.children.get(0).getChar().equals("S"))
        {
            makeS(tree.children.get(0),table);
            makeS(tree.children.get(1),table);
        }
        //S -> L = E ;
        else if(tree.children.get(0).getChar().equals("L")){
            String[] l = makeL(tree.children.get(0),table);
            String[] e = makeE(tree.children.get(2),table);
            if(l[1].equals("int")&&e[1].equals("real"))
            {
                error.add("Error at Line "+tree.info[0]+"：缺少强制类型转换");
            }
            result.add(l[0]+" = "+e[0]);
            result2.add(new String[]{"=",e[0],"-",l[0]});
        }
        //S -> call id ( Elist ) ;
        else if(tree.children.get(0).getChar().equals("call")){
            String id = tree.children.get(1).info[2];
            SymbolTableItem item = table.getSymbol(id);
            if(item==null||!item.kind.equals("函数"))
            {
                error.add("Error at Line "+tree.info[0]+"：未声明的函数"+id);
                return;
            }
            String[] elist = makeElist(tree.children.get(3),table,new String[0]);
            for (String s : elist) {
                result.add("param " + s);
                result2.add(new String[]{"param",s,"-","-"});
            }
            result.add("call "+id+","+elist.length);
            result2.add(new String[]{"call",id,elist.length+"","-"});
        }
        //if B then S EL
        else if(tree.children.get(0).getChar().equals("if")){
            TFList tfList = makeB(tree.children.get(1),table);
            int m1 = result.size();
            makeS(tree.children.get(3),table);
            //EL -> ε
            if(tree.children.get(4).info[0].isEmpty())
            {
                int m2 = result.size();
                for(int t : tfList.truelist) {
                    result.set(t, result.get(t).replace("_", m1 + ""));
                    result2.get(t)[3] = m1+"";
                }
                for(int f : tfList.falselist) {
                    result.set(f, result.get(f).replace("_", m2 + ""));
                    result2.get(f)[3] = m2+"";
                }
            }
            //EL -> else S
            else {
                int temp = result.size();
                result.add("goto _");
                result2.add(new String[]{"j","-","-","_"});
                int m2 = result.size();
                makeS(tree.children.get(4).children.get(1),table);
                int end = result.size();
                for(int t : tfList.truelist) {
                    result.set(t, result.get(t).replace("_", m1 + ""));
                    result2.get(t)[3] = m1+"";
                }
                for(int f : tfList.falselist) {
                    result.set(f, result.get(f).replace("_", m2 + ""));
                    result2.get(f)[3] = m2+"";
                }
                result.set(temp,result.get(temp).replace("_",end+""));
                result2.get(temp)[3] = end+"";
            }
        }
        //S -> do S while B
        else if(tree.children.get(0).getChar().equals("do")){
            int begin = result.size();
            makeS(tree.children.get(1),table);
            TFList tfList = makeB(tree.children.get(3),table);
            int end = result.size();
            for(int t : tfList.truelist) {
                result.set(t, result.get(t).replace("_", begin + ""));
                result2.get(t)[3] = begin+"";
            }
            for(int f : tfList.falselist) {
                result.set(f, result.get(f).replace("_", end + ""));
                result2.get(f)[3] = end+"";
            }
        }
    }
    public static String[] makeElist(TreeNode tree, SymbolTable table, String[] input)
    {
        //Elist' -> , E Elist'
        if(tree.children.get(0).getChar().equals(","))
        {
            String[] output = new String[input.length+1];
            for(int i=0;i<input.length;i++)
                output[i] = input[i];
            output[output.length-1] = makeE(tree.children.get(1),table)[0];
            return makeElist(tree.children.get(2),table,output);
        }
        //Elist -> E Elist'
        else if(tree.children.get(0).getChar().equals("E"))
        {
            String[] output = new String[input.length+1];
            for(int i=0;i<input.length;i++)
                output[i] = input[i];
            output[output.length-1] = makeE(tree.children.get(0),table)[0];
            return makeElist(tree.children.get(1),table,output);
        }
        //Elist' -> ε Elist -> ε
        return input;
    }
    public static String[] makeL(TreeNode tree, SymbolTable table)
    {
        //L -> id L'
        if(tree.children.get(0).getChar().equals("id")) {
            SymbolTableItem id = table.getSymbol(tree.children.get(0).info[2]);
            if(id==null)
            {
                error.add("Error at Line "+tree.info[0]+"：未声明的变量"+tree.children.get(0).info[2]);
                return new String[]{"0","int"};
            }

            String[] idLevel = makeL_(tree.children.get(1),table,new String[0]);
            if(id.kind.equals("变量"))
            {
                if(idLevel.length!=0)
                    error.add("Error at Line "+tree.info[0]+"："+id.name+"不是数组类型");
                return new String[]{id.name, id.type};
            }
            else if(id.kind.equals("数组"))
            {
                int[] arrayLevel = (int[])id.others;
                if(idLevel.length==0) {
                    error.add("Error at Line "+tree.info[0]+"："+id.name + "不是变量类型");
                    return new String[]{id.name + "[0]", id.type};
                }else if(arrayLevel.length!=idLevel.length) {
                    error.add("Error at Line "+tree.info[0]+"：数组"+id.name+"长度不匹配");
                }

                int total = 1;
                for(int i : arrayLevel)
                    total *= i;
                String t = "";
                for(int i=0;i<arrayLevel.length&&i<idLevel.length;i++)
                {
                    String temp1 = "t"+index;
                    index++;
                    result.add(temp1+" = "+idLevel[i]);
                    result2.add(new String[]{"=",idLevel[i],"-",temp1});
                    String temp2 = "t"+index;
                    index++;
                    String temp3 = "t"+index;
                    index++;
                    result.add(temp2+" = "+temp1+" * "+total);
                    result2.add(new String[]{"*",temp1,total+"",temp2});
                    if(!t.isEmpty()) {
                        result.add(temp3 + " = " + t + " + " + temp2);
                        result2.add(new String[]{"+",t,temp2,temp3});
                    }
                    t = temp3;
                    total /= arrayLevel[1];
                }
                return new String[]{id.name+"["+t+"]", id.type};
            }
            else
            {
                error.add("Error at Line "+tree.info[0]+"："+id.name+"不是数组或变量类型");
            }
        }
        return new String[]{"0","int"};
    }
    public static String[] makeL_(TreeNode tree, SymbolTable table, String[] level)
    {
        //L' -> [ E ] L'
        if(tree.children.get(0).getChar().equals("[")) {
            String[] e = makeE(tree.children.get(1),table);
            if(!e[1].equals("int"))
            {
                error.add("Error at Line "+tree.info[0]+"：数组第"+level.length+"维下标出现非整数值");
                e[0] = "0";
            }
            String[] newLevel = new String[level.length+1];
            for(int i=0;i<level.length;i++)
                newLevel[i] = level[i];
            newLevel[newLevel.length-1] = e[0];
            return makeL_(tree.children.get(3), table, newLevel);
        }
        //L' -> ε
        return level;
    }
    public static String[] makeE(TreeNode tree, SymbolTable table)
    {
        //E -> F E'
        if(tree.children.get(0).getChar().equals("F")){
            return makeE_(tree.children.get(1),table,makeF(tree.children.get(0),table));
        }
        return new String[]{"0","int"};
    }
    public static String[] makeE_(TreeNode tree, SymbolTable table, String[] input)
    {
        //E' -> + F E'
        if(tree.children.get(0).getChar().equals("+")){
            String t = "t"+index;
            index++;
            String[] g = makeF(tree.children.get(1),table);
            String type = input[1].equals("real")||g[1].equals("real")?"real":"int";
            result.add(t+" = "+input[0]+" + "+g[0]);
            result2.add(new String[]{"+",input[0],g[0],t});
            return makeE_(tree.children.get(2),table,new String[]{t,type});
        }
        //E' -> ε
        return input;
    }
    public static String[] makeF(TreeNode tree, SymbolTable table)
    {
        //F -> G F'
        if(tree.children.get(0).getChar().equals("G")){
            return makeF_(tree.children.get(1),table,makeG(tree.children.get(0),table));
        }
        return new String[]{"0","int"};
    }
    public static String[] makeF_(TreeNode tree, SymbolTable table, String[] input)
    {
        //F' -> * G F'
        if(tree.children.get(0).getChar().equals("*")){
            String t = "t"+index;
            index++;
            String[] g = makeG(tree.children.get(1),table);
            String type = input[1].equals("real")||g[1].equals("real")?"real":"int";
            result.add(t+" = "+input[0]+" * "+g[0]);
            result2.add(new String[]{"*",input[0],g[0],t});
            return makeF_(tree.children.get(2),table,new String[]{t,type});
        }
        //F' -> ε
        return input;
    }
    public static String[] makeG(TreeNode tree, SymbolTable table)
    {
        //G -> intdigit
        if(tree.children.get(0).getChar().equals("intd")) {
            return new String[]{tree.children.get(0).info[2], "int"};
        }
        else if(tree.children.get(0).getChar().equals("reald")) {
            return new String[]{tree.children.get(0).info[2], "real"};
        }
        //G -> L
        else if(tree.children.get(0).getChar().equals("L")){
            return makeL(tree.children.get(0),table);
        }
        //G -> ( E )
        else if(tree.children.get(0).getChar().equals("(")){
            return makeE(tree.children.get(1),table);
        }
        //G -> - G
        else if(tree.children.get(0).getChar().equals("-")){
            String t = "t"+index;
            index++;
            String[] g = makeG(tree.children.get(0),table);
            result.add(t+" = -"+g[0]);
            result2.add(new String[]{"-","0",g[0],t});
            return new String[]{t, g[1]};
        }
        return new String[]{"0","int"};
    }
    public static TFList makeB(TreeNode tree, SymbolTable table)
    {
        //B -> E OP E B'
        if(tree.children.get(0).getChar().equals("E"))
        {
            List<Integer> truelist = new ArrayList<Integer>();
            List<Integer> falselist = new ArrayList<Integer>();
            String e1 = makeE(tree.children.get(0),table)[0];
            String e2 = makeE(tree.children.get(2),table)[0];
            truelist.add(result.size());
            String ch = tree.children.get(1).children.get(0).getChar();
            result.add("if "+ e1 +" "+ch+" " + e2 + " goto _");
            result2.add(new String[]{"j"+ch,e1,e2,"_"});
            falselist.add(result.size());
            result.add("goto _");
            result2.add(new String[]{"j","-","-","_"});
            return makeB_(tree.children.get(3),table,new TFList(truelist, falselist));
        }
        //B -> not B B'
        else if(tree.children.get(0).getChar().equals("not"))
        {
            TFList temp = makeB(tree.children.get(1), table);
            return makeB_(tree.children.get(2), table, new TFList(temp.falselist, temp.truelist));
        }
        //B -> ( B ) B'
        else if(tree.children.get(0).getChar().equals("("))
        {
            TFList temp = makeB(tree.children.get(1), table);
            return makeB_(tree.children.get(3), table, temp);
        }
        //B -> true B'
        else if(tree.children.get(0).getChar().equals("true"))
        {
            List<Integer> truelist = new ArrayList<Integer>();
            List<Integer> falselist = new ArrayList<Integer>();
            truelist.add(result.size());
            result.add("goto _");
            result2.add(new String[]{"j","-","-","_"});
            return makeB_(tree.children.get(1), table, new TFList(truelist,falselist));
        }
        //B -> false B'
        else if(tree.children.get(0).getChar().equals("false"))
        {
            List<Integer> truelist = new ArrayList<Integer>();
            List<Integer> falselist = new ArrayList<Integer>();
            falselist.add(result.size());
            result.add("goto _");
            result2.add(new String[]{"j","-","-","_"});
            return makeB_(tree.children.get(1), table, new TFList(truelist,falselist));
        }
        return new TFList(null,null);
    }
    public static TFList makeB_(TreeNode tree, SymbolTable table, TFList input){
        //B -> and B B'
        if(tree.children.get(0).getChar().equals("and"))
        {
            int m = result.size();
            for(int t : input.truelist) {
                result.set(t, result.get(t).replace("_", m + ""));
                result2.get(t)[3] = m+"";
            }
            TFList temp2 = makeB(tree.children.get(1),table);
            List<Integer> falselist = new ArrayList<Integer>();
            falselist.addAll(input.falselist);
            falselist.addAll(temp2.falselist);
            TFList temp = new TFList(temp2.truelist,falselist);
            return makeB_(tree.children.get(2),table,temp);
        }
        //B -> or B B'
        else if(tree.children.get(0).getChar().equals("or"))
        {
            int m = result.size();
            for(int t : input.falselist) {
                result.set(t, result.get(t).replace("_", m + ""));
                result2.get(t)[3] = m+"";
            }
            TFList temp2 = makeB(tree.children.get(1),table);
            List<Integer> truelist = new ArrayList<Integer>();
            truelist.addAll(input.truelist);
            truelist.addAll(temp2.truelist);
            TFList temp = new TFList(truelist,temp2.falselist);
            return makeB_(tree.children.get(2),table,temp);
        }
        //B' -> ε
        return input;
    }
    public static class TFList
    {
        public List<Integer> truelist, falselist;
        TFList(List<Integer> truelist, List<Integer> falselist)
        {
            this.truelist = truelist;
            this.falselist = falselist;
        }
    }
}
