import java.io.*;
import java.util.*;

/**
 * Created by xilingyuli on 2016/10/17.
 */
public class DFA {
    public Map<Character,Integer> map;  //各个输入字符在DFA文件第一行中的下标
    public int[][] table;  //DFA表
    //从文件中读取DFA表
    DFA(String file)
    {
        try{
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String firstLine = reader.readLine();
            String[] arr = firstLine.trim().split("\\s+");
            map = new HashMap<Character,Integer>();
            for(int i=0;i<arr.length;i++)
            {
                map.put(arr[i].charAt(0),i);
            }
            table = new int[Token.states][arr.length];
            for(int i=0;i<Token.states;i++)
            {
                String line = reader.readLine();
                String[] items = line.trim().split("\\s+");
                for(int j=0;j<arr.length;j++)
                {
                    table[i][j] = Integer.parseInt(items[j]);
                }
            }
            reader.close();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    //利用DFA表分析代码，生成token序列
    public List<String[]> doNFA(String code)
    {
        List<String[]> tokens = new ArrayList<String[]>();
        int state = 0;  //当前状态
        int lastFinalState = 0;  //上一个终态，用于错误处理
        int finalStateIndex = 0;  //产生上个终态时的字符位置
        int beginIndex = 0;  //单词起始字符位置

        for(int i=0;;i++) {
            //结尾处处理
            if(i>=code.length())
            {
                //一个单词结束
                if(lastFinalState!=0) {  //之前输入存在合法终态，回退
                    tokens.add(getTokenDiscrible(code,beginIndex,finalStateIndex+1,lastFinalState));
                    state = 0;
                    i = finalStateIndex;  //从上个终态处开始处理
                    lastFinalState = 0;  //重置上个终态的值
                    beginIndex = i+1;
                    if(beginIndex>=code.length())
                        break;
                }else {  //之前输出不存在合法终态，结束
                    if(!code.substring(beginIndex,i).matches("\\s+")&&beginIndex<i)  //出错，而不是跳过空白字符
                        tokens.add(getTokenDiscrible(code,beginIndex,i,-1));
                    break;
                }
                continue;
            }
            char c = code.charAt(i);
            if(map.containsKey(c)){  //c是合法非空白字符
                if(table[state][map.get(c)]==0){  //下一个状态为空
                    if(lastFinalState!=0) {  //之前输入存在合法终态
                        tokens.add(getTokenDiscrible(code,beginIndex,finalStateIndex+1,lastFinalState));
                        state = 0;
                        i = finalStateIndex;  //从上个终态处开始处理
                        lastFinalState = 0;  //重置上个终态的值
                        beginIndex = i+1;
                    }else {  //之前输入不存在合法终态，进行错误处理
                        while (i<code.length()&&
                                (!map.containsKey(code.charAt(i))||table[0][map.get(code.charAt(i))]==0))  //不正确的字符
                            i++;  //跳过
                        tokens.add(getTokenDiscrible(code,beginIndex,i,-1));
                        state = 0;
                        lastFinalState = 0;
                        beginIndex = i;
                        i--;
                    }
                }else  //下一个状态非空
                {
                    state = table[state][map.get(c)];  //转移到下一个状态
                    if(Token.getToken(state)!=null)  //是一个终态
                    {
                        lastFinalState = state;
                        finalStateIndex = i;
                    }
                }
            }else if(Character.isWhitespace(c))  //c是空白字符
            {
                if(state==34||state==35||state==31||state==32)  //字符串和注释中的空白字符不做截断处理
                    continue;

                //一个单词结束
                if(lastFinalState!=0) {  //之前输入存在合法终态
                    tokens.add(getTokenDiscrible(code,beginIndex,finalStateIndex+1,lastFinalState));
                    state = 0;
                    i = finalStateIndex;  //从上个终态处开始处理
                    lastFinalState = 0;  //重置上个终态的值
                    beginIndex = i+1;
                }else {  //之前输出不存在合法终态
                    while (i<code.length()
                            &&(!map.containsKey(code.charAt(i))||table[0][map.get(code.charAt(i))]==0))  //空白字符或不正确的字符
                        i++;  //跳过
                    if(!code.substring(beginIndex,i).matches("\\s+")&&beginIndex<i)  //出错，而不是跳过空白字符
                        tokens.add(getTokenDiscrible(code,beginIndex,i,-1));
                    state = 0;
                    lastFinalState = 0;
                    beginIndex = i;
                    i--;
                }
            }else  //c是非法字符
            {
                if(state!=34&&state!=35&&state!=31&&state!=32)  //c不在字符串和注释中
                    tokens.add(getTokenDiscrible(code,i,i+1,-2));  //非法字符
            }
        }
        return tokens;
    }

    private String[] getTokenDiscrible(String code, int beginIndex, int finalIndex, int state)
    {
        String token = Token.getToken(state);  //种别码
        String text = code.substring(beginIndex,finalIndex);
        String str[] = new String[3];
        int line = 1;
        for(int i=0;i<beginIndex;i++)
            if(code.charAt(i)=='\n')
                line++;
        str[0] = line+"";
        if(state==-1)
        {
            str[1] = "词法错误";
            str[2] = text;
            return str;
        }
        if(state==-2)
        {
            str[1] = "非法字符";
            str[2] = text;
            return str;
        }
        if(token.equals("id")&&Token.keywords.contains(text))  //是关键字而不是标识符
            token = text;
        str[1] = token;
        if(token.equals("intd")||token.equals("reald")||token.equals("id")||token.equals("STRING"))
            str[2] = text;
        else
            str[2] = "";
        return str;
    }
}
