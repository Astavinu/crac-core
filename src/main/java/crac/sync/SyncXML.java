package crac.sync;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class SyncXML {
	/* @Value("${dbsync.path}") */
	public static final String PATH = "src/main/resources/db_sync.xml";

	public static final String ROOT_NODE = "sync";

	public static final String SYNCHRONIZE_NODE = "synchronize";
	public static final String IMPORT_NODE = "import";
	public static final String DB_NODE = "db";
	public static final String TABLE_NODE = "table";
	public static final String MAP_NODE = "map";

	public static final String DB_URL_ATT = "url";
	public static final String DB_NAME_ATT = "name";
	public static final String DB_USER_ATT = "user";
	public static final String DB_PASSWORD_ATT = "password";
	public static final String DB_INTERVAL_ATT = "interval";
	public static final String DB_DONE_ATT = "done";

	public static final String TABLE_FROM_ATT = "from";
	public static final String TABLE_TO_ATT = "to";

	public static final String MAP_FROM_ATT = "from";
	public static final String MAP_TO_ATT = "to";
	public static final String MAP_DEFAULT_ATT = "default";

	private SyncXMLRoot root;

	public class SyncXMLRoot {
		public ArrayList<SyncXMLDBObject> synchronizeDatabases;
		public ArrayList<SyncXMLDBObject> importDatabases;
		
		public SyncXMLRoot(ArrayList<SyncXMLDBObject> synchronizeDatabases,
				ArrayList<SyncXMLDBObject> importDatabases) {
			this.synchronizeDatabases = synchronizeDatabases;
			this.importDatabases = importDatabases;
		}
		
		public String toXML() {
			StringBuilder b = new StringBuilder();
			b.append("<"+ROOT_NODE+">\n");
			
			b.append("\t<"+SYNCHRONIZE_NODE+">\n");
			for (SyncXMLDBObject o : synchronizeDatabases) {
				b.append(o.toXML());
			}
			b.append("\t</"+SYNCHRONIZE_NODE+">\n");
			
			b.append("\t<"+IMPORT_NODE+">\n");
			for (SyncXMLDBObject o : importDatabases) {
				b.append(o.toXML());
			}
			b.append("\t</"+IMPORT_NODE+">\n");
			
			b.append("</"+ROOT_NODE+">\n");
			return b.toString();
		}
	}

	public class SyncXMLDBObject {
		public SyncXMLDBObject(String url, String name, String user,
				String password, int interval, Boolean done,
				ArrayList<SyncXMLTableObject> tables) {
			this.url = url;
			this.name = name;
			this.user = user;
			this.password = password;
			this.interval = interval;
			this.done = done;
			this.tables = tables;
		}

		public String url;
		public String name;
		public String user;
		public String password;
		public int interval;
		public Boolean done;

		public ArrayList<SyncXMLTableObject> tables;

		@Override
		public String toString() {
			return "[SyncXMLDBObject] url = " + url + ", name = " + name
					+ ", interval = " + interval + ", done = " + done;
		}
		
		public String toXML() {
			StringBuilder b = new StringBuilder();
			b.append("\t\t<"+DB_NODE+" "+DB_URL_ATT+"=\""+url+"\" "+DB_NAME_ATT+"=\""+name+"\" "+DB_USER_ATT+"=\""+user+"\" "+DB_PASSWORD_ATT+"=\""+password+"\" "+(interval >= 0 ? (DB_INTERVAL_ATT+"=\""+interval+"\" ") : "")+(done != null ? (DB_DONE_ATT+"=\""+done+"\"") : "")+">\n");
			
			for (SyncXMLTableObject o : tables) {
				b.append(o.toXML());
			}
			
			b.append("\t\t</"+DB_NODE+">\n");
			return b.toString();
		}
	}

	public class SyncXMLTableObject {
		public SyncXMLTableObject(String from, String to,
				ArrayList<SyncXMLMapObject> mappings) {
			this.from = from;
			this.to = to;
			this.mappings = mappings;
		}

		public String from;
		public String to;

		public ArrayList<SyncXMLMapObject> mappings;

		@Override
		public String toString() {
			return "[SyncXMLTableObject] from = " + from + ", to = " + to;
		}
		
		public String toXML() {
			StringBuilder b = new StringBuilder();
			b.append("\t\t\t<"+TABLE_NODE+" "+TABLE_FROM_ATT+"=\""+from+"\" "+TABLE_TO_ATT+"=\""+to+"\">\n");
			
			for (SyncXMLMapObject o : mappings) {
				b.append(o.toXML());
			}
			
			b.append("\t\t\t</"+TABLE_NODE+">\n");
			return b.toString();
		}
	}

	public class SyncXMLMapObject {
		public SyncXMLMapObject(String from, String to, String defaultValue) {
			this.from = from;
			this.to = to;
			this.defaultValue = defaultValue;
		}

		public String from;
		public String to;
		public String defaultValue;

		@Override
		public String toString() {
			return "[SyncXMLMapObject] from = " + from + ", to = " + to
					+ ", default = " + defaultValue;
		}
		
		public String toXML() {
			return "\t\t\t\t<"+MAP_NODE+" "+(from.length() > 0 ? (MAP_FROM_ATT+"=\""+from+"\" ") : "")+MAP_TO_ATT+"=\""+to+"\" "+(defaultValue.length() > 0 ? (MAP_DEFAULT_ATT+"=\""+defaultValue+"\"") : "") + "></"+MAP_NODE+">\n";
		}
	}

	public SyncXML()
			throws ParserConfigurationException, SAXException, IOException {
		File xmlFile = new File(PATH);

		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
				.newInstance();
		DocumentBuilder documentBuilder = documentBuilderFactory
				.newDocumentBuilder();
		Document document = documentBuilder.parse(xmlFile);

		NodeList rootChildren = document.getChildNodes();
		for (int i = 0; i < rootChildren.getLength(); i++) {
			Node n = rootChildren.item(i);
			if (n.getNodeName().equals(ROOT_NODE))
				root = parseRootNode(n);
		}

		if (root == null)
			throw new RuntimeException("SyncXML: Invalid db_sync.xml file!");
	}

	public SyncXMLRoot getRoot() {
		return root;
	}

	private SyncXMLRoot parseRootNode(Node n) {
		ArrayList<SyncXMLDBObject> synchronizeDatabases = new ArrayList<SyncXMLDBObject>();
		ArrayList<SyncXMLDBObject> importDatabases = new ArrayList<SyncXMLDBObject>();

		NodeList rootChildren = n.getChildNodes();
		for (int i = 0; i < rootChildren.getLength(); i++) {
			Node cur = rootChildren.item(i);
			if (cur.getNodeName().equals(SYNCHRONIZE_NODE))
				synchronizeDatabases.addAll(parseDBNodes(cur));
			else if (cur.getNodeName().equals(IMPORT_NODE))
				importDatabases.addAll(parseDBNodes(cur));
		}

		return new SyncXMLRoot(synchronizeDatabases, importDatabases);
	}

	private ArrayList<SyncXMLDBObject> parseDBNodes(Node n) {
		ArrayList<SyncXMLDBObject> databases = new ArrayList<SyncXMLDBObject>();

		NodeList children = n.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node cur = children.item(i);
			if (cur.getNodeName().equals(DB_NODE)) {
				SyncXMLDBObject db = parseDBNode(cur);
				databases.add(db);
			}
		}

		return databases;
	}

	private SyncXMLDBObject parseDBNode(Node n) {
		// attributes
		// must have
		String url = parseAttribute(n, DB_URL_ATT);
		String name = parseAttribute(n, DB_NAME_ATT);
		String user = parseAttribute(n, DB_USER_ATT);
		String password = parseAttribute(n, DB_PASSWORD_ATT);

		// can have
		int interval = -1;
		try {
			String att = parseAttribute(n, DB_INTERVAL_ATT, false);
			if (att.length() > 0)
				interval = Integer.parseInt(att);
		} catch (Exception e) {
		}
		Boolean done = null;
		try {
			String att = parseAttribute(n, DB_DONE_ATT, false);
			if (att.length() > 0)
				done = Boolean.parseBoolean(att);
		} catch (Exception e) {
		}

		// tables
		ArrayList<SyncXMLTableObject> tables = new ArrayList<SyncXMLTableObject>();
		NodeList children = n.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node cur = children.item(i);
			if (cur.getNodeName().equals(TABLE_NODE)) {
				tables.add(parseTableNode(cur));
			}
		}

		return new SyncXMLDBObject(url, name, user, password, interval, done,
				tables);
	}

	private SyncXMLTableObject parseTableNode(Node n) {
		// attributes
		// must have
		String from = parseAttribute(n, TABLE_FROM_ATT);
		String to = parseAttribute(n, TABLE_TO_ATT);

		// mappings
		ArrayList<SyncXMLMapObject> mappings = new ArrayList<SyncXMLMapObject>();
		NodeList children = n.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node cur = children.item(i);
			if (cur.getNodeName().equals(MAP_NODE)) {
				mappings.add(parseMapNode(cur));
			}
		}

		return new SyncXMLTableObject(from, to, mappings);
	}

	private SyncXMLMapObject parseMapNode(Node n) {
		// attributes
		// must have
		String to = parseAttribute(n, MAP_TO_ATT);

		// can have
		String from = parseAttribute(n, MAP_FROM_ATT, false);
		String defaultValue = parseAttribute(n, MAP_DEFAULT_ATT, false);

		return new SyncXMLMapObject(from, to, defaultValue);
	}

	private String parseAttribute(Node n, String attributeName) {
		return parseAttribute(n, attributeName, true);
	}

	private String parseAttribute(Node n, String attributeName,
			boolean mustHave) {
		NamedNodeMap atts = n.getAttributes();
		Node item = atts.getNamedItem(attributeName);
		if (item == null && mustHave)
			throw new RuntimeException("SyncXML: Missing required \""
					+ attributeName + "\" attribute in node \""
					+ n.getNodeName() + "\"!");
		else if (item == null)
			return "";
		else
			return item.getTextContent();
	}
	
	public String toXML() {
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
				+ root.toXML();
	}
	
	public void write() {
		File xmlFile = new File(PATH);
		
		FileWriter writer = null;
		try {
			writer = new FileWriter(xmlFile, false); // overwrite
			writer.write(toXML());
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
