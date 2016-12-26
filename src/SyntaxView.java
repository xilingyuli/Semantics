import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * Created by xilingyuli on 2016/10/26.
 */
public class SyntaxView extends JPanel {
    JTextArea view;
    JTextField path;
    JButton button;
    LL ll;
    SyntaxView()
    {
        super();
        view = new JTextArea();
        path = new JTextField();
        button = new JButton("选择文法");
        //选择文法文件
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser(path.getText());
                chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                chooser.showDialog(path,"确定");
                File f = chooser.getSelectedFile();
                if(f!=null) {
                    path.setText(f.getPath());
                    setSyntax(f);
                }
            }
        });
        initLayout();
    }
    private void initLayout()
    {
        JScrollPane pane = new JScrollPane(view);
        pane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        GridBagLayout layout = new GridBagLayout();
        this.setLayout(layout);
        this.add(pane);
        path.setPreferredSize(new Dimension(214,22));
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
        this.setPreferredSize(new Dimension(400, 500));
        this.updateUI();
    }

    public void setSyntax(File f)
    {
        ll = new LL(f);
        view.setText("first集:\n"+ll.printFirst()+"\nfollow集:\n"+ll.printfollow()+"\n预测分析表:\n"+ll.printTable());
    }

    //从文件中读取文法
    public LL getSyntax()
    {
        if(path.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "请选择文法文件");
            return null;
        }
        return ll;
    }

    public void addSyntaxOutput(String output){
        view.setText(view.getText()+"\n"+output);
    }
}
