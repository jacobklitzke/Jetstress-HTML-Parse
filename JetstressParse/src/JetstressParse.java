import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;



public class JetstressParse {

	public static void main(String[] args) throws IOException
	{
		Scanner scanner = new Scanner(System.in);
		
		System.out.print("Enter the path to the stress file: ");
		String stressPath = scanner.nextLine();
		stressPath = stressPath.replace("\"", "");
		
		System.out.print("Enter the path to the backup file: ");
		String backupPath = scanner.nextLine();
		backupPath = backupPath.replace("\"", "");
		
		System.out.print("Enter the path to the recovery file: ");
		String recoveryPath = scanner.nextLine();
		recoveryPath = recoveryPath.replace("\"", "");
		
		File stress = new File(stressPath);
		Document parsedStress = Jsoup.parse(stress, "UTF-8");
		
		File backup = new File(backupPath);
		Document parsedBackup = Jsoup.parse(backup, "UTF-8");
		
		File recovery = new File(recoveryPath);
		Document parsedRecovery = Jsoup.parse(recovery, "UTF-8");
		
		System.out.print("Enter the number of databases used in the test: ");
		int numOfDatabases = scanner.nextInt();
		scanner.close();
		System.out.println();
		System.out.println();
		
//		String stressPath = "C:\\Users\\n292p995\\Desktop\\Stress_2015_7_17_14_9_16.html";
//		String backupPath = "C:\\Users\\n292p995\\Desktop\\DatabaseBackup_2015_7_17_13_43_55.html";
//		String recoveryPath = "C:\\Users\\n292p995\\Desktop\\SoftRecovery_2015_7_17_13_24_24.html";
//		File stress = new File(stressPath);
//		File backup = new File(backupPath);
//		File recovery = new File(recoveryPath);
//		Document parsedStress = Jsoup.parse(stress, "UTF-8");
//		Document parsedBackup = Jsoup.parse(backup, "UTF-8");
//		Document parsedRecovery = Jsoup.parse(recovery, "UTF-8");
//		int numOfDatabases = 2;
		
		Elements fieldsets = parsedStress.getElementsByTag("fieldset");
		
		Element fieldset = fieldsets.get(1);
		Elements tablerows = fieldset.getElementsByTag("tr");
		Element row = tablerows.get(3);
		Elements tabledata = row.getElementsByTag("td");
		double bytes = Double.parseDouble(tabledata.get(1).text());
		double gigabytes = bytes/(Math.pow(1024, 3));	
		
		fieldset = fieldsets.get(4);
		
		Elements tables = fieldset.getElementsByTag("tr");
		tables.remove(0);
		
		
		double databaseReadsSec = 0;
		double databaseWritesSec = 0;
		double databaseReadLatency = 0;
		double databaseWriteLatency = 0;
		double logWritesSec = 0;
		double logWriteLatency = 0;
		for(Element table : tables)
		{
			Elements datas = table.getElementsByTag("td");
			datas.remove(0);
			
			databaseReadsSec += Double.parseDouble(datas.get(2).text());
			databaseWritesSec += Double.parseDouble(datas.get(3).text());
			databaseReadLatency += Double.parseDouble(datas.get(0).text());
			databaseWriteLatency += Double.parseDouble(datas.get(1).text());
			logWritesSec += Double.parseDouble(datas.get(9).text());
			logWriteLatency += Double.parseDouble(datas.get(7).text());
		}
		
		databaseReadsSec = databaseReadsSec/numOfDatabases;
		databaseWritesSec = databaseWritesSec/numOfDatabases;
		databaseReadLatency = databaseReadLatency/numOfDatabases;
		databaseWriteLatency = databaseWriteLatency/numOfDatabases;
		logWritesSec = logWritesSec/numOfDatabases;
		logWriteLatency = logWriteLatency/numOfDatabases;
		
		fieldsets = parsedBackup.getElementsByTag("fieldset"); 
		fieldset = fieldsets.get(0);
		tables = fieldset.getElementsByTag("tr");
		for(int i = 0; i <= numOfDatabases; i++)
		{
			tables.remove(0);
		}
		
		tabledata = tables.get(0).getElementsByTag("td");
		double MBPerDatabase = Double.parseDouble(tabledata.get(3).text());
		tabledata = tables.get(1).getElementsByTag("td");
		double MBPerServer = Double.parseDouble(tabledata.get(3).text());
		
		fieldsets = parsedRecovery.getElementsByTag("fieldset"); 
		fieldset = fieldsets.get(0);
		tables = fieldset.getElementsByTag("tr");
		for(int i = 0; i <= numOfDatabases + 1; i++)
		{
			tables.remove(0);
		}
		
		tabledata = tables.get(0).getElementsByTag("td");
		double playLog = Double.parseDouble(tabledata.get(1).text())/Double.parseDouble(tabledata.get(2).text());
		
		
		String columns = "Excel Category";
		String padding = "                                                  ";
		
		columns += padding.substring(0, padding.length() - columns.length());
		System.out.print(columns);
		System.out.println("Value");
		
		String rowLabel = "Database Capacity Utilization (first number): ";
		rowLabel += padding.substring(0, padding.length() - rowLabel.length());
		
		System.out.print(rowLabel);
		System.out.println(gigabytes);
		
		rowLabel = "Database Disk Reads/sec: ";
		rowLabel += padding.substring(0, padding.length() - rowLabel.length());
		System.out.print(rowLabel);
		System.out.println(databaseReadsSec);
		
		rowLabel = "Database Disk Writes/sec: ";
		rowLabel += padding.substring(0, padding.length() - rowLabel.length());
		System.out.print(rowLabel);
		System.out.println(databaseWritesSec);
		
		rowLabel = "Average Database Disk Read Latency (ms): ";
		rowLabel += padding.substring(0, padding.length() - rowLabel.length());
		System.out.print(rowLabel);
		System.out.println(databaseReadLatency);
		
		rowLabel = "Average Database Disk Write Latency (ms): ";
		rowLabel += padding.substring(0, padding.length() - rowLabel.length());
		System.out.print(rowLabel);
		System.out.println(databaseWriteLatency);
		
		rowLabel = "Log Disk Writes/sec: ";
		rowLabel += padding.substring(0, padding.length() - rowLabel.length());
		System.out.print(rowLabel);
		System.out.println(logWritesSec);
		
		rowLabel = "Average Log Disk Write Latency (ms): ";
		rowLabel += padding.substring(0, padding.length() - rowLabel.length());
		System.out.print(rowLabel);
		System.out.println(logWriteLatency);
		
		rowLabel = "MB Read/sec per database: ";
		rowLabel += padding.substring(0, padding.length() - rowLabel.length());
		System.out.print(rowLabel);
		System.out.println(MBPerDatabase);
		
		rowLabel = "MB Read/sec per server: ";
		rowLabel += padding.substring(0, padding.length() - rowLabel.length());
		System.out.print(rowLabel);
		System.out.println(MBPerServer);
		
		rowLabel = "Average Time to play one log file: ";
		rowLabel += padding.substring(0, padding.length() - rowLabel.length());
		System.out.print(rowLabel);
		System.out.println(playLog);
	}
}
