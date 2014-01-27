import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

// Cette classe est une classe "boîte à outils"
		class Commands{
			
			protected BankThread _t;
			protected BankServer _s;
			protected BufferedReader _in;
			protected PrintWriter _out;

			protected Gson g;
			
			protected BankBase _bBase;
			
			// Données de l'utilisateur
			private String _email;
			private String _pass;
			
			public Commands (BankThread t){
				this._t = t;
				this._s = t.get_serv();
				this._in = t.get_in();
				this._out = t.get_out();
				
				this.g = new Gson();
				
				this._bBase = new BankBase("jdbc:mysql://localhost:3306/db_ebank", "BankServer", "ebank");
			}
			
			
			
		
			public void handleNewConnection(){

				String message =""; // message du client
				
				
				try{
					// Variable pour savoir si on continue
					ArrayList<String> errorLog = new ArrayList<String>();

					
					
					_out.println("Server Otter | CacheCash server");
					_out.println("                                                  |      .-\"\"\"-.");
					_out.println("--------------------------------------------      |     /      o\\");
					_out.println("                                                  |    |    o   0).-.");
					_out.println("Copyright : PSKL & SERGIO, 2014                   |    |       .-;(_/     .-.");
					_out.println("                                                  |     \\     /  /)).---._|  `\\   ,");
					_out.println("--------------------------------------------      |      '.  '  /((       `'-./ _/|");
					_out.println("                                                  |        \\  .'  )        .-.;`  /");
					_out.println("                                                  |         '.             |  `\\-'");
					_out.println("                                                  |           '._        -'    /");
					_out.println("                                                  |     jgs      ``\"\"--`------`");
					_out.println();
					
					// On demande à l'hôte ce qu'il veut
					do{
						_out.println(" > What you can do: ");
						_out.println("	- Type 1 to get the password of an account");
						_out.println("	- Type 2 to check the balance of an account");
						_out.println("	- Type 3 to credit an account");
						_out.println("	- Type quit to exit");
						
						_out.flush();
						
						message = _in.readLine();
						
					}while(!message.equals("1")
							&& !message.equals("2")
							&& !message.equals("3")
							&& !message.equals("quit"));
					
					// Redirection des demandes
					if(message.equals("quit")){
						endConnection();
						return;
					}else if(message.equals("1")){
						giveEncPass();
						endConnection();
					}else if(message.equals("2")){
						checkAccountBalance();
						endConnection();
					}else if(message.equals("3")){
						cashAccount();
						endConnection();
					}
					
					
					
				// cas d'erreur	
				}catch(IOException e){
					System.err.println(e.getMessage());
				
				// Cas de déconnexion
				}finally{
					
					// Cas normal -> déconnexion du client
					try{
						// On indique la déconnexion du client
						System.out.println("Lynx --> Client n°"+_s.getCliCnt()+" just disconnected");
						_s.delClient(_s.getCliCnt()-1);
						_t.get_s().close();
						
					}catch(IOException e){
						System.out.println(e.getMessage());
					}
				}

				
				
			}
			
			private void giveEncPass(){
				_out.println(" > Please provide account id: ");
				_out.flush();
				
				try {
					int accountID = new Integer(_in.readLine());
					
					String encPass = _bBase.getAccountEncryptPass(accountID);
					
					if(encPass != null){
						_out.println(encPass);
						
					}else{
						_out.println("[ERROR] Couldn't get the encrypted password! for account n°"+accountID);
					}
					_out.flush();
					
				} catch (IOException e) {
					System.err.println("Couldn't get the request from the host : " +e.getMessage());
				}
				
				
			}
			
			private void checkAccountBalance(){
				_out.println(" > Please provide account id and amount to retrieve : {id: ???, amount: ???}");
				_out.flush();
				
				try {
					String message = _in.readLine();
					AccountTransaction abc = g.fromJson(message, AccountTransaction.class);
					
					if(_bBase.checkBalance(abc.getID(), abc.getAmount())){
						_out.println(" > OK");
					}else{
						_out.println(" > NOT OK");
					}
					_out.flush();
					
				} catch (IOException e) {
					System.err.println("Couldn't get the request from the host : " +e.getMessage());
				}
			}
			
			private void endConnection(){
				System.out.println("End of connection");
				
				_out.println("Bye !");
				_out.flush();
				return;
			}
			
			
			private void cashAccount(){
				_out.println(" > Please provide account id and amount to cash : {id: ???, amount: ???}");
				_out.flush();
				
				try {
					String message = _in.readLine();
					AccountTransaction abc = g.fromJson(message, AccountTransaction.class);
					
					if(_bBase.creditAccount(abc.getID(), abc.getAmount())){
						_out.println(" > OK, account n°"+abc.getID()+" credited for € " + abc.getAmount());
					}else{
						_out.println(" > NOT OK");
					}
					_out.flush();
					
				} catch (IOException e) {
					System.err.println("Couldn't get the request from the host : " +e.getMessage());
				}
			}
			
			
			
			
			class AccountTransaction{
				private int id;
				private double amount; 
				
				public int getID(){
					return id;
				}
				public double getAmount(){
					return amount;
				}
			}
			
		}
	
		
