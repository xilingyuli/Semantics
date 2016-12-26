import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Objects;

/**
 * Created by xilingyuli on 2016/10/17.
 * 主窗口
 */
public class MainFrame extends JFrame {
    JButton button;

    //更改ui样式
    static {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
        }
    }

    MainFrame()
    {
        super("语义分析");
        CodeView codeView = new CodeView();  //代码显示窗口
        DFAView dfaView = new DFAView();  //DFA打印窗口
        SyntaxView syntaxView = new SyntaxView();
        OutputView outputView = new OutputView();  //结果显示窗口
        button = new JButton("分析");
        button.setPreferredSize(new Dimension(1000,30));
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DFA dfa = dfaView.getDFA();  //从文件读取dfa
                LL ll = syntaxView.getSyntax();
                if(dfa!=null&&ll!=null) {
                    //词法部分
                    java.util.List<String[]> tokens = dfa.doNFA(codeView.getCode());
                    String dfaOutput = "";
                    for(String[] s : tokens)
                        dfaOutput += s[0]+":\t"+s[1]+"\t\t"+s[2]+"\n";
                    dfaView.setDFAOutput(dfaOutput);
                    //语法部分
                    Object[] result = ll.getTree(tokens);
                    TreeNode tree = (TreeNode) result[1];
                    String error = (String)result[0];
                    syntaxView.addSyntaxOutput("语法树:\n"+tree.printTree()+"\nError:\n"+error);
                    //语义部分
                    IntermediateCode.analysis(tree);
                    String output = "符号表\n"+IntermediateCode.printSymbolTable()
                            +"\n四元式和三地址码\n"+IntermediateCode.printResult()+"\n"
                            +"Error:\n"+IntermediateCode.printError();
                    outputView.setLabelText(output);
                }
            }
        });
        setLayout(new FlowLayout());
        add(codeView);
        add(dfaView);
        add(syntaxView);
        add(outputView);
        add(button);
        setPreferredSize(new Dimension(1350,600));
        pack();
        setResizable(false);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
    }
}
