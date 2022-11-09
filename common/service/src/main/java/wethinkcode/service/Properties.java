package wethinkcode.service;

import picocli.CommandLine;

import java.io.*;
import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

import static wethinkcode.logger.Logger.formatted;


public class Properties{
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

    public static void populateFields(Service<?> service, Object child, String ... args){
        try {
            new Properties(service, child, args);
        } catch (IOException e){
            System.err.println("File error has occurred. This is usually due to a missing config or resource file.");
            System.err.println("Stacktrace: ");
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Creates a new properties instance,
     * loading its default properties from the class givens relative position
     * @throws IOException if an I/O error occurs
     */
    Properties(Service<?> service, Object e, String ... args) throws IOException {
        logger = formatted(this.getClass().getSimpleName() + " " + e.getClass().getSimpleName());

        ArrayList<String> propertiesNames = new ArrayList<>() {{
            add("--config");
            add("-c");
        }};

        ArrayList<String> serviceNames = new ArrayList<>() {{
            add("--port");
            add("-p");
            add("--commands");
            add("-o");
            add("--domain");
            add("-dom");
        }};

        String[] a = Arrays
                .stream(args)
                .filter(arg -> propertiesNames.stream().anyMatch(arg::contains))
                .toArray(String[]::new);
        new CommandLine(this).parseArgs(a);

        loadProperties(e.getClass());
        populateEmptyFields(service, e);

        a = Arrays
                .stream(args)
                .filter(arg -> serviceNames.stream().anyMatch(arg::contains))
                .toArray(String[]::new);
        new CommandLine(service).parseArgs(a);
        a = Arrays
                .stream(args)
                .filter(
                        arg ->  serviceNames.stream().noneMatch(arg::contains)
                                && propertiesNames.stream().noneMatch(arg::contains)
                )
                .toArray(String[]::new);
        safeExecute(e, a);
    }


    /**
     * Uses reflection to fetch the default.properties file from resources.
     * Tries the child class first, then if that fails, it will try the Service_OLD class.
     */
    void loadDefaultProperties(Class<?> clazz){
        InputStream content = getDefaultPropertiesStream(clazz);
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
     * Tries loading a properties file from CLI then Child Resources then Service_OLD Resources
     * @throws IOException if an I/O error occurs
     */
    void loadProperties(Class<?> clazz) throws IOException {
        if (!loadCustomProperties()) loadDefaultProperties(clazz);

        properties.load(reader);
        logger.info("Properties File Contents: ");
        properties
                .keySet()
                .forEach(key -> logger.info(key + " = " + properties.getProperty((String) key)));
    }

    /**
     * Loads default properties as an inputStream
     */
    public static InputStream getDefaultPropertiesStream(Class<?> child){
        InputStream content;
        try {
            Path path = Path.of(new File(
                    child
                            .getProtectionDomain()
                            .getCodeSource()
                            .getLocation().toURI()

            ).getPath()).resolve("default.properties");
            content = new FileInputStream(path.toFile());
            Objects.requireNonNull(content);
        } catch (NullPointerException | FileNotFoundException | URISyntaxException e){
            content = Properties.class.getResourceAsStream("/default.properties");
        }

        Objects.requireNonNull(content);

        return content;
    }

    /**
     * Run CLI on classes with this method if there's a possibility that they have no CLI args
     */
    private void safeExecute(Object o, String ... args){
        try {
            new CommandLine(o).parseArgs(args);
        } catch (CommandLine.InitializationException ignored) {}
    }

    private void populateEmptyFields(Object ... objects){
        Arrays
            .stream(objects)
            .forEach(object -> {
                String[] args = Arrays
                        .stream(object.getClass().getDeclaredFields())
                        .filter(field -> Arrays
                             .stream(field.getDeclaredAnnotations())
                             .anyMatch(annotation -> annotation
                                     .annotationType()
                                     .equals(CommandLine.Option.class))
                        )
                        .map(this::populateEmptyField)
                        .filter(Objects::nonNull)
                        .toArray(String[]::new);

                safeExecute(object, args);
            });
    }

    private String populateEmptyField(Field field) {
        CommandLine.Option o = field.getDeclaredAnnotation(CommandLine.Option.class);
        if (properties.get(field.getName()) == null){
            return null;
        }
        String name = o.names()[0];
        String value = (String) properties.get(field.getName());
        return name + "=" + value;
    }
}
