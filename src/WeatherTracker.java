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
	
    private static final String RESPONSE = "response";
    private static final String HISTORY  = "history";
    private static final String ERROR    = "error";
    private static final String INVALID_OPTION = "Invalid option. Please use option -h or "
                                                 + "--help a list of available commands";
    private static final String USAGE_MSG = "WeatherTracker -k [api_key] -f [feature] [-options]\n"
                                            + "Query Wunderground for weather data.\n The 'k' option must "
                                            + "be used for all feature requests";
    
    public static Boolean validData (JsonNode node) {
        return node.get(RESPONSE).get(ERROR) == null;
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
	
    public static boolean fetchHistoricalData(String apiKey, String city, String state, String date, String savePath) 
            throws MalformedURLException, IOException {
		// Do not attempt to get historical data unless all parameters have been passed
        if (city == null || state == null || date == null) {
            throw new IllegalArgumentException("City, State, and Date must be provided when requesting historical data");
        } 
		
        JsonNode json = new WundergroundData(apiKey).fetchHistorical(city, state, date);

        if (!validData(json)) {
            System.out.println(json.get(RESPONSE).get(ERROR));
            return false;
        }
		
	    //Files and full path will be saved in the format of ${savePath}/${city}/${date}.json
        String dirPath = String.format("%s/%s", savePath, city);
        String fileName = String.format("%s.json", date);
        
        saveDataAsFile(json.path(HISTORY), dirPath, fileName);
        
        return true;
    }
	
    public static void main(String args[]) throws IOException, ParseException {
		
        String feature  = null;
        String city     = null;
        String state    = null;
        String date     = null;
        String apiKey   = null;
        String savePath = System.getProperty("user.dir");
	    
	    //Initialize and set up CLI help options
        Options options = new Options();
        options.addOption("f", "feature", true , "Feature requested");
        options.addOption("p", "path"   , true , "Location to save file (defaults to current working directory)");
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
        if (cmd.hasOption("f")) { 
            feature = cmd.getOptionValue("f");
        }
        if (cmd.hasOption("p")) {
            savePath = cmd.getOptionValue("p") ;
        }
        if (cmd.hasOption("c")) { 
            city = cmd.getOptionValue("c");
        }
        if (cmd.hasOption("s")) { 
            state = cmd.getOptionValue("s");
        }
        if (cmd.hasOption("d")) { 
            date = cmd.getOptionValue("d");
        }
        if (cmd.hasOption("k")) { 
            apiKey = cmd.getOptionValue("k");
        }
        
        // Main entry point
        if (cmd.hasOption("h") || args.length == 0) {
            new HelpFormatter().printHelp(USAGE_MSG, options);
        }
        else if ("history".equals(feature) && apiKey != null) {
            fetchHistoricalData(apiKey, city, state, date, savePath); 
        }
        else {
            System.out.println(INVALID_OPTION);
        }
    }
}