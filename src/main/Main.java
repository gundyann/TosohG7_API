
/**
 *date: 08.11.2019   -  time: 12:53:06
 *user: yanng   -  yann.gund@gmx.ch
 *
 */
package main;

import g7anbindung.TosohAPI;

//Entrypoint of Application, starts the API to the Tosoh G7.
public class Main {
	public static void main(String[] args) {
		
		System.out.println("Start of programm");
		Thread tosohAPI = new Thread(new TosohAPI());
		tosohAPI.start();		 
	}
	
}
