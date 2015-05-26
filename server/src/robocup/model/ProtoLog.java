package robocup.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import robocup.model.enums.LogState;

public class ProtoLog {
	//private ArrayList<byte[]> messages = new ArrayList<byte[]>();
	/**
	 * Long is the timestamp for the inputStream
	 */
	private LinkedHashMap<Long, byte[]> messages = new LinkedHashMap<Long, byte[]>();
	private int cursor;
	private LogState state;

	public ProtoLog() {	
		state = LogState.READY;
		cursor = 0;
	}

	public void loadMessages(File toRead) {
		LinkedHashMap<Long, byte[]> linkedHashMapList = new LinkedHashMap<Long, byte[]>();
        try{
            FileInputStream fileInputStream = new FileInputStream(toRead);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            linkedHashMapList = (LinkedHashMap<Long, byte[]>)objectInputStream.readObject();
            objectInputStream.close();
            fileInputStream.close();
            for(Entry<Long, byte[]> m :linkedHashMapList.entrySet()){
                linkedHashMapList.put(m.getKey(), m.getValue());
            }

        }catch(Exception e){ }
        messages = linkedHashMapList;
	}

	public void clear(){
		messages = new LinkedHashMap<Long, byte[]>();
		cursor = 0;
		state = LogState.READY;
	}
	public LogState getState(){
		return state;
	}
	
	public void setState(LogState _state){
		state = _state;
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
		return (long) 1;
	}

	private byte[] getValueByIndex(LinkedHashMap<Long, byte[]> hMap, int index){
	   return (byte[]) hMap.values().toArray()[index];
	}
	
	private Long getKeyByIndex(LinkedHashMap<Long, byte[]> hMap, int index){
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
		try{
		    File fileOne = new File(fileName);
		    FileOutputStream fileOutputStream = new FileOutputStream(fileOne);
		    ObjectOutputStream oos = new ObjectOutputStream(fileOutputStream);
		
		    oos.writeObject(messages);
		    oos.flush();
		    oos.close();
		    fileOutputStream.close();
		}catch(Exception e){
			return false;
		}
		return true;
	}

	public byte[] getData(int _cursor) {
		return getValueByIndex(messages, _cursor);
	}

	public void add(byte[] bs) {
		messages.put(System.currentTimeMillis(), bs);
	}
}
