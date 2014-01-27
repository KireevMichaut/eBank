import java.io.*;
import java.util.*;
import java.net.*;


public class BankServer {

	
	protected ArrayList<PrintWriter> _tabCli = new ArrayList<PrintWriter>();
	protected int _cliCnt = 0;
	
	protected static String serverName = "Otter";
	
	protected static int aPORT = 9292;
	
	
	// Main: attente des clients
	@SuppressWarnings("resource")
	public static void main(String[] args){
			
			BankServer serv = new BankServer();
			
			try{
				Integer  port = 0;
				if(args.length<=0){
					port = new Integer(aPORT); // 18000 -> Port par défaut
				}else{
					port = new Integer(args[0]);// Choix d'un numéro de port
				}
				
				ServerSocket ss = new ServerSocket(port.intValue());
				
				printWelcome(port);
				
				while(true){
					new BankThread(ss.accept(),serv);// Un client se connecte, un nouveau Thread est créé
				}
			}catch(Exception e){
				System.err.println("Probleme -> "+e.getMessage());
			}
			
		}
		
	static private void printWelcome(Integer port){
		System.out.println("--------");
	    System.out.println(serverName+" : From PSKL & Sergio");
	    System.out.println("--------");
	    System.out.println("Starts on port : "+port.toString());
	    System.out.println("--------");
	    System.out.println("Quit : hit \"quit\"");
	    System.out.println("Connections count : hit \"total\"");
	    System.out.println("--------");
	}
		
		// Envoi d'un message à tous les client
		synchronized public void sendAll(String message, String sLast){
			
			// On parcourt la table des connexions
			PrintWriter out;
			for(int i=0; i<_tabCli.size(); i++){
				out = _tabCli.get(i);
				
				if(out != null){
					out.print(message+sLast);
					out.flush();
				}
				
			}
			
		}
		
		// Suppression d'un client
		synchronized public void delClient(int index){
			_cliCnt --;
			
			if(_tabCli.get(index) != null){
				_tabCli.remove(index);
			}
		}
		
		// Ajout d'un nouveau client. On retourne le numéro du client ajouté
		synchronized public int addClient(PrintWriter out){
			_cliCnt++;
			_tabCli.add(out);
			
			return _cliCnt-1;
		}
		
		// Retourne le nombre de clients connectés
		synchronized public int getCliCnt(){
			return _cliCnt;
		}
}
