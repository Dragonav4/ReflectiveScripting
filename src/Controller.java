import annotations.Bind;
import javax.script.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;

public class Controller {
    private final Object modelInstance;
    private final Map<String, Object> modelData = new LinkedHashMap<>();
    private int dataSize;

    public Controller(String modelName) {
        try {
            Class<?> modelClass = Class.forName("models." + modelName);
            this.modelInstance = modelClass.getDeclaredConstructor().newInstance();
        } catch (Exception _) {
            throw new RuntimeException("Error initializing model: " + modelName);
        }
    }

    public Controller readDataFrom(String fname) throws Exception {
        modelData.clear();
        try {
            String filePath = "src/" + fname;
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                line = line.replace(",", ".");

                if (line.startsWith("LATA")) {
                    String[] years = line.trim().split("\\s+");
                    dataSize = years.length - 1;
                    modelData.put("LL", dataSize);
                    modelData.put("LATA", years);
                } else {
                    String[] parts = line.split("\\s+");
                    String varName = parts[0];
                    if (parts.length < 2) {
                        System.err.println("No values found for variable: " + varName);
                        continue;
                    }
                    try {
                        double[] values = new double[dataSize];
                        for (int i = 0; i < dataSize; i++) {
                            if (i < parts.length - 1) {
                                values[i] = Double.parseDouble(parts[i + 1]);
                            } else {
                                values[i] = values[i - 1];
                            }
                        }
                        modelData.put(varName, values);
                    } catch (NumberFormatException ex) {
                        System.err.println("Error parsing values for variable: " + varName + ". Line skipped.");
                    }
                }
            }
            bindDataModel();
        } catch (Exception _) {
            throw new Exception("Error reading data from file: " + fname);
        }
        return this;
    }

    private void bindDataModel() throws IllegalAccessException {
        for (Field field : modelInstance.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(Bind.class)) {
                field.setAccessible(true);
                String fieldName = field.getName();
                if (modelData.containsKey(fieldName)) {
                    var fieldValue = modelData.get(fieldName);
                    field.set(modelInstance, fieldValue);
                }
            }
        }
    }

    public String getResultAsCSV() {
        StringBuilder CSV = new StringBuilder();
        for (var entry : modelData.entrySet()) {
            var fieldName = entry.getKey();
            var value = entry.getValue();
            if (value instanceof double[]) {
                var array = (double[]) value;
                CSV.append(fieldName);
                for (double v : array) {
                    CSV.append(",").append(v);
                }
                CSV.append("\n");
            } else if (fieldName == "LATA" && value instanceof String[]) {
                var array = (String[]) value;
                CSV.append(String.join(",", array));
                CSV.append("\n");
            }
        }
        return CSV.toString();
    }

    public Controller runModel() {
        try {
            modelInstance.getClass().getMethod("run").invoke(modelInstance);
            readDataFromModel();
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("The 'run' method is not defined in the model.", e);
        } catch (Exception e) {
            throw new RuntimeException("Error running model", e);
        }
        return this;
    }

    private Controller readDataFromModel() throws IllegalAccessException {
        for (var field : modelInstance.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(Bind.class)) {
                field.setAccessible(true);
                modelData.put(field.getName(), field.get(modelInstance));
            }
        }
        return this;
    }

    public boolean isInitialized() {
        return !modelData.isEmpty();
    }

    public Controller runScriptFromFile(String fname) throws IOException, ScriptException {
        var path = Paths.get(fname);

        String scriptText = Files.readString(path);
        return runScript(scriptText);
    }

    private ScriptEngine createGroovyEngine() {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine groovyEngine = manager.getEngineByExtension("groovy");
        if (groovyEngine == null) {
            throw new RuntimeException("Groovy script engine not found. Make sure Groovy is available.");
        }
        return groovyEngine;
    }

    public Controller runScript(String scriptText) throws ScriptException {
        ScriptEngine engine = createGroovyEngine();

        for (var entry : modelData.entrySet()) {
            engine.put(entry.getKey(), entry.getValue());
        }
        engine.eval(scriptText);

        Bindings engineBindings = engine.getBindings(ScriptContext.ENGINE_SCOPE);

        for (var entry : engineBindings.entrySet()) {
            var key = entry.getKey();
            var value = entry.getValue();
            if (key.length() > 1 && value instanceof double[])
                modelData.put(key, value);
        }
        return this;
    }
}
