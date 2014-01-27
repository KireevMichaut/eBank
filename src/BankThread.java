import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

class BankThread implements Runnable{
			
			private Thread _t;			//Thread du client
			private Socket _s;			//socet liée au client
			private PrintWriter _out;	//fllux d'entrée
			private BufferedReader _in;	//flux de sortie
			private BankServer _serv;		//pour utilisation des méthodes de la classe principale
			private int _cliNb = 0;		//Numéro du client
			
			private Commands _c;
			
			
			// Constructeur: on crée le éléments nécessaires au dialogue avec le client
			public BankThread(Socket s, BankServer serv){
				
				_serv = serv; // Permettra de passer de local à global
				_s = s;
				
				try{
					// récupération des flux de communication
					_out = new PrintWriter(s.getOutputStream());
					_in = new BufferedReader(new InputStreamReader(s.getInputStream()));
					// notification du serveur
					_serv.addClient(_out);
					
					
					
				}catch(IOException e){
					System.err.println(e.getMessage());
				}
				
				// Lancement du Thread
				_t = new Thread(this);
				_t.start();
				
			}
			
			// Méthode run()
			// On attend les messages du client et on les redirige 
			public void run(){
				
				
				System.out.println("New Connexion: the new client is n°" + _serv.getCliCnt());
				
				// On lance une couche de lecture pour implémenter un protocole
				_c = new Commands(this);
				_c.handleNewConnection();
				
			}

			public Thread get_t() {
				return _t;
			}

			public Socket get_s() {
				return _s;
			}

			public PrintWriter get_out() {
				return _out;
			}

			public BufferedReader get_in() {
				return _in;
			}

			public BankServer get_serv() {
				return _serv;
			}

			public int get_cliNb() {
				return _cliNb;
			}

			public Commands get_c() {
				return _c;
			}
			
		}
