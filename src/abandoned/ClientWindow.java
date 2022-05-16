package abandoned;

import javax.swing.*;
import java.awt.*;

public class ClientWindow extends JFrame {
    public ClientWindow() {
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        super.setTitle("Client Socket");
        super.setLayout(null);
        super.setResizable(false);
        super.setBounds(0,0,screen.width/4,screen.height/4);
        super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
