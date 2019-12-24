
/**
 *date: 08.11.2019   -  time: 10:43:22
 *user: yanng   -  yann.gund@gmx.ch
 *
 */
package dataHandler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public abstract class DataHandlerToSystem extends DataHandlerDecorator {

	public DataHandlerToSystem(DataHandlerG7 handler) {
		super(handler);
	}
	
	protected void writeFileToSystem(String defPath, String content) {
		File file = new File(defPath);
		try (FileOutputStream outputStream = new FileOutputStream(file)) {
			if (!file.exists()) {
				file.createNewFile();
			}
			byte[] contentInBytes = content.getBytes();

			outputStream.write(contentInBytes);
			outputStream.flush();
			outputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}		
}
