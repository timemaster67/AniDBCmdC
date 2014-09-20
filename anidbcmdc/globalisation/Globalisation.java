package anidbcmdc.globalisation;

import java.util.Locale;
import java.util.ResourceBundle;

public class Globalisation {
	public ResourceBundle messages;
	
	
	public Globalisation(){
		messages = ResourceBundle.getBundle("AniDBCmdC",Locale.getDefault());	
	}
	
	public ResourceBundle getTranslation(){
		return messages;
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
	}

}
