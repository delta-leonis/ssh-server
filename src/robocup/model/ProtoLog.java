package robocup.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import robocup.model.enums.LogState;

/**
 * Class that contains a {@link LinkedHashMap} containing a set of raw protobuf data as a byte[] accompanied by the corresponding timestamp (in millis) as key 
 */
public class ProtoLog {
	/** Long is the timestamp for the inputStream */
	private LinkedHashMap<Long, byte[]> messages = new LinkedHashMap<Long, byte[]>();
	private int cursor;
	private LogState state;

	public ProtoLog() {	
		state = LogState.READY;
		cursor = 0;
	}

	/**
	 * Loads {@link LinkedHashMap LinkedHashMap<Long, byte[]>} from a {@link File} to the current log
	 * @param file to read from
	 */
	@SuppressWarnings("unchecked")
	public void loadMessages(File file) {
		LinkedHashMap<Long, byte[]> linkedHashMapList = new LinkedHashMap<Long, byte[]>();
        try{
            FileInputStream fileInputStream = new FileInputStream(file);
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

	/**
	 * Removes all entries from the map, resets cursor and current state
	 */
	public void clear(){
		messages = new LinkedHashMap<Long, byte[]>();
		cursor = 0;
		state = LogState.READY;
	}
	
	/**
	 * @return current state
	 */
	public LogState getState(){
		return state;
	}
	
	/**
	 * Sets current state for playback status
	 * @param _state	new state
	 */
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
	
	/**
	 * @param index
	 * @return	timestamp at given index
	 */
	public long getTimeStamp(int index){
		return getKeyByIndex(index);
	}
	
	/**
	 * @return time before next frame should be loaded in milliseconds 
	 */
	public Long getTimeDelta(){
		if(cursor < messages.size()-1)
			return (getKeyByIndex(cursor+1) - getKeyByIndex( cursor));
		return (long) 1;
	}

	/**
	 * @param index	
	 * @return	the Value at a certain index
	 */
	private byte[] getValueByIndex(int index){
	   return (byte[]) messages.values().toArray()[index];
	}
	
	/**
	 * @param index
	 * @return	the Key at a certain index
	 */
	private Long getKeyByIndex(int index){
	   return (Long) messages.keySet().toArray()[index];
	}
	
	/**
	 * sets current cursor in log
	 * @param _index new cursorPosition
	 */
	public void setCursor(int _cursor){
		if(cursor <= messages.size() -1)
			cursor = _cursor;
	}
	
	/**
	 * Serializes and saves the {@link HashMap} to a file
	 * @param filePath	path to save
	 * @return	true when succesfull
	 */
	public boolean saveToFile(String filePath){
		try{
		    File fileOne = new File(filePath);
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

	/**
	 * @param index	the index for the frame
	 * @return	get the data of a frame at given index
	 */
	public byte[] getData(int index) {
		return getValueByIndex(index);
	}

	/**
	 * Add a bytestring to the current loaded log
	 * @param message	bytestring to add
	 */
	public void add(byte[] message) {
		messages.put(System.currentTimeMillis(), message);
	}
}
