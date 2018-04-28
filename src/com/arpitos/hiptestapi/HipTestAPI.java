package com.arpitos.hiptestapi;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONArray;
import org.json.JSONObject;

public class HipTestAPI {

	String ACCESS_TOKEN = null;
	String CLIENT = null;
	String UID = null;
	String PROJECT_ID = "80452";

	public HipTestAPI(String project_id) throws Exception {
		this.PROJECT_ID = project_id;

		// Get new Credentials
		renewAPICredentials();

		// Get All Test Projects
		List<Project> projects = getAllProjects();
		for (Project pro : projects) {
			System.out.println(pro.getId() + " : " + pro.getName());
		}

		// Get All Test Scenrio for one particular project
		List<TestScenario> testScenarios = getProjectTestScenarios(PROJECT_ID);
		for (TestScenario test : testScenarios) {
			System.out.println(test.getId() + " : " + test.getName());
		}

		createTestRun("TEST_ARPIT_" + System.currentTimeMillis());
		Thread.sleep(5000);

		// Get All Test runs for one particular project
		List<TestRun> testRuns = getProjectTestRuns(PROJECT_ID);
		for (TestRun testrun : testRuns) {
			System.out.println(testrun.getId() + " : " + testrun.getName());
		}

		// Get Last created test run for one particular project
		String LastTestRunID = getLastCreatedTestRuns(PROJECT_ID);

		// Get Test Case Snapshot for Last created test run
		List<TestSnapshot> testSnapshots = getScenrioOfTestRun(PROJECT_ID, LastTestRunID);
		for (TestSnapshot testsnapshot : testSnapshots) {
			System.out.println(testsnapshot.getId() + " : " + testsnapshot.getName());
		}

		// Set All test case status to Pass
		for (TestSnapshot testSnapshot : testSnapshots) {
			System.out.println(testSnapshot.getId() + " : " + testSnapshot.getName());
			addTestResult(PROJECT_ID, LastTestRunID, testSnapshot.getId(), true);
		}

	}

	private String renewAPICredentials() throws Exception {
		String https_url = "https://app.hiptest.com/api/auth/sign_in";
		URL url = new URL(https_url);
		HttpsURLConnection connector = (HttpsURLConnection) url.openConnection();

		connector.setRequestMethod("POST");
		connector.setRequestProperty("Content-Type", "application/json");

		JSONObject obj = new JSONObject();
		obj.put("email", "arpit.shah@invenco.com");
		obj.put("password", "angel008");
		System.out.println(obj.toString());
		byte[] data = obj.toString().getBytes();
		writeDataToHTTP(connector, data);

		validateResponse(connector);

		// Store globally
		ACCESS_TOKEN = connector.getHeaderField("access-token");
		CLIENT = connector.getHeaderField("client");
		UID = connector.getHeaderField("uid");

		System.out.println("\nUser Credential : ");
		System.out.println("Content-Type : " + connector.getHeaderField("Content-Type"));
		System.out.println("access-token : " + ACCESS_TOKEN);
		System.out.println("token-type : " + connector.getHeaderField("token-type"));
		System.out.println("client : " + CLIENT);
		System.out.println("expiry : " + connector.getHeaderField("expiry"));
		System.out.println("uid : " + UID);

		return readDataFromHTTP(connector);
	}

	public List<Project> getAllProjects() throws Exception {
		String https_url = "https://app.hiptest.com/api/projects/";
		URL url = new URL(https_url);
		HttpsURLConnection connector = (HttpsURLConnection) url.openConnection();

		connector.setRequestMethod("GET");
		connector.setRequestProperty("Accept", "application/vnd.api+json; version=1");
		connector.setRequestProperty("access-token", ACCESS_TOKEN);
		connector.setRequestProperty("client", CLIENT);
		connector.setRequestProperty("uid", UID);

		// printResponse(connector);
		int responseCode;
		if (HttpURLConnection.HTTP_OK != (responseCode = connector.getResponseCode())) {
			System.out.println("Response code is not as expected : " + responseCode);
		}

		String responseData = readDataFromHTTP(connector);

		JSONObject responseJSON = new JSONObject(responseData);
		JSONArray dataArray = responseJSON.getJSONArray("data");

		List<Project> testProjects = new ArrayList<>();
		for (int i = 0; i < dataArray.length(); i++) {
			JSONObject objTestScenario = dataArray.getJSONObject(i);

			if (objTestScenario.getString("type").equals("projects")) {
				JSONObject attributesObj = objTestScenario.getJSONObject("attributes");

				String id = objTestScenario.getString("id");
				String created_at = attributesObj.getString("created-at");
				String updated_at = attributesObj.getString("updated-at");
				String last_author = attributesObj.getString("last-author");
				String name = attributesObj.getString("name");
				String description = attributesObj.getString("description");
				boolean bddmode = attributesObj.getBoolean("bdd-mode");

				testProjects.add(new Project(id, created_at, updated_at, last_author, name, description, bddmode));
			}

		}

		return testProjects;
	}

