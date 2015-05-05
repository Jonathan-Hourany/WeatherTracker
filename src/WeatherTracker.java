//package weather_tracker;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class WeatherTracker {
	
	public static Boolean validData (JsonNode node) {
	    return node.get("response").get("error") == null;
	}
	
	public static void saveDataAsFile(JsonNode node, String dirPath, String fileName) 
	        throws JsonGenerationException, JsonMappingException, IOException {
		
	    File dir = new File(dirPath);
	    
		if (!dir.exists()) {
		    if (!dir.mkdirs()) {
		        throw new IOException("Could not make file at " + dirPath);
		    } 
		}
		
		File file = new File(dirPath, fileName);
        new ObjectMapper().writeValue(file, node);  
        System.out.println("File created at: " + file);
	}
	
	public static boolean historicalData(String apiKey, String city, String state, String date) 
	        throws MalformedURLException, IOException {
		// Do not attempt to get historical data unless all parameters have been passed
		if (city == null || state == null || date == null) {
			throw new IllegalArgumentException("City, State, and Date must be provided when using historical look-up");
		} 
		else {
			JsonNode json = new WundergroundData(apiKey).fetchHistorical(city, state, date); //.path("history").path("dailysummary");

			if (validData(json)) {
				//Files and full path will be saved in the format of ${cwd}/${city}/${date}.json
				String dirPath = String.format("%s/%s", System.getProperty("user.dir"), city);
				String fileName = String.format("%s.json", date);
				
				saveDataAsFile(json.path("history"), dirPath, fileName);
				
				return true;
			}
			else { 
				System.out.println(json.get("response").get("error"));
				return false;
			}
		}
	}
	
	public static void main(String args[]) throws IOException, ParseException {
		
		String feature = null;
		String city    = null;
		String state   = null;
		String date    = null;
		String apiKey  = "52e46c4b8074e7f5";
		
		//Initialize and set up CLI help options
		Options options = new Options();
		options.addOption("f", "feature", true , "Feature requested");
		options.addOption("c", "city"	, true , "City requested");
		options.addOption("s", "state"	, true , "");
		options.addOption("d", "date"	, true , "Format as YYYMMDD. Date of look-up when doing a historical query");
		options.addOption("k", "key"	, true , "Wunderground API Key");
		options.addOption("h", "help"	, false, "Show help");
		
		//Initialize CLI Parsers
		CommandLineParser parser = new BasicParser();
		
		// Parse CLI input
		CommandLine cmd = parser.parse(options, args);
		
		// Set CLI input to variables
		if (cmd.hasOption("f")) { feature = cmd.getOptionValue("f"); }
		if (cmd.hasOption("c")) { city	  = cmd.getOptionValue("c"); }
		if (cmd.hasOption("s")) { state   = cmd.getOptionValue("s"); }
		if (cmd.hasOption("d")) { date	  = cmd.getOptionValue("d"); }
		if (cmd.hasOption("k")) { apiKey  = cmd.getOptionValue("k"); }
		
		// History Feature
		if (cmd.hasOption("h") || args.length == 0) {
		    new HelpFormatter().printHelp("Query Wunderground for weather data", options);
		} 
		else if ("history".equals(feature)) { 
		        historicalData(apiKey, city, state, date); 
		}
	    else {
	      System.out.println("Invalid feature. Please use option -h or --help a list of available commands");
	    }
	}
}