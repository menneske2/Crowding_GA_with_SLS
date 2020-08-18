/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package statistics;

import java.io.File;
import java.io.FileWriter;

/**
 *
 * @author Fredrik-Oslo
 */
public class ResultsWriter {
	
	private final FileWriter writer;
	
	public ResultsWriter(String filename){
		try{
			File f = new File(filename);
			if(f.exists())
				f.delete();
			f.createNewFile();
			writer = new FileWriter(f);
		} catch(Exception e){
			throw new Error();
		}
	}
	
	public void write(String s){
		try{
			System.out.print(s);
			writer.write(s);
		} catch(Exception e){
			throw new Error();
		}
	}
	
	public void writeln(String s){
		s += "\n";
		write(s);
	}
	
	public void flush(){
		try{
			writer.flush();
		} catch(Exception e){
			throw new Error();
		}
	}
	
	public void close(){
		try{
			writer.close();
		} catch(Exception e){
			throw new Error();
		}
	}
}
