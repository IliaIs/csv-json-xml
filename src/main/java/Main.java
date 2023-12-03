import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        String[] columnMapping =
                {"id", "firstName", "lastName", "country", "age"};
        String fileName =
                "/Users/ilyaisaev/IdeaProjects/csv-json-xml/src/main/resources/data.csv";
        List<Employee> list = parseCSV(columnMapping, fileName);
        String json = listToJson(list);
        jsonWrite(json, "/Users/ilyaisaev/IdeaProjects/csv-json-xml/src/main/resources/jsonData.json");
        List<Employee> secondList = parseXML(
                "/Users/ilyaisaev/IdeaProjects/csv-json-xml/src/main/resources/data1.xml");
        String secondJson = listToJson(secondList);
        jsonWrite(secondJson, "/Users/ilyaisaev/IdeaProjects/csv-json-xml/src/main/resources/jsonData2.json");
        String jsonFile = readString(
                "/Users/ilyaisaev/IdeaProjects/csv-json-xml/src/main/resources/jsonData.json");
        List<Employee> thirdList = jsonToList(jsonFile);
        System.out.println(thirdList);
    }

    public static List<Employee> jsonToList(String json) {
        Gson gson = new Gson();
        Type listType = new TypeToken<List<Employee>>(){}.getType();
        return gson.fromJson(json, listType);
    }

    public static String readString(String path) {
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String strJson = reader.readLine();
            StringBuilder json = new StringBuilder();
            while (strJson != null) {
                json.append(strJson);
                strJson = reader.readLine();
            }
            return json.toString();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public static List<Employee> parseCSV(String[] columnMapping, String fileName) {
        try (CSVReader reader = new CSVReader(new FileReader(fileName))) {
            ColumnPositionMappingStrategy<Employee> strategy =
                    new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);
            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(reader)
                    .withMappingStrategy(strategy)
                    .build();
            return csv.parse();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public static List<Employee> parseXML(String path) {
        try {
            List<Employee> list = new ArrayList<>();
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new File(path));
            NodeList nodeList = document.getElementsByTagName("employee");
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    Employee employee = new Employee(
                            Long.parseLong(element.getElementsByTagName("id").item(0).getTextContent()),
                            element.getElementsByTagName("firstName").item(0).getTextContent(),
                            element.getElementsByTagName("lastName").item(0).getTextContent(),
                            element.getElementsByTagName("country").item(0).getTextContent(),
                            Integer.parseInt(element.getElementsByTagName("age").item(0).getTextContent())
                    );
                    list.add(employee);
                }
            }
            return list;
        } catch (ParserConfigurationException | SAXException | IOException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public static String listToJson(List<Employee> list) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.setPrettyPrinting().create();
        Type listType = new TypeToken<List<Employee>>() {
        }.getType();
        return gson.toJson(list, listType);
    }

    public static void jsonWrite(String json, String path) {
        try (FileWriter writer = new FileWriter(path)) {
            writer.write(json);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
