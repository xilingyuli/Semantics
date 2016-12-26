import com.sun.org.apache.xerces.internal.xs.StringList;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by xilingyuli on 2016/10/17.
 */
public class Token {
    public static int states = 57; //所有的状态数
    public static Set<String> keywords = new HashSet<String>();

    static {
        /*keywords.add("auto");
        keywords.add("double");
        keywords.add("int");
        keywords.add("struct");
        keywords.add("break");
        keywords.add("else");
        keywords.add("long");
        keywords.add("switch");
        keywords.add("case");
        keywords.add("enum");
        keywords.add("register");
        keywords.add("typedef");
        keywords.add("char");
        keywords.add("extern");
        keywords.add("return");
        keywords.add("union");
        keywords.add("const");
        keywords.add("float");
        keywords.add("short");
        keywords.add("unsigned");
        keywords.add("continue");
        keywords.add("for");
        keywords.add("signed");
        keywords.add("void");
        keywords.add("default");
        keywords.add("goto");
        keywords.add("sizeof");
        keywords.add("volatile");
        keywords.add("do");
        keywords.add("if");
        keywords.add("while");
        keywords.add("static");*/
        keywords.add("while");
        keywords.add("do");
        keywords.add("proc");
        keywords.add("int");
        keywords.add("real");
        keywords.add("if");
        keywords.add("then");
        keywords.add("else");
        keywords.add("call");
        keywords.add("record");
        keywords.add("and");
        keywords.add("or");
        keywords.add("not");
        keywords.add("true");
        keywords.add("false");
    }

    //根据状态值返回种别码，若非终态返回空
    public static String getToken(int state)
    {
        switch (state)
        {
            case 2:
            case 5:
                return "reald";
                //return "REAL";  //实数
            case 1:
            case 6:
                //return "DEC";  //十进制
            case 7:
                //return "OCT";  //八进制
            case 9:
                //return "HEX";  //十六进制
                return "intd";
            case 10:
                //return "IDN";
                return "id";
            case 11:
                //return "NOT";
                return "not";
            case 12:
                //return "NE";
                return "!=";
            case 13:
                //return "ASSIGN";
                return "=";
            case 14:
                //return "EQ";
                return "==";
            case 15:
                //return "LSS";
                return "<";
            case 16:
                //return "LEQ";
                return "<=";
            case 17:
                //return "GTR";
                return ">";
            case 18:
                //return "GEQ";
                return ">=";
            case 19:
                //return "PLUS";
                return "+";
            case 20:
                //return "PLUS_ASSIGN";
                return "+=";
            case 21:
                //return "INC";
                return "++";
            case 22:
                //return "MINUS";
                return "-";
            case 23:
                //return "MINUS_ASSIGN";
                return "-=";
            case 24:
                //return "DEC";
                return "--";
            case 25:
                //return "MULTI";
                return "*";
            case 26:
                //return "MULTI_ASSIGN";
                return "*=";
            case 27:
                //return "MOD";
                return "%";
            case 28:
                //return "MOD_ASSIGN";
                return "%=";
            case 29:
                //return "DIV";
                return "/";
            case 30:
                //return "DIV_ASSIGN";
                return "/=";
            case 33:
                return "NOTE";
            case 36:
                return "STRING";
            //case 37:
                //return "BIT_OR";
            case 38:
                //return "OR";
                return "or";
            //case 39:
                //return "BIT_AND";
            case 40:
                //return "AND";
                return "and";
            //case 41:
                //return "BIT_XOR";
            case 42:
                //return "SEMI";
                return ";";
            case 43:
                //return "LS";
                return "[";
            case 44:
                //return "RS";
                return "]";
            case 45:
                //return "SLP";
                return "(";
            case 46:
                //return "SRP";
                return ")";
            case 47:
                //return "LP";
                return "{";
            case 48:
                //return "RP";
                return "}";
            case 49:
                //return "COMMA";
                return ",";
            //case 50:
                //return "HASH";
            //case 51:
                //return "POINT";
            //case 52:
                //return "COLON";
            case 56:
                //return "CHAR";
                return "character";
            default:
                return null;
        }
    }
}
