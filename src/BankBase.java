import java.sql.*;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class BankBase {

	private Connection connection;
	private static final String SECRET_KEY = "banksecret";
	
	public BankBase(String url, String user, String pass){
		// Connexion à la base de données
		try {
	        Class.forName("com.mysql.jdbc.Driver");
	    } catch (ClassNotFoundException e) {
	        System.err.println("Impossible de charger le driver " + e.getMessage());
	    } 
		try{
			connection = DriverManager.getConnection(url, user, pass);
			
		}catch (SQLException e) {
			System.err.println("[SQL ERROR] Couldn't connect to the database : " + e.getMessage());
			return;
		}
		
	}
	
	// Récupération du mot de passe d'un utilisateur repéré par son numéro de compte
	public String getAccountEncryptPass(int accountID){
		
			PreparedStatement pst;
			try {
				pst = connection.prepareStatement("SELECT password FROM account WHERE id = ?");
				pst.setInt(1,accountID);
				
				if(!pst.execute()){
					return null;
				}
				
				ResultSet rs = pst.getResultSet();
				rs.next();
				
				return encrypt(rs.getString(1));
			} catch (SQLException e) {
				System.err.println("[SQL ERROR] Impossible to get the password for account " + accountID);
			}
		
		return null;
	}
	
	public boolean checkPasswordValidity(int accountID, String pass){
		
		String encPass = getAccountEncryptPass(accountID);
		
		return encPass.equals(pass);
	}
	
	public boolean creditAccount(int accountID, double amount){
		
		try {
			PreparedStatement pst = connection.prepareStatement("UPDATE account SET balance = balance + ? WHERE id = ?");
			pst.setDouble(1, amount);
			pst.setInt(2, accountID);
			
			if(pst.executeUpdate() != 1){
				return false;
			}
			
			return true;
			
			
		} catch (SQLException e) {
			System.err.println("[SQL ERROR] Impossible to credit the account n°" + accountID +" : "+e.getMessage());
		}
		
		return false;
	}
	
	public boolean checkBalance(int accountID, double threshold){
		
		try {
			PreparedStatement pst = connection.prepareStatement("SELECT balance FROM account WHERE id = ?");
			pst.setInt(1, accountID);
			
			if(!pst.execute()){
				return false;
			}
			
			ResultSet rs = pst.getResultSet();
			rs.next();
			
			double delta = rs.getDouble(1) - threshold;
			
			return delta > 0;
			
		} catch (SQLException e) {
			System.err.println("[SQL ERROR] Impossible to get the balance of account n°"+accountID +" : " + e.getMessage());
		}
		
		return false;
		
		
	}

	// Chiffrement d'une String en blowfish
	private String encrypt(String input){
		byte[] crypt = null;
		
		try {
			// Génération d'une spec de clé secrète à partir de la clé "secret"
			SecretKeySpec KS = new SecretKeySpec(SECRET_KEY.getBytes(), "Blowfish");
			
			// Initialisation d'un code basé sur blowfish
		    Cipher cipher = Cipher.getInstance("Blowfish");
		    cipher.init(Cipher.ENCRYPT_MODE, KS);

		    // chiffrement du message
		    crypt = cipher.doFinal(input.getBytes());
			
		} catch (NoSuchAlgorithmException e) {
			System.err.println("Algorithme inconnu " + e.getMessage());
		} catch (NoSuchPaddingException e) {
			System.err.println("Offset inconnu " + e.getMessage());
		} catch (InvalidKeyException e) {
			System.err.println("Clé non valide " + e.getMessage());
		} catch (IllegalBlockSizeException e) {
			System.err.println("Bloc de taille invalide " + e.getMessage());
		} catch (BadPaddingException e) {
			System.err.println("Mauvais Padding : " + e.getMessage());
		}
		
		// (pour des raisons d'encodage des caractères et de sauvegarde des bytes[] en base, on gardera les HexValues)
		return byteArrayToHexString(crypt);
	}
	
	private static String byteArrayToHexString(byte[] b) {
		  String result = "";
		  for (int i=0; i < b.length; i++) {
		    result += Integer.toString( ( b[i] & 0xff ) + 0x100, 16).substring( 1 );
		  }
		  return result;
	}
	
	
	
	// Main pour test
	public static void main(String[]args){
		// Création d'une connexion
		BankBase tBase = new BankBase("jdbc:mysql://localhost:3306/db_ebank", "BankServer", "ebank");

		if(tBase.checkBalance(12345, 5000.0)){
			System.out.println("OK");
		}else{
			System.out.println("NOTOK");
		}
		
			
	        try {
	            /* Fermeture de la connexion */
	        	tBase.connection.close();
	        } catch ( SQLException ignore ) {
	            /* Si une erreur survient lors de la fermeture, il suffit de l'ignorer. */
	        	System.err.println("[ERREUR SQL] IMPOSSIBLE DE FERMER LA CONNEXION!!");
	        }	

		    
		}
		
}
