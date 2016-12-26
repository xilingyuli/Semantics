import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;

/**
 * Created by xilingyuli on 2016/10/25.
 */
public class LL {
    String[] terminator = new String[]{"id","intd","reald","proc","int","real","if","then","else","call","record","while","do",
            "=","(",")","[","]","+","-","*","<","<=","==","!=",">=",">","and","or","not","true","false",",",";"};
    Map<String,Set<String>> original,first,follow;
    Map<String,Map<String,String[]>> table;
    LL(File file)
    {
        original = new HashMap<String,Set<String>>();
        first = new HashMap<String,Set<String>>();
        follow = new HashMap<String,Set<String>>();
        table = new HashMap<String,Map<String,String[]>>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine())!=null)
            {
                if(!line.contains("->"))
                    continue;
                String key = line.substring(0, line.indexOf("->")).trim();
                String[] value = line.substring(line.indexOf("->")+2).split("\\u007C");
                Set<String> valueList = new LinkedHashSet<String>();
                for(String v : value)
                    valueList.add(v.trim());
                original.put(key, valueList);
            }
            getFirst();
            getFollow();
            getTable();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    private void getFirst()
    {
            first.put("ε", new HashSet<String>());
            first.get("ε").add("ε");
            //所有终结符first集为自身
            for (String x : terminator) {
                Set<String> value = new HashSet<String>();
                value.add(x);
                first.put(x, value);
            }
            //X->ε则first集含ε
            for (String x : original.keySet()) {
                Set<String> value = new HashSet<String>();
                if (hasEmpty(original.get(x))) {
                    value.add("ε");
                }
                first.put(x, value);
            }
            boolean change;
            do {
                change = false;
                for (String x : original.keySet()) {
                    //非终结符
                    if (!isTerminator(x)) {
                        for (String str : original.get(x)) {
                            String[] temp = str.split(" ");
                            //X->Y1Y2...找到首个不含ε的Yi
                            int i = 0;
                            while (i < temp.length && (temp[i].equals("ε") || hasEmpty(original.get(temp[i]))))
                                i++;
                            //添加前的大小
                            int count = first.get(x).size();
                            //X的first集
                            Set<String> value = first.get(x);
                                if (i < temp.length)  //存在Yi
                                {
                                    //X的first集添加Yi及Yi前的每个Yj的first集
                                    for (int j = 0; j <= i; j++) {
                                        for (String t : first.get(temp[j]))
                                            if (!t.equals("ε"))
                                                value.add(t);
                                    }
                                } else  //不存在Yi则添加空
                                {
                                    value.add("ε");
                                }
                                if (value.size() != count)
                                    change = true;
                        }
                    }
                }
            } while (change);
    }
    private void getFollow()
    {
        //所有终结符follow集为空
        for(String x : terminator)
            follow.put(x, new HashSet<String>());
        for(String x : original.keySet())
            follow.put(x, new HashSet<String>());
        follow.get("P").add("#");
        boolean change;
        do{
            change = false;
            for(String x : original.keySet())
            {
                //非终结符
                if(!isTerminator(x))
                {
                    for(String str : original.get(x))
                    {
                        //每一条文法
                        String[] temp = str.split(" ");
                        //first集加入follow中
                        for(int i=0;i<temp.length-1;i++)
                        {
                            if(!temp[i].equals("ε")&&!isTerminator(temp[i]))
                            {
                                Set<String> tempSet = follow.get(temp[i]);
                                int c = tempSet.size();
                                tempSet.addAll(first.get(temp[i+1]));
                                tempSet.remove("ε");
                                if(tempSet.size()!=c)
                                    change = true;
                            }
                        }
                        //follow集加入follow中
                        for(int i=temp.length-1;i>=0;i--)
                        {
                            if(i+1>=temp.length||hasEmpty(first.get(temp[i+1])))
                            {
                                if(temp[i].equals("ε"))
                                    continue;
                                if(isTerminator(temp[i]))
                                    break;
                                Set<String> tempSet = follow.get(temp[i]);
                                int c = tempSet.size();
                                tempSet.addAll(follow.get(x));
                                if(tempSet.size()!=c)
                                    change = true;
                            }
                            else
                                break;
                        }
                    }
                }
            }
        }while (change);
    }
    private void getTable()
    {
        for(String x : original.keySet())
        {
            //非终结符
            if(!isTerminator(x))
            {
                table.put(x, new HashMap<String, String[]>());
                Map<String, String[]> tempMap = table.get(x);
                //空数组算作同步点
                for(String t : follow.get(x))
                    tempMap.put(t,new String[0]);
                //分析每个语法变量
                for(String str : original.get(x))
                {
                    //每一条文法
                    String[] temp = str.split(" ");
                    if(hasEmpty(first.get(temp[0])))
                    {
                        for(String t : follow.get(x))
                            tempMap.put(t,temp);
                    }
                    for(String t : first.get(temp[0]))
                        tempMap.put(t,temp);
                    tempMap.remove("ε");
                }
            }
        }
    }
    public boolean hasEmpty(Collection<String> collection)
    {
        if(collection==null)
            return false;
        for(String s : collection)
        {
            if(s.equals("ε"))
                return true;
        }
        return false;
    }
    public boolean isTerminator(String s)
    {
        switch (s)
        {
            case "id":
            case "intd":
            case "reald":
            case "proc":
            case "int":
            case "real":
            case "if":
            case "then":
            case "else":
            case "call":
            case "record":
            case "while":
            case "do":
            case "=":
            case "(":
            case ")":
            case "[":
            case "]":
            case "+":
            case "-":
            case "*":
            case "<":
            case "<=":
            case "==":
            case "!=":
            case ">=":
            case ">":
            case "and":
            case "or":
            case "not":
            case "true":
            case "false":
            case ",":
            case ";":
                return true;
        }
        return false;
    }
    public Object[] getTree(List<String[]> data)
    {
        data.add(new String[]{"","#",""});
        String error = "";
        TreeNode tree = new TreeNode(null, new String[]{"","P",""});
        Stack<TreeNode> stack = new Stack<TreeNode>();
        stack.push(tree);
        int index = 0;
        while (!stack.empty()&&index<data.size())
        {
            //从输入读取字符
            String ch = data.get(index)[1];
            if(ch.equals("词法错误")||ch.equals("非法字符"))
            {
                error += "Error at Line "+data.get(index)[0]+"："+data.get(index)[1]+"，"+data.get(index)[2]+"\n";
                index++;
                continue;
            }

            TreeNode top = stack.peek();
            //栈顶字符与输入相同，规约
            if(top.getChar().equals(ch))
            {
                top.info = data.get(index);
                stack.pop();
                index++;
                continue;
            }

            //出现额外字符
            if(!table.containsKey(top.getChar()))
            {
                if(ch.equals("intd")||ch.equals("reald")||ch.equals("id")||ch.equals("STRING"))
                    ch = data.get(index)[2];
                error += "Error at Line "+data.get(index)[0]+"：语法错误，跳过"+ch+"\n";
                index++;
                continue;
            }
            String[] rule = table.get(top.getChar()).get(ch);
            if(rule!=null)  //无错误
            {
                if(rule.length!=0)  //不是同步点
                {
                    stack.pop();
                    for(int i=rule.length-1;i>=0;i--)
                    {
                        TreeNode node = new TreeNode(top,new String[]{"",rule[i],""});
                        top.addChild(node);
                        if(!node.getChar().equals("ε"))
                            stack.push(node);
                    }
                }
                else  //是同步点，删去栈顶元素
                {
                    error += "Error at Line "+data.get(index)[0]+"：语法错误，M["+top.getChar()+","+ch+"]=synch\n";
                    stack.pop();
                }
            }
            else  //有错误
            {
                if(ch.equals("intd")||ch.equals("reald")||ch.equals("id")||ch.equals("STRING"))
                    ch = data.get(index)[2];
                error += "Error at Line "+data.get(index)[0]+"：语法错误，跳过"+ch+"\n";
                index++;
            }
        }
        return new Object[]{error,tree};
    }
    public String printFirst()
    {
        StringBuilder sb = new StringBuilder();
        for(String str : first.keySet())
        {
            sb.append(str).append(":\t").append(first.get(str).toString()).append("\n");
        }
        return sb.append("\n").toString();
    }
    public String printfollow()
    {
        StringBuilder sb = new StringBuilder();
        for(String str : follow.keySet())
        {
            sb.append(str).append(":\t").append(follow.get(str).toString()).append("\n");
        }
        return sb.append("\n").toString();
    }
    public String printTable()
    {
        StringBuilder sb = new StringBuilder();
        for(String str : terminator)
            sb.append("\t\t\t").append(str);
        sb.append("\n");
        for(String str : table.keySet())
        {
            sb.append(str).append("\t\t\t");
            for(String str2 : terminator) {
                if(table.get(str).get(str2)==null)
                    sb.append("error").append("\t\t\t");
                else if(table.get(str).get(str2).length==0)
                    sb.append("synch").append("\t\t\t");
                else {
                    String re = "";
                    for(String it : table.get(str).get(str2))
                        re += it+" ";
                    re = str+"->"+re.trim();
                    sb.append(re).append("\t");
                    if(re.length()<16)
                        sb.append("\t");
                    if(re.length()<8)
                        sb.append("\t");
                }
            }
            sb.append("\n");
        }
        return sb.append("\n").toString();
    }
}
