package robocup.model;

import java.io.ByteArrayInputStream;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class ProtoLog {
	//private ArrayList<ByteArrayInputStream> messages = new ArrayList<ByteArrayInputStream>();
	/**
	 * Long is the timestamp for the inputStream
	 */
	private LinkedHashMap<Long, ByteArrayInputStream> messages = new LinkedHashMap<Long, ByteArrayInputStream>();
	private int cursor = 0;
	
	
	public void genRandomData(int size){
		long millis = System.currentTimeMillis();
		for(int i = 0; i < size; i++){
			messages.put(millis + i*100, new ByteArrayInputStream(new byte[]  {0,0}));
		}
	}
	
	/**
	 * @return current index in the message-array
	 */
	public int getCursor(){
		return cursor;
	}
	
	/**
	 * @return total number of messages in this log
	 */
	public int getSize(){
		return messages.size();
	}
	
	public Long getTimeDelta(){
		if(cursor < messages.size()-1)
			return (getKeyByIndex(messages, cursor+1) - getKeyByIndex(messages, cursor));
		return (long) 0;
	}

	public ByteArrayInputStream getValueByIndex(LinkedHashMap<Long, ByteArrayInputStream> hMap, int index){
	   return (ByteArrayInputStream) hMap.values().toArray()[index];
	}
	
	public Long getKeyByIndex(LinkedHashMap<Long, ByteArrayInputStream> hMap, int index){
	   return (Long) hMap.keySet().toArray()[index];
	}
	
	/**
	 * sets current cursor in log
	 * @param _index new cursorPosition
	 */
	public void setCursor(int _cursor){
		if(cursor <= messages.size() -1)
			cursor = _cursor;
	}
	
	public boolean saveToFile(String fileName){
		try(FileWriter fw = new FileWriter(fileName)) {
		    fw.write("ja dit werkt dus niet echt zegmaar");
		    fw.close();
		    return true;
		} catch(Exception e){ }
		return false;
	}
}
