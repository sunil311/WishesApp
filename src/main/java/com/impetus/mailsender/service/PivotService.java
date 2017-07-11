package com.impetus.mailsender.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.impetus.mailsender.beans.Employee;
import com.impetus.mailsender.beans.Filter;
import com.impetus.mailsender.exception.BWisherException;
import com.impetus.mailsender.util.DataHelper;

@Service
public class PivotService implements DataService {
    private ObjectMapper mapper = new ObjectMapper();

    @Override
    public List<Employee> getEmployees(Filter filter, Date mailDate) {
        try {
            String webEndPoint = "http://pivot.impetus.co.in:8088/wishes/getUsers";
            ArrayNode jsonArray = getEmployee(webEndPoint, filter, mailDate);
            List<Employee> employees = new ArrayList<Employee>();
            for (int i = 0; i < jsonArray.size(); i++) {
                JsonNode jsonNode = jsonArray.get(i);
                Employee employee = new Employee();
                employee.setEMAIL(jsonNode.get("EMAIL").asText());
                employee.setEMAIL("sunil.gupta@impetus.co.in");
                employee.setNAME(jsonNode.get("NAME").asText());
                employee.setIMGURL(jsonNode.get("IMGURL").asText());
                employee.setSUBJECT(jsonNode.get("SUBJECT").asText());
                employees.add(employee);
            }
            return employees;

        } catch (Exception e) {
            throw new BWisherException("Pivot Service Exception occured.", e);
        }
    }

    private ArrayNode getEmployee(String webEndPoint, Filter filter, Date mailDate) throws Exception {
        try {
            URL url = new URL(webEndPoint);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            String input = prepareRequestData(filter, mailDate);// "{\"location\":[\"NOIDA\"],\"occations\":[\"Birthday\"],\"clients\":[\"Amex\"],\"date\":\"2017/06/10\"}";
            OutputStream os = conn.getOutputStream();
            os.write(input.getBytes());
            os.flush();
            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            JsonNode objectNode = mapper.readTree(br);

            conn.disconnect();

            if (objectNode.isArray()) {
                return (ArrayNode) objectNode;
            }
        } catch (IOException e) {
            throw new BWisherException(e);
        }
        return null;
    }

    private String createRequestBody(Filter filter) {
        ObjectNode requestBody = mapper.createObjectNode();
        ArrayNode loc = mapper.createArrayNode();
        loc.add("NOIDA");
        ArrayNode occ = mapper.createArrayNode();
        occ.add("Birthday");
        ArrayNode clients = mapper.createArrayNode();
        clients.add("Amex");
        requestBody.set("location", loc);
        requestBody.set("occations", occ);
        requestBody.set("clients", clients);
        requestBody.put("date", "2017/06/10");
        return requestBody.toString();
    }

    /** @param filter
     * @return */
    private String prepareRequestData(Filter filter, Date mailDate) {
        ObjectNode request = mapper.createObjectNode();

        if (filter != null && filter.getLocations() != null && filter.getLocations().length > 0) {
            ArrayNode locations = mapper.createArrayNode();
            for (String location : filter.getLocations()) {
                locations.add(location);
            }
            request.set("location", locations);
        }

        if (filter != null && filter.getClients() != null && filter.getClients().length > 0) {
            ArrayNode clients = mapper.createArrayNode();
            for (String client : filter.getClients()) {
                clients.add(client);
            }
            request.set("clients", clients);
        }

        if (filter != null && filter.getOccations() != null && filter.getOccations().length > 0) {
            ArrayNode occations = mapper.createArrayNode();
            for (String occation : filter.getOccations()) {
                occations.add(occation);
            }
            request.set("occations", occations);
        }

        request.put("date", DataHelper.formateDateYYYYMMDD(mailDate));
        return request.toString();
    }

}
