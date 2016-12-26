import javax.swing.*;
import java.awt.*;

/**
 * Created by xilingyuli on 2016/10/17.
 * 分析结果显示窗口
 */
public class OutputView extends JPanel {
    JTextArea label;
    OutputView()
    {
        label = new JTextArea();
        JScrollPane pane = new JScrollPane(label);
        setLayout(new BorderLayout());
        add(pane, "Center");
        setPreferredSize(new Dimension(300, 500));
        updateUI();
    }
    public void setLabelText(String text)
    {
        label.setText(text);
    }
}
