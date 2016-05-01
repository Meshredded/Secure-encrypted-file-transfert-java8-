package client;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.SignedObject;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;

import javax.crypto.SecretKey;
import javax.swing.JOptionPane;

import client.user_interface.UserController;
import client.user_interface.UserModal;
import client.user_interface.UserView;
import encryption.Security;
import encryption.Utils;

public class FtpClient{
	
	private static FtpClient clientSingleton;
	private Socket clientSocket;
	private DataOutputStream clientOutPutStream;
	private DataInputStream clientInputStream;
	private BufferedReader buffer;
	private ObjectOutputStream oos ;
	
	String ip;
	int port;
	
	

	private FtpClient(String ip, int port) {
		this.ip = ip;
		this.port = port;
	}
	public static FtpClient getInstance(String ip, int port){
		if(clientSingleton == null){
			clientSingleton = new FtpClient(ip, port);
		}
		return clientSingleton;
	}

	public void run() throws IOException, ClassNotFoundException, InterruptedException, NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException, InvalidKeyException, SignatureException   {
		try {
			clientSocket = new Socket(ip,port);
		} catch (UnknownHostException e) {
			JOptionPane.showMessageDialog(null, "Unknown Host !", "Error", JOptionPane.ERROR_MESSAGE);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, e.getStackTrace()[0], e.getMessage(), JOptionPane.ERROR_MESSAGE);
			
		}
		clientInputStream  = new DataInputStream(clientSocket.getInputStream());
		clientOutPutStream = new DataOutputStream(clientSocket.getOutputStream());
		oos = new ObjectOutputStream(clientOutPutStream);
		
		ObjectInputStream ois = new ObjectInputStream (clientInputStream);
		
		Signature signature = Signature.getInstance("SHA256withRSA");
		
		SignedObject signedServerCertificate = (SignedObject) ois.readObject();
		PublicKey  serverPublicKey = (PublicKey) ois.readObject();
		X509Certificate serverCertificate = (X509Certificate) signedServerCertificate.getObject();
		
		if (!signedServerCertificate.verify(serverPublicKey, signature)){
			Thread.currentThread().sleep((long) 5000);
			Thread.currentThread().destroy();
		}
		
		// Verification du certificat du serveur aupres de la CA
		try {
			serverCertificate.verify(serverPublicKey);
		} catch (InvalidKeyException e) {
			JOptionPane.showMessageDialog(null, e.getStackTrace()[0], e.getMessage(), JOptionPane.ERROR_MESSAGE);
			Thread.currentThread().sleep((long) 5000);
			Thread.currentThread().destroy();
		} catch (CertificateException e) {
			JOptionPane.showMessageDialog(null, e.getStackTrace()[0], e.getMessage(), JOptionPane.ERROR_MESSAGE);
			Thread.currentThread().sleep((long) 5000);
			Thread.currentThread().destroy();
		} catch (NoSuchAlgorithmException e) {
			JOptionPane.showMessageDialog(null, e.getStackTrace()[0], e.getMessage(), JOptionPane.ERROR_MESSAGE);
			Thread.currentThread().sleep((long) 5000);
			Thread.currentThread().destroy();
		} catch (NoSuchProviderException e) {
			JOptionPane.showMessageDialog(null, e.getStackTrace()[0], e.getMessage(), JOptionPane.ERROR_MESSAGE);
			Thread.currentThread().sleep((long) 5000);
			Thread.currentThread().destroy();
		} catch (SignatureException e) {
			JOptionPane.showMessageDialog(null, e.getStackTrace()[0], e.getMessage(), JOptionPane.ERROR_MESSAGE);
			Thread.currentThread().sleep((long) 15000);
			Thread.currentThread().destroy();
		}
		
		JOptionPane.showMessageDialog(null, "Server's certificate has been reveived Correctly : !\n"
				+ serverCertificate.getIssuerDN()+"\n"
				+ serverCertificate.getSigAlgName()+"\n", "Success", JOptionPane.INFORMATION_MESSAGE);
		
		
		// Le Client Envoi son certificat 
		KeyPair clientKeyPair = Security.generateRSAKeyPair();
		PrivateKey clientPrivateKey = clientKeyPair.getPrivate();
		PublicKey  clientPublicKey = clientKeyPair.getPublic();
		X509Certificate clientCertificate = null;
		try {
			clientCertificate = Security.createCertificate("FTP Client", clientKeyPair);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
    	SignedObject signedCert = new SignedObject(clientCertificate, clientPrivateKey, signature);
		
    	oos.writeObject(signedCert);
    	oos.writeObject(clientPublicKey);
		
    	//************ la reception de la cle de session cryptee et signee
    	SignedObject signedSessionCryptedKey = (SignedObject) ois.readObject();
    	byte[] sessionCryptedKey = (byte []) signedSessionCryptedKey.getObject();
    	
    	if (!signedSessionCryptedKey.verify(serverPublicKey, signature)){
    		Thread.currentThread().sleep((long) 5000);
			Thread.currentThread().destroy();
    	}
    	
    	SecretKey sessionKey = null;
    	try {
			sessionKey = Security.getDesKeyDecrypted(sessionCryptedKey, clientPrivateKey);
			System.out.println("Session Key Received par le  Client !");
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getStackTrace()[0], e.getMessage(), JOptionPane.ERROR_MESSAGE);
		}
    	
    	
    	// Test
    	//available = clientInputStream.readInt();
    	//byte[] textCrypted = new byte [available];
    	//clientInputStream.readFully(textCrypted, 0, available);
    	//byte[] decripted = Security.DesDecrypt(sessionKey, textCrypted);
    	//ArrayList<String> objet = (ArrayList<String>) Utils.deserialize(decripted);
    	//System.out.println(objet);
    	
    	UserModal modal = new UserModal(clientInputStream, clientOutPutStream, sessionKey);
    	UserView view = new UserView();
    	UserController controller = new UserController(modal, view);
    	controller.control();
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
