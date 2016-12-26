import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 * Created by xilingyuli on 2016/10/19.
 * 显示DFA表
 */
public class DFAView extends JPanel {
    JTextArea dfa;
    JTextField path;
    JButton button;
    DFAView()
    {
        super();
        dfa = new JTextArea();
        path = new JTextField();
        button = new JButton("选择DFA");
        //选择DFA文件
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser(path.getText());
                chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                chooser.showDialog(path,"确定");
                File f = chooser.getSelectedFile();
                if(f!=null) {
                    path.setText(f.getPath());
                    //setDFA(f);
                }
            }
        });
        initLayout();
    }
    private void initLayout()
    {
        JScrollPane pane = new JScrollPane(dfa);
        pane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        GridBagLayout layout = new GridBagLayout();
        this.setLayout(layout);
        this.add(pane);
        path.setPreferredSize(new Dimension(220,22));
        this.add(path);
        this.add(button);
        GridBagConstraints s = new GridBagConstraints();
        s.fill = GridBagConstraints.BOTH;
        s.gridx = 0;
        s.gridy = 0;
        s.gridwidth = 5;
        s.gridheight = 4;
        s.weightx = 1;
        s.weighty = 1;
        layout.setConstraints(pane, s);
        s.gridy = 4;
        s.gridwidth = 4;
        s.gridheight = 1;
        s.weighty = 0;
        layout.addLayoutComponent(path, s);
        s.gridx = 4;
        s.gridwidth = 1;
        s.weightx = 0;
        layout.addLayoutComponent(button, s);
        this.setPreferredSize(new Dimension(300, 500));
        this.updateUI();
    }

    public void setDFA(File f)
    {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(f));
            StringBuilder sb = new StringBuilder("   "+reader.readLine()+"\n");
            for(int i=0;i<Token.states;i++)
            {
                if(i<10)
                    sb.append(" "+i+" "+reader.readLine()+"\n");
                else
                    sb.append(""+i+" "+reader.readLine()+"\n");
            }
            dfa.setText(sb.toString());
            reader.close();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void setDFAOutput(String output)
    {
        dfa.setText(output);
    }

    //从文件中读取DFA表
    public DFA getDFA()
    {
        if(path.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "请选择dfa文件");
            return null;
        }
        return new DFA(path.getText());
    }
}
