package crac.sync;

import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import crac.sync.SyncXML.SyncXMLDBObject;
import crac.sync.SyncXML.SyncXMLMapObject;
import crac.sync.SyncXML.SyncXMLTableObject;

public class SyncManager {

	private int interval;

	public SyncManager() {
		// TODO: either use top db entry interval, or move interval to a <sync> xml attribute?
		interval = 5000;
	}

	public int getIntervalMS() {
		return interval;
	}

	public void sync() {
		System.out.println("SyncManager::sync() @ " + System.currentTimeMillis());
		
		// load config file
		SyncXML xml = null;
		try {
			xml = new SyncXML();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			return;
		} catch (SAXException e) {
			e.printStackTrace();
			return;
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		
		// go through all sync + import dbs
		if (xml != null) {
			// TODO: modify interval variable here with the newest one from the xml file
			
			ArrayList<SyncXMLDBObject> synchronizeDatabases = xml.getRoot().synchronizeDatabases;
			for (SyncXMLDBObject o : synchronizeDatabases) {
				syncDatabase(o);
			}
			
			boolean writeChanges = false;
			ArrayList<SyncXMLDBObject> importDatabases = xml.getRoot().importDatabases;
			for (SyncXMLDBObject o : importDatabases) {
				if (importDatabase(o))
					writeChanges = true;
			}
			
			// if any imports happened successfully, save modified xml (done=true)
			if (writeChanges)
			{
				System.out.println("SyncManager: Saving updated db_sync.xml after import ...");
				xml.write();
				System.out.println("SyncManager: Done.");
			}
		}
	}
	
	private void syncDatabase(SyncXMLDBObject db)
	{
		System.out.println("SyncManager::syncDatabase() for "+db);
		
		// TODO: attempt db connection
		
		// TODO: update/insert/change crac db
		// go through all tables
		for (SyncXMLTableObject t : db.tables) {
			
			// go through all mappings
			for (SyncXMLMapObject m : t.mappings) {
				
			}
		}
	}
	
	private boolean importDatabase(SyncXMLDBObject db)
	{
		System.out.println("SyncManager::importDatabase() for "+db);
		if (db.done)
			return false;
		
		// TODO: attempt db connection
		
		// TODO: update/insert/change crac db
		// go through all tables
		for (SyncXMLTableObject t : db.tables) {
			
			// go through all mappings
			for (SyncXMLMapObject m : t.mappings) {
				
			}
		}
		
		db.done = true; // change
		return true; // ok, we have to SyncXML.write() the changes back in sync()
	}
}