	public List<TestScenario> getProjectTestScenarios(String project_id) throws Exception {
		String https_url = "https://app.hiptest.com/api/projects/" + project_id + "/scenarios";
		URL url = new URL(https_url);
		HttpsURLConnection connector = (HttpsURLConnection) url.openConnection();

		connector.setRequestMethod("GET");
		connector.setRequestProperty("Accept", "application/vnd.api+json; version=1");
		connector.setRequestProperty("access-token", ACCESS_TOKEN);
		connector.setRequestProperty("client", CLIENT);
		connector.setRequestProperty("uid", UID);

		// printResponse(connector);
		int responseCode;
		if (HttpURLConnection.HTTP_OK != (responseCode = connector.getResponseCode())) {
			System.out.println("Response code is not as expected : " + responseCode);
		}

		String responseData = readDataFromHTTP(connector);

		JSONObject responseJSON = new JSONObject(responseData);
		JSONArray dataArray = responseJSON.getJSONArray("data");

		List<TestScenario> testScenarios = new ArrayList<>();
		for (int i = 0; i < dataArray.length(); i++) {
			JSONObject objTestScenario = dataArray.getJSONObject(i);

			if (objTestScenario.getString("type").equals("scenarios")) {
				JSONObject attributesObj = objTestScenario.getJSONObject("attributes");

				String id = objTestScenario.getString("id");
				String created_at = attributesObj.getString("created-at");
				String updated_at = attributesObj.getString("updated-at");
				String last_author = attributesObj.getString("last-author");
				String name = attributesObj.getString("name");
				String description = attributesObj.getString("description");
				int folder_id = attributesObj.getInt("folder-id");
				String definition = attributesObj.getString("definition");

				testScenarios.add(new TestScenario(id, created_at, updated_at, last_author, name, description, folder_id, definition));
			}

		}

		return testScenarios;
	}

	public List<TestRun> getProjectTestRuns(String project_id) throws Exception {
		String https_url = "https://app.hiptest.com/api/projects/" + project_id + "/test_runs";
		URL url = new URL(https_url);
		HttpsURLConnection connector = (HttpsURLConnection) url.openConnection();

		connector.setRequestMethod("GET");
		connector.setRequestProperty("Accept", "application/vnd.api+json; version=1");
		connector.setRequestProperty("access-token", ACCESS_TOKEN);
		connector.setRequestProperty("client", CLIENT);
		connector.setRequestProperty("uid", UID);

		// printResponse(connector);
		int responseCode;
		if (HttpURLConnection.HTTP_OK != (responseCode = connector.getResponseCode())) {
			System.out.println("Response code is not as expected : " + responseCode);
		}
		String responseData = readDataFromHTTP(connector);

		JSONObject responseJSON = new JSONObject(responseData);
		JSONArray dataArray = responseJSON.getJSONArray("data");

		List<TestRun> testRuns = new ArrayList<>();
		for (int i = 0; i < dataArray.length(); i++) {
			JSONObject objTestScenario = dataArray.getJSONObject(i);

			if (objTestScenario.getString("type").equals("test-runs")) {
				JSONObject attributesObj = objTestScenario.getJSONObject("attributes");
				JSONObject statusObj = objTestScenario.getJSONObject("attributes").getJSONObject("statuses");

				String id = objTestScenario.getString("id");

				String created_at = attributesObj.getString("created-at");
				String updated_at = attributesObj.getString("updated-at");
				String last_author = attributesObj.getString("last-author");
				String name = attributesObj.getString("name");
				String description = attributesObj.getString("description");

				int passed = statusObj.getInt("passed");
				int failed = statusObj.getInt("failed");
				int retest = statusObj.getInt("retest");
				int undefined = statusObj.getInt("undefined");
				int blocked = statusObj.getInt("blocked");
				int skipped = statusObj.getInt("skipped");
				int wip = statusObj.getInt("wip");

				testRuns.add(new TestRun(id, created_at, updated_at, last_author, name, description, passed, failed, retest, undefined, blocked,
						skipped, wip));
			}

		}
		return testRuns;
	}

