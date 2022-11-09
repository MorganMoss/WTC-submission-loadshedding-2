package wethinkcode.service;

import picocli.CommandLine;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.logging.Logger;
import static wethinkcode.logger.Logger.formatted;

public class Properties implements Callable<Integer> {
    private static final String DEFAULT_PORT = "7000";
    private static final Boolean DEFAULT_COMMAND = false;
    private final Service child;
    private Reader reader;
    private final Logger logger;
    private final java.util.Properties properties = new java.util.Properties();

    /**
     * Config File that holds all properties
     */
    @CommandLine.Option(
            names = {"--config", "-c"},
            description = "A file pathname referring to an (existing!) configuration file in standard Java properties-file format",
            type = File.class,
            echo = true
    )
    File config = null;

    /**
     * Port for the service
     */
    @CommandLine.Option(
            names = {"--port", "-p"},
            description = "The name of a directory where CSV datafiles may be found. This option overrides and data-directory setting in a configuration file.",
            type = Integer.class
    )
    public Integer port = null;

    /**
     * Commands enables/disables
     */
    @CommandLine.Option(
            names = {"--commands", "-o"},
            description = "Enables or Disables Commands during runtime from sys.in",
            type = Boolean.class
    )
    Boolean commands = null;

    /**
     * This will get a non-standard field from your .properties file.
     * @param nonStandardField the name of the field
     * @return A string representing that field
     */
    public String get(String nonStandardField){
        return (String) properties.get(nonStandardField);
    }

    /**
     * Creates a new properties instance,
     * loading its default properties from the class givens relative position
     * @param child class extending Service
     * @throws IOException if an I/O error occurs
     */
    Properties(Service child, String ... args) throws IOException {
        this.child = child;

        logger = formatted(this.getClass().getSimpleName() + " " + child.getClass().getSimpleName());

        new CommandLine(this).execute(args);

        loadProperties();
        defaultNullValues();
    }

    /**
     * Uses reflection to fetch the default.properties file from resources.
     * Tries the child class first, then if that fails, it will try the Service class.
     */
    void loadDefaultProperties(){
        InputStream content = child.getDefaultPropertiesStream();
        logger.info("Loaded Default Config File");
        reader = new InputStreamReader(content);
    }

    /**
     * checks to see if there's a custom properties file loaded via CLI
     * @throws IOException If an I/O error occurs
     */
    boolean loadCustomProperties() throws IOException {
        if (config != null){
            logger.info("CLI Argument Found for Config File: " + config);
            reader = new FileReader(config);
        }

        return config != null;
    }

    /**
     * Tries loading a properties file from CLI then Child Resources then Service Resources
     * @throws IOException if an I/O error occurs
     */
    void loadProperties() throws IOException {
        if (!loadCustomProperties()) loadDefaultProperties();

        properties.load(reader);
        logger.info("Properties File Contents: ");
        properties.keySet().forEach(key -> logger.info(key + " = " + properties.getProperty((String) key)));
    }

    /**
     * Attempts to grab default properties of vital
     * environment variables if not specified in config or CLI
     */
    void defaultNullValues() {
        if (port == null) {
            port = Integer.parseInt((String) properties.getOrDefault("port", DEFAULT_PORT));
            logger.info("Loaded Default Port: " + port);
        } else {
            logger.info("CLI Argument Found for Port: " + port);
        }

        if (commands == null) {
            commands = Boolean.valueOf((String) properties.getOrDefault("commands", DEFAULT_COMMAND.toString()));
            logger.info("Loaded Default Commands: " + commands);
        } else {
            logger.info("CLI Argument Found for Commands: " + commands);
        }
    }


    @Override
    public Integer call() {
        return 0;
    }
}
