package XMLMapper;

import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.*;

//import org.apache.kafka.clients.producer.KafkaProducer;
//import org.apache.kafka.clients.producer.Producer;
//import org.apache.kafka.clients.producer.ProducerRecord;
import org.json.JSONObject;
import org.json.simple.JSONArray;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class Mapper {

	public static void main(String[] args) {

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

		DocumentBuilder db = null;
		org.w3c.dom.Document dom = null;
		Mapper obj = new Mapper();
		try {
			db = dbf.newDocumentBuilder();
			dom = db.parse(obj.getClass().getResourceAsStream("June3.xml"));
		} catch (Exception ex) {
		}

		try {

			InputStream input = new FileInputStream("C:\\Users\\Bibhudutta Moharana\\eclipse-workspace\\Scheduler\\src\\simplescheduler\\config.properties");

			Properties prop = new Properties();

			// load a properties file
			prop.load(input);
			String url = prop.getProperty("db.url");
			String user = prop.getProperty("db.user");
			String passwd = prop.getProperty("db.password");
			String acc_query = prop.getProperty("account_query");
			String con_query = prop.getProperty("contact_query");
			String add_query = prop.getProperty("address_query");
			String driver = prop.getProperty("db.driverclass");

			Element docEle = dom.getDocumentElement();

			// get a nodelist of elements
			NodeList sourceSystems = docEle.getElementsByTagName("SourceSystem");
			int i;
			if (sourceSystems != null && sourceSystems.getLength() > 0) {

				for (i = 0; i < sourceSystems.getLength(); i++) {
					Element sourceSystem = (Element) sourceSystems.item(i);
					String systemName = sourceSystem.getAttribute("Name");
					if (systemName.equals("MySQL")) {
						NodeList entities = sourceSystem.getElementsByTagName("Entity");

						for (int j = 0; j < entities.getLength(); j++) {
							Element entity = (Element) entities.item(j);
							String entityName = entity.getAttribute("Name");
							if (0 == entityName.compareTo("Account")) {
								NodeList SQLs = sourceSystem.getElementsByTagName("SQL");
								String sql_query = new String();
								for (int k = 0; k < SQLs.getLength(); k++) {
									Element SQL = (Element) SQLs.item(k);
									sql_query = SQL.getNodeValue();
								}
								Class.forName(driver);
								Connection con = DriverManager.getConnection(url, user, passwd);
								Statement st1 = con.createStatement();
								ResultSet rs1 = st1.executeQuery(sql_query);
								ResultSetMetaData rsmd1 = rs1.getMetaData();
								//JSONArray array_account = new JSONArray();

								while (rs1.next()) {

									NodeList attributes = sourceSystem.getElementsByTagName("Attribute");
									for (int k = 0; k < attributes.getLength(); k++) {
										Element attribute = (Element) attributes.item(k);
										String sourceName = attribute.getAttribute("SourceName");
										String targetName = attribute.getAttribute("TargetName");

										int numColumns = rsmd1.getColumnCount();
										JSONObject json_account = new JSONObject();
										for (int l = 1; l <= numColumns; l++) {
											String column_name = rsmd1.getColumnName(l);
											if (0 == column_name.compareTo(sourceName)) {
												json_account.put(targetName, rs1.getObject(column_name));
											}
										}
										System.out.println(json_account);

									}

								}

							}
						}
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();

		}
	}
}
