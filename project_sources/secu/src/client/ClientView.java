package client;

import java.net.InetAddress;
import java.net.UnknownHostException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class ClientView   {

	private JFrame frame ;
    private JLabel lblIpAddress;
    private JButton connectButton;
    private JTextField portTextField;
    private JTextField ipTextField;
    private JLabel portLabel ;
	public ClientView() {
		
        frame = new JFrame("Client de Transfere de fichiers");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);        
        frame.setSize(600,400);        
        frame.getContentPane().setLayout(null);
        
        lblIpAddress = new JLabel("Ip address of the server:");
        lblIpAddress.setBounds(12, 48, 250, 36);
        frame.getContentPane().add(lblIpAddress);
        
        ipTextField = new JTextField();
        ipTextField.setBounds(12, 96, 316, 36);
        
        
        try {
			ipTextField.setText(InetAddress.getLocalHost().getHostAddress());
		} catch (UnknownHostException e) {
			System.out.println("UnknownHost ip is set ");
			ipTextField.setText("127.0.0.1");
		}
        
        frame.getContentPane().add(ipTextField);
       
        portLabel = new JLabel("Port of the server:");
        portLabel.setBounds(450, 48, 150, 36);
        frame.getContentPane().add(portLabel);
        portTextField = new JTextField();
        portTextField.setBounds(450, 96, 50, 36);
        portTextField.setText("9494");
        frame.getContentPane().add(portTextField);
        
        
        connectButton = new JButton("Connect to server");        
        connectButton.setBounds(134, 185, 316, 36);
        frame.getContentPane().add(connectButton); 
        
        frame.setVisible(true);
	}

	
	
	
	//****************Getters et Setters*************************
	
	public JButton getconnectButton() {
		return connectButton;
	}
	public JTextField getPortTextField() {
		return portTextField;
	}
	public JTextField getIpTextField() {
		return ipTextField;
	}
	
	
	
	
	
	
}
