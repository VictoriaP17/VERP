package com.axelor.apps.hr.web;

import com.axelor.apps.hr.db.AssistanceReport;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import java.lang.invoke.MethodHandles;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AssistanceReportController {

  private final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  static final class APIParams {
    public String apiUrl = "";
    public final RequestConfig requestConfig = RequestConfig.custom().build();
    public List<NameValuePair> formParameters = new ArrayList<>();
    public HttpClient httpClient = null;
    public HttpPost httpRequest = null;
    public HttpResponse httpResponse = null;
    public HttpEntity httpResponseEntity = null;
    public String responseString = "";
    public JSONObject responseObject = null;
  }

  public static void addFormParameters(
      List<NameValuePair> apiFormParams,
      List<String> parametersNames,
      List<String> parametersValues) {
    for (int i = 0; i < parametersNames.size(); i++) {
      apiFormParams.add(new BasicNameValuePair(parametersNames.get(i), parametersValues.get(i)));
    }
  }

  public static void postRequest(APIParams apiParams) throws Exception {
    apiParams.httpClient =
        HttpClientBuilder.create().setDefaultRequestConfig(apiParams.requestConfig).build();
    apiParams.httpRequest = new HttpPost(apiParams.apiUrl);
    apiParams.httpRequest.setEntity(new UrlEncodedFormEntity(apiParams.formParameters));
    apiParams.httpResponse = apiParams.httpClient.execute(apiParams.httpRequest);
    apiParams.httpResponseEntity = apiParams.httpResponse.getEntity();
    apiParams.responseString = EntityUtils.toString(apiParams.httpResponseEntity);
    apiParams.responseObject = new JSONObject(apiParams.responseString);
  }

  public static String getTimeDifference(String movementDate, String checkInMovementDate) {
    String checkInMovementTime = checkInMovementDate.substring(17, 19);
    String checkInMovementMinutes = checkInMovementDate.substring(20, 22);
    String movementTime = movementDate.substring(17, 19);
    String movementMinutes = movementDate.substring(20, 22);
    int hoursDifference = Integer.parseInt(movementTime) - Integer.parseInt(checkInMovementTime);
    int minutesDifference =
        Integer.parseInt(movementMinutes) - Integer.parseInt(checkInMovementMinutes);
    if (minutesDifference < 0 && hoursDifference > 0) {
      hoursDifference--;
      minutesDifference += 60;
    }

    String timeDifference =
        (Integer.toString(hoursDifference)
            + " hours "
            + Integer.toString(minutesDifference)
            + " minutes ");

    return timeDifference;
  }

  public static String createCompleteRows(
      JSONArray dataArray, Map<String, Integer> newEmployeeNames) throws Exception {
    String completeRows = "";
    for (int i = 0; i < dataArray.length(); i++) {
      JSONArray innerArray = dataArray.getJSONArray(i);
      completeRows += "<tr style='text-align: center;'>";
      String movementDate = innerArray.getString(0);
      String movementEmployeeName = innerArray.getString(1);
      String movementImageURL = innerArray.getString(2);
      if (newEmployeeNames.containsKey(movementEmployeeName)) {
        int firstIndex = newEmployeeNames.get(movementEmployeeName);
        JSONArray checkInArray = dataArray.getJSONArray(firstIndex);
        String checkInMovementDate = checkInArray.getString(0);
        String checkInMovementImageURL = checkInArray.getString(2);
        String movementDuration = getTimeDifference(movementDate, checkInMovementDate);
        completeRows += ("<td>" + movementEmployeeName + "</td>");
        completeRows += ("<td>" + checkInMovementDate + "</td>");
        completeRows += ("<td>" + movementDate + "</td>");
        completeRows += ("<td>" + movementDuration + "</td>");
        completeRows += ("<td><a href='" + checkInMovementImageURL + "'>View image</a></td>");
        completeRows += ("<td><a href='" + movementImageURL + "'>View image</a></td>");
        completeRows += "</tr>";
        newEmployeeNames.remove(movementEmployeeName);
      } else {
        newEmployeeNames.put(movementEmployeeName, i);
      }
    }

    return completeRows;
  }

  public static String createIncompleteRows(
      JSONArray dataArray, Map<String, Integer> newEmployeeNames) throws Exception {
    String incompleteRows = "";
    for (Map.Entry<String, Integer> entry : newEmployeeNames.entrySet()) {
      JSONArray innerArray = dataArray.getJSONArray(entry.getValue());
      String movementEmployeeName = innerArray.getString(1);
      String movementDate = innerArray.getString(0);
      String movementImageURL = innerArray.getString(2);
      incompleteRows += ("<td>" + movementEmployeeName + "</td>");
      incompleteRows += ("<td>" + movementDate + "</td>");
      incompleteRows += ("<td>No checkout record</td>");
      incompleteRows += ("<td>N/A</td>");
      incompleteRows += ("<td><a href='" + movementImageURL + "'>View image</a></td>");
      incompleteRows += ("<td>N/A</td>");
      incompleteRows += "</tr>";
    }

    return incompleteRows;
  }

  public static String getError(JSONObject errorObject) throws Exception {
    String error = errorObject.getString("error");
    return error;
  }

  public void generateAssistanceReport(ActionRequest request, ActionResponse response) {

    AssistanceReport currentReport = request.getContext().asType(AssistanceReport.class);

    boolean enteredEmployee = false;

    LocalDate reportDate = currentReport.getReportDate();
    if (reportDate == null) {
      response.setError("Date is required to generate report", "Null date");
      return;
    }

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    String formattedReportDate = reportDate.format(formatter);

    String reportEmployee = "";

    if (currentReport.getReportEmployee() != null && currentReport.getReportEmployee() != "") {
      enteredEmployee = true;
      reportEmployee = currentReport.getReportEmployee();
    }

    currentReport.setReportDate(null);
    currentReport.setReportEmployee(null);

    APIParams reportGenerationAPI = new APIParams();
    reportGenerationAPI.apiUrl = "";

    List<String> reportParametersNamesList = new ArrayList<>();
    List<String> reportParametersValuesList = new ArrayList<>();

    reportParametersNamesList.add("reportDate");
    reportParametersValuesList.add(formattedReportDate);

    if (enteredEmployee) {
      reportParametersNamesList.add("reportEmployee");
      reportParametersValuesList.add(reportEmployee);
    }

    addFormParameters(
        reportGenerationAPI.formParameters, reportParametersNamesList, reportParametersValuesList);

    try {
      postRequest(reportGenerationAPI);
      int responseCode = reportGenerationAPI.httpResponse.getStatusLine().getStatusCode();
      String responseString = "";
      if (responseCode == 200) {
        Map<String, Integer> newEmployeeNames = new HashMap<>();
        JSONArray dataArray = reportGenerationAPI.responseObject.getJSONArray("data");
        String resultsTableStart =
            "<div style='overflow-x: auto;'><table border='1' style='border-collapse: collapse; width: 50%; margin: 0 auto;'>\r\n"
                + //
                "        <tr style='text-align: center;'>\r\n"
                + //
                "            <td>Employee name</td>\r\n"
                + //
                "            <td>Check-In Timestamp</td>\r\n"
                + //
                "            <td>Check-Out Timestamp</td>\r\n"
                + //
                "            <td>Duration</td>\r\n"
                + //
                "            <td>Check-In Image</td>\r\n"
                + //
                "            <td>Check-Out Timestamp</td>\r\n"
                + //
                "        </tr>";
        String resultsTableEnd = "</table></div>";
        String resultsTableContents = "";
        String resultsIncompleteRows = "";
        String resultsTable = "";

        String resultsCompleteRows = createCompleteRows(dataArray, newEmployeeNames);

        if (newEmployeeNames.size() != 0) {
          resultsIncompleteRows = createIncompleteRows(dataArray, newEmployeeNames);
        }
        resultsTable =
            resultsTableStart + resultsCompleteRows + resultsIncompleteRows + resultsTableEnd;
        response.setValue("htmlResults", resultsTable);
        return;
      } else if (responseCode == 400) {
        String error = getError(reportGenerationAPI.responseObject.getJSONObject("errors"));
        response.setError(error, "Error, exception on API");
        return;
      }
    } catch (Exception e) {
      response.setError(e.toString(), "Exception");
    }
  }
}
