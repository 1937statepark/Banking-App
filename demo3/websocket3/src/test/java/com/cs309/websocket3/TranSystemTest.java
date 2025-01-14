package com.cs309.websocket3;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import com.cs309.websocket3.Accounts.Account;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Order;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import io.restassured.RestAssured;
import io.restassured.response.Response;

import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.boot.test.web.server.LocalServerPort;	// SBv3

import java.util.HashMap;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
public class TranSystemTest {

  @LocalServerPort
  int port;

  @Before
  public void setUp() {
    RestAssured.port = port;
    RestAssured.baseURI = "http://localhost";
  }

  @Test
  @Order(1)
  public void accountPutTest() {
    // Send request and receive response
    HashMap<String, Object> requestBody = new HashMap<String, Object>();
    requestBody.put("accountNumber", 1);
    requestBody.put("accountName", "Test Account");
    requestBody.put("accountType", "Testing");
    requestBody.put("balance", 100);
    Response response = RestAssured.given().
            header("Content-Type", "application/json").
            body(requestBody).
            when().
            put("/accounts/1");


    // Check status code
    int statusCode = response.getStatusCode();
    assertEquals(200, statusCode);

    // Check response body for correct response
    String returnString = response.getBody().asString();
    try {
      JSONObject returnObj = new JSONObject(returnString);
      assertEquals(1, returnObj.getInt("accountNumber"));
      assertEquals("Test Account", returnObj.get("accountName"));
      assertEquals("Testing", returnObj.get("accountType"));
      assertEquals(100, returnObj.getInt("balance"));
    } catch (JSONException e) {
      e.printStackTrace();
    }

  }

  //Response result dependent on the account update running before this test
  //@Order tag ensures accountPutTest has run before this one
  @Test
  @Order(2)
  public void transactionTest() {
    // Send request and receive response
    HashMap<String, Object> requestBody = new HashMap<String, Object>();
    requestBody.put("transactionAmount", -50);
    requestBody.put("memo", "Testing Transaction");
    Response response = RestAssured.given().
            header("Content-Type", "application/json").
            body(requestBody).
            when().
            post("/transactions/1");


    // Check status code
    int statusCode = response.getStatusCode();
    assertEquals(200, statusCode);

    // Check response body for correct response
    response = RestAssured.given().header("Content-Type", "application/json").
            when().
            get("/accounts/1");

    String returnString = response.getBody().asString();
    try {
      JSONObject returnObj = new JSONObject(returnString);
      assertEquals(150, returnObj.getInt("balance"));
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }

  //Adds user 1 to the list of auth users on the test account. Parsing the return of /accounts/1/auth for the relationship
  @Test
  @Order(3)
  public void setAuthorizedUserTest() {
    // Send request and receive response
    Response response = RestAssured.given().header("Content-Type", "plain/text").
            when().
            post("/accounts/1/auth/1");

    int statusCode = response.getStatusCode();

    assertEquals(200, statusCode);

    //Check authuser
    response = RestAssured.given().header("Content-Type", "application/json").when().get("/accounts/1/auth");
    String returnString = response.getBody().asString();

    try {
      JSONArray returnArr = new JSONArray(returnString);
      JSONObject returnObj = returnArr.getJSONObject(0);
      JSONArray authorizedUsers = returnObj.getJSONArray("authAccounts");
      JSONObject authorizedUser = authorizedUsers.getJSONObject(0);
      assertEquals(1, authorizedUser.getInt("accountNumber"));
    } catch (JSONException e) {
      e.printStackTrace();
    }

  }

  //Tests removing authorized users from an account. Because of the PUT method and @Order tag, the test account should now be empty
  @Test
  @Order(4)
  public void removeAuthUserTest() {
    // Send request and receive response
    Response response = RestAssured.given().header("Content-Type", "plain/text").
            when().
            delete("/accounts/1/auth/1");

    int statusCode = response.getStatusCode();

    assertEquals(200, statusCode);

    //Check authuser
    response = RestAssured.given().header("Content-Type", "application/json").when().get("/users");
    String returnString = response.getBody().asString();

    try {
      JSONArray returnArr = new JSONArray(returnString);
      JSONObject returnObj = returnArr.getJSONObject(0);
      JSONArray authorizedUsers = returnObj.getJSONArray("authAccounts");
      assertEquals(0, authorizedUsers.length());
    } catch (JSONException e) {
      e.printStackTrace();
    }

  }

  //Adds user 1 to the list of auth users on the test account using put. Parsing the return of /accounts/1/auth for the relationship
  @Test
  @Order(5)
  public void putAuthorizedUserTest() {
    // Send request and receive response
    Response response = RestAssured.given().header("Content-Type", "plain/text").
            when().
            put("/accounts/1/auth/1");

    int statusCode = response.getStatusCode();

    assertEquals(200, statusCode);

    //Check authuser
    response = RestAssured.given().header("Content-Type", "application/json").when().get("/accounts/1/auth");
    String returnString = response.getBody().asString();

    try {
      JSONArray returnArr = new JSONArray(returnString);
      JSONObject returnObj = returnArr.getJSONObject(0);
      JSONArray authorizedUsers = returnObj.getJSONArray("authAccounts");
      JSONObject authorizedUser = authorizedUsers.getJSONObject(0);
      assertEquals(1, authorizedUser.getInt("accountNumber"));
    } catch (JSONException e) {
      e.printStackTrace();
    }

    RestAssured.given().header("Content-Type", "plain/text").when().delete("/accounts/1/auth/1");
  }

  //Tests POST method for accounts, verifying creation by checking the latest created account and then deleting it if found
  @Test
  public void accountCreateDeleteTest() {
    // Send request and receive response
    HashMap<String, Object> requestBody = new HashMap<String, Object>();
    requestBody.put("accountOwner", 1);
    requestBody.put("accountName", "Test Account");
    requestBody.put("accountType", "Testing");
    requestBody.put("balance", 999);
    Response response = RestAssured.given().header("Content-Type", "application/json").
            body(requestBody).
            when().
            post("/accounts");

    // Check status code
    int statusCode = response.getStatusCode();
    assertEquals(200, statusCode);

    int accountNum = 0;

    // Check response body for correct response
    response = RestAssured.given().header("Content-Type", "application/json").when().get("/accounts");
    String returnString = response.getBody().asString();
    try {
      JSONArray returnArr = new JSONArray(returnString);
      JSONObject returnObj = returnArr.getJSONObject(returnArr.length() - 1);
      assertEquals("Test Account", returnObj.getString("accountName"));
      assertEquals("Testing", returnObj.getString("accountType"));
      assertEquals(999, returnObj.getInt("balance"));

      accountNum = returnObj.getInt("accountNumber");
    } catch (JSONException e) {
      e.printStackTrace();
    }

    //We must have made a test account, delete it now
    response = RestAssured.given().header("Content-Type", "application/json").when().delete("/accounts/"+accountNum);
    statusCode = response.getStatusCode();

    assertEquals(200, statusCode);

    //Check response body for correct response
    //Hopefully this test runs fast enough that another account is not created in that time
    response = RestAssured.given().header("Content-Type", "application/json").when().get("/accounts");
    returnString = response.getBody().asString();
    try {
      JSONArray returnArr = new JSONArray(returnString);
      JSONObject returnObj = returnArr.getJSONObject(returnArr.length() - 1);
      assertNotEquals("Test Account", returnObj.getString("accountName"));
      assertNotEquals("Testing", returnObj.getString("accountType"));
      assertNotEquals(999, returnObj.getInt("balance"));
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }

}
