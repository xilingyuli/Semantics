import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;

/**
 * Created by xilingyuli on 2016/10/17.
 * 显示代码，代码文件及DFA文件选择
 */
public class CodeView extends JPanel {
    JTextArea code;
    JTextField path;
    JButton button;
    CodeView()
    {
        super();
        code = new JTextArea();
        path = new JTextField();
        button = new JButton("选择源码");
        //选择源码文件
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser(path.getText());
                chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                chooser.showDialog(path,"确定");
                File f = chooser.getSelectedFile();
                if(f!=null) {
                    path.setText(f.getPath());
                    setCode(f);
                }
            }
        });
        initLayout();
    }
    private void initLayout()
    {
        JScrollPane pane = new JScrollPane(code);
        pane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        GridBagLayout layout = new GridBagLayout();
        this.setLayout(layout);
        this.add(pane);
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
    //将代码显示到窗口
    private void setCode(File f)
    {
        try{
            String s = "";
            FileInputStream in = new FileInputStream(f);
            byte[] buffer = new byte[in.available()];
            in.read(buffer);
            in.close();
            s = new String(buffer,"utf-8");
            code.setText(s);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    //获取窗口中的代码
    public String getCode()
    {
        return code.getText();
    }
}