	public String getLastCreatedTestRuns(String project_id) throws Exception {
		List<TestRun> testRuns = getProjectTestRuns(project_id);
		String testRunID = testRuns.get(0).getId();
		String testRunName = testRuns.get(0).getName();

		System.out.println("testRunID : " + testRunID);
		System.out.println("testRunName : " + testRunName);

		return testRunID;
	}

	public List<TestSnapshot> getScenrioOfTestRun(String project_id, String test_run_id) throws Exception {
		String https_url = "https://app.hiptest.com/api/projects/" + project_id + "/test_runs/" + test_run_id + "/test_snapshots";
		URL url = new URL(https_url);
		HttpsURLConnection connector = (HttpsURLConnection) url.openConnection();

		connector.setRequestMethod("GET");
		connector.setRequestProperty("Accept", "application/vnd.api+json; version=1");
		connector.setRequestProperty("access-token", ACCESS_TOKEN);
		connector.setRequestProperty("client", CLIENT);
		connector.setRequestProperty("uid", UID);

		// printResponse(connector);
		int responseCode;
		if (HttpURLConnection.HTTP_OK != (responseCode = connector.getResponseCode())) {
			System.out.println("Response code is not as expected : " + responseCode);
		}

		String responseData = readDataFromHTTP(connector);

		JSONObject responseJSON = new JSONObject(responseData);
		JSONArray dataArray = responseJSON.getJSONArray("data");

		List<TestSnapshot> testSnapshots = new ArrayList<>();
		for (int i = 0; i < dataArray.length(); i++) {
			JSONObject objTestScenario = dataArray.getJSONObject(i);

			if (objTestScenario.getString("type").equals("test-snapshots")) {
				JSONObject attributesObj = objTestScenario.getJSONObject("attributes");

				String id = objTestScenario.getString("id");
				String created_at = attributesObj.getString("created-at");
				String updated_at = attributesObj.getString("updated-at");
				String last_author = attributesObj.getString("last-author");
				String name = attributesObj.getString("name");

				testSnapshots.add(new TestSnapshot(id, created_at, updated_at, last_author, name));
			}

		}

		return testSnapshots;
	}

	public String createTestRun(String releaseName) throws Exception {

		// Create HTTPS URL request
		String https_url = "https://app.hiptest.com/api/projects/" + PROJECT_ID + "/test_runs";
		URL url = new URL(https_url);
		HttpsURLConnection connector = (HttpsURLConnection) url.openConnection();
		connector.setRequestMethod("POST");
		connector.setRequestProperty("Accept", "application/vnd.api+json; version=1");
		connector.setRequestProperty("access-token", ACCESS_TOKEN);
		connector.setRequestProperty("client", CLIENT);
		connector.setRequestProperty("uid", UID);

		// Construct and send JSON Object
		{
			List<String> scenarioIDs = new ArrayList<>();

			// Get All Test Scenario IDs for given project and add them to
			// request
			List<TestScenario> testScenarios = getProjectTestScenarios(PROJECT_ID);
			for (TestScenario testscenario : testScenarios) {
				scenarioIDs.add(testscenario.getId());
			}

			JSONArray array = new JSONArray();
			for (int i = 0; i < scenarioIDs.size(); i++) {
				array.put(Integer.parseInt(scenarioIDs.get(i)));
			}

			JSONObject obj = new JSONObject();
			obj.put("name", releaseName);
			obj.put("scenario_ids", array);
			JSONObject obj1 = new JSONObject();
			obj1.put("attributes", obj);
			JSONObject obj2 = new JSONObject();
			obj2.put("data", obj1);

			System.out.println(obj2);
			byte[] data = obj2.toString().getBytes();

			writeDataToHTTP(connector, data);
		}
		validateResponse(connector);
		String responseData = readDataFromHTTP(connector);

		// Get Test run ID from response
		JSONObject responseJSON = new JSONObject(responseData);
		return responseJSON.getJSONObject("data").getString("id");
	}

	public void addTestResult(String project_id, String testRun_id, String testScenario_ID, boolean pass) throws Exception {
		// Create HTTPS URL request
		String https_url = "https://app.hiptest.com/api/projects/" + PROJECT_ID + "/test_runs/" + testRun_id + "/test_snapshots/" + testScenario_ID
				+ "/test_results";
		URL url = new URL(https_url);
		HttpsURLConnection connector = (HttpsURLConnection) url.openConnection();
		connector.setRequestMethod("POST");
		connector.setRequestProperty("Accept", "application/vnd.api+json; version=1");
		connector.setRequestProperty("access-token", ACCESS_TOKEN);
		connector.setRequestProperty("client", CLIENT);
		connector.setRequestProperty("uid", UID);

		// Construct and send JSON Object
		{
			List<String> scenarioIDs = new ArrayList<>();

			// Get All Test Scenario IDs for given project and add them to
			// request
			List<TestScenario> testScenarios = getProjectTestScenarios(PROJECT_ID);
			for (TestScenario testscenario : testScenarios) {
				scenarioIDs.add(testscenario.getId());
			}

			JSONArray array = new JSONArray();
			for (int i = 0; i < scenarioIDs.size(); i++) {
				array.put(Integer.parseInt(scenarioIDs.get(i)));
			}

			JSONObject obj = new JSONObject();
			if (pass) {
				obj.put("status", "passed");
			} else {
				obj.put("status", "failed");
			}
			obj.put("status-author", "BR_" + System.getProperty("user.name"));
			obj.put("description", "BladeRunner_Execution_Complete");
			JSONObject obj1 = new JSONObject();
			obj1.put("attributes", obj);
			obj1.put("type", "test-results");
			JSONObject obj2 = new JSONObject();
			obj2.put("data", obj1);

			System.out.println(obj2);
			byte[] data = obj2.toString().getBytes();

			writeDataToHTTP(connector, data);
		}
		validateResponse(connector);
		String responseData = readDataFromHTTP(connector);

	}

	private void printResponse(HttpsURLConnection con) throws Exception {
		System.out.println("Response Code : " + con.getResponseCode());
		System.out.println("Cipher Suite : " + con.getCipherSuite());
		System.out.println("Response Message : " + con.getResponseMessage());
		System.out.println("\n");

		Certificate[] certs = con.getServerCertificates();
		for (Certificate cert : certs) {
			System.out.println("Cert Type : " + cert.getType());
			System.out.println("Cert Hash Code : " + cert.hashCode());
			System.out.println("Cert Public Key Algorithm : " + cert.getPublicKey().getAlgorithm());
			System.out.println("Cert Public Key Format : " + cert.getPublicKey().getFormat());
			System.out.println("\n");
		}
	}

	private String readDataFromHTTP(HttpsURLConnection connector) {
		StringBuilder sb = new StringBuilder();
		BufferedInputStream is = null;
		try {
			is = new BufferedInputStream(connector.getInputStream());
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String inputLine = "";

			while ((inputLine = br.readLine()) != null) {
				sb.append(inputLine);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		System.out.println("\n" + sb.toString());
		return sb.toString();
	}

	private void writeDataToHTTP(HttpsURLConnection connector, byte[] data) throws IOException {
		// Send data
		connector.setDoOutput(true);
		DataOutputStream outputStream = new DataOutputStream(connector.getOutputStream());
		outputStream.write(data);
		outputStream.flush();
		outputStream.close();
	}

	private void validateResponse(HttpsURLConnection connector) throws IOException {
		int responseCode = connector.getResponseCode();
		if (HttpsURLConnection.HTTP_OK == responseCode || HttpsURLConnection.HTTP_ACCEPTED == responseCode) {
			System.out.println("Response code is not as expected : " + responseCode);
		}
	}

}
