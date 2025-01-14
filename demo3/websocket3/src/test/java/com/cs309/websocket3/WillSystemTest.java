package com.cs309.websocket3;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.cs309.websocket3.Users.User;
import com.cs309.websocket3.Budget.Budget;
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
public class WillSystemTest {

    @LocalServerPort
    int port;

    @Before
    public void setUp() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
    }

    @Test
    @Order(1)
    public void userPutTest() {
        // Send request and receive response for out method that changes password
        HashMap<String, Object> requestBody = new HashMap<String, Object>();
        requestBody.put("username", "ratman");
        requestBody.put("password", "passing");
        requestBody.put("address", "here");
        requestBody.put("phone", "100");
        Response response = RestAssured.given().
                header("Content-Type", "application/json").
                body(requestBody).
                when().
                put("/users/johnny");


        // Check status code
        int statusCode = response.getStatusCode();
        assertEquals(200, statusCode);

        // Check response body for correct response
        String returnString = response.getBody().asString();
        try {
            JSONObject returnObj = new JSONObject(returnString);
            assertEquals("ratman", returnObj.get("username"));
            assertEquals("passing", returnObj.get("password"));
            assertEquals("here", returnObj.get("address"));
            assertEquals("100", returnObj.get("phone"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    @Test
    @Order(2)
    public void createBudgetTest() {
        // Send request to create a budget
        HashMap<String, Object> requestBody = new HashMap<>();
        requestBody.put("expenses", 0.0);   // Actual expenses
        requestBody.put("income", 500.0);
        requestBody.put("expexp", 150.0);     // Expected expenses (this is provided)
        // Do not need to send 'expdif' as it will be calculated
        requestBody.put("expdif", null);      // Explicitly send null, assuming it's calculated on the backend

        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(requestBody)
                .when()
                .post("/budget");

        // Check status code
        int statusCode = response.getStatusCode();
        assertEquals(201, statusCode);

        // Check response body for correct response
        String returnString = response.getBody().asString();
        try {
            JSONObject returnObj = new JSONObject(returnString);
            assertEquals("success", returnObj.get("message"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Test
    @Order(3)
    public void getBudgetByUserIdTest() {
        // Ensure the budget exists with correct values first
        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .when()
                .get("/budget/1");

        int statusCode = response.getStatusCode();
        assertEquals(200, statusCode);

        // Fetch the initial values
        String returnString = response.getBody().asString();
        try {
            JSONObject returnObj = new JSONObject(returnString);
            assertEquals(477.0, returnObj.getDouble("expenses")); // initial value
            assertEquals(15.0, returnObj.getDouble("income")); // initial value
            assertEquals(527.0, returnObj.getDouble("expexp")); // initial value
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Test
    @Order(4)
    public void updateExpensesTest() {
        // First, set up the initial known values if needed (create or reset the budget).
        HashMap<String, Object> requestBody = new HashMap<>();
        requestBody.put("expenses", 100.0);
        requestBody.put("income", 500.0);
        requestBody.put("expexp", 150.0);

        // POST to create the initial budget if it does not exist
        RestAssured.given()
                .header("Content-Type", "application/json")
                .body(requestBody)
                .when()
                .post("/budget");

        // Now send a PUT request to update the expenses for the budget with user ID 1
        HashMap<String, Object> updateBody = new HashMap<>();
        updateBody.put("expenses", 50.0);  // Update expenses
        updateBody.put("income", 500.0);
        updateBody.put("expexp", 150.0);

        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(updateBody)
                .when()
                .put("/budget/1");

        // Check status code
        int statusCode = response.getStatusCode();
        assertEquals(200, statusCode);

        // Check response body
        String returnString = response.getBody().asString();
        try {
            JSONObject returnObj = new JSONObject(returnString);
            assertEquals("success", returnObj.get("message"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Verify updated budget
        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .when()
                .get("/budget/1");

        String updatedReturnString = response.getBody().asString();
        try {
            JSONObject updatedReturnObj = new JSONObject(updatedReturnString);
            assertEquals(50.0, updatedReturnObj.getDouble("expenses"));  // Updated value
            assertEquals(500.0, updatedReturnObj.getDouble("income"));
            assertEquals(150.0, updatedReturnObj.getDouble("expexp"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Test
    @Order(5)
    public void budgetNotFoundTest() {
        // Send GET request to a non-existent budget
        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .when()
                .get("/budget/999");

        // Check status code
        int statusCode = response.getStatusCode();
        assertEquals(404, statusCode);
    }

    @Test
    @Order(6)
    public void ticketAssignTest() {
        // Send request and receive response for out method that changes password
        HashMap<String, Object> requestBody = new HashMap<String, Object>();
        requestBody.put("username", "ratman");
        requestBody.put("body", "passing");
        requestBody.put("date", "today");
        requestBody.put("adminId", null);
        Response response = RestAssured.given().
                header("Content-Type", "application/json").
                body(requestBody).
                when().
                put("/tickets/1");


        // Check status code
        int statusCode = response.getStatusCode();
        assertEquals(200, statusCode);

        // Check response body for correct response
        String returnString = response.getBody().asString();
        try {
            JSONObject returnObj = new JSONObject(returnString);
            assertEquals("ratman", returnObj.get("username"));
            assertEquals("body", returnObj.get("password"));
            assertEquals("today", returnObj.get("date"));
            assertEquals(null, returnObj.get("adminId"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    @Test
    @Order(7)
    public void getAllUsersTest() {
        // Send GET request to fetch all users
        Response response = RestAssured.given()
                .when()
                .get("/users");

        // Assert response status and body
        assertEquals(200, response.getStatusCode());
        assertEquals(true, response.getBody().asString().contains("johnny"));
    }
    @Test
    @Order(8)
    public void getUserByUsernameTest() {
        // Send GET request to retrieve user by username
        Response response = RestAssured.given()
                .when()
                .get("/users/johnny");

        // Assert response status and username
        assertEquals(200, response.getStatusCode());
        assertEquals("johnny", response.jsonPath().getString("username"));
    }

    @Test
    @Order(9)
    public void userNotFoundTest() {
        // Send GET request to retrieve a non-existent user
        Response response = RestAssured.given()
                .when()
                .get("/users/nonexistent");

        // Assert response status and failure message
        assertEquals(404, response.getStatusCode());
    }
    @Test
    @Order(10)
    public void updateUserTest() {
        // Prepare request body to update user
        String requestBody = "{ \"password\": \"newpassword123\" }";

        // Send PUT request to update user
        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(requestBody)
                .when()
                .put("/users/johnny");

        // Assert response status and updated data
        assertEquals(200, response.getStatusCode());
        assertEquals("johnny", response.jsonPath().getString("username"));
        assertEquals("newpassword123", response.jsonPath().getString("password"));
    }

    @Test
    @Order(11)
    public void updateUserNotFoundTest() {
        // Send PUT request for non-existent user
        String requestBody = "{ \"password\": \"newpassword123\" }";
        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(requestBody)
                .when()
                .put("/users/nonexistent");

        // Assert response status for not found
        assertEquals(404, response.getStatusCode());
    }
    @Test
    @Order(12)
    public void deleteUserTest() {
        // Send DELETE request to delete user by username
        Response response = RestAssured.given()
                .when()
                .delete("/users/johnny");

        // Assert response status
        assertEquals(200, response.getStatusCode());
        assertEquals("{\"message\":\"success\"}", response.getBody().asString());
    }

    @Test
    @Order(13)
    public void deleteUserNotFoundTest() {
        // Send DELETE request for a non-existent user
        Response response = RestAssured.given()
                .when()
                .delete("/users/nonexistent");

        // Assert response status for not found
        assertEquals(404, response.getStatusCode());
    }
    @Test
    @Order(14)
    public void getAllAdminsTest() {
        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .when()
                .get("/admins");

        int statusCode = response.getStatusCode();
        assertEquals(200, statusCode);

        String returnString = response.getBody().asString();
        try {
            JSONArray returnArray = new JSONArray(returnString);
            assertNotNull(returnArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Test
    @Order(15)
    public void getAdminByIdTest() {
        int employeeId = 1; // Replace with a valid employee ID

        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .when()
                .get("/admins/" + employeeId);

        int statusCode = response.getStatusCode();
        if (statusCode == 200) {
            String returnString = response.getBody().asString();
            try {
                JSONObject returnObj = new JSONObject(returnString);
                assertEquals(employeeId, returnObj.getInt("employeeId"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            assertEquals(404, statusCode);
        }
    }

    @Test
    @Order(16)
    public void createAdminTest() {
        HashMap<String, Object> requestBody = new HashMap<>();
        requestBody.put("employeeId", 2); // Replace with a unique employee ID
        requestBody.put("password", "password123");

        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(requestBody)
                .when()
                .post("/admins");

        int statusCode = response.getStatusCode();
        assertEquals(201, statusCode);

        String returnString = response.getBody().asString();
        try {
            JSONObject returnObj = new JSONObject(returnString);
            assertEquals("success", returnObj.get("message"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Test
    @Order(17)
    public void updateAdminTest() {
        int employeeId = 1; // Replace with a valid employee ID
        HashMap<String, Object> requestBody = new HashMap<>();
        requestBody.put("password", "newpassword123");

        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(requestBody)
                .when()
                .put("/admins/" + employeeId);

        int statusCode = response.getStatusCode();
        if (statusCode == 200) {
            String returnString = response.getBody().asString();
            try {
                JSONObject returnObj = new JSONObject(returnString);
                assertEquals(employeeId, returnObj.getInt("employeeId"));
                assertEquals("newpassword123", returnObj.getString("password"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            assertEquals(404, statusCode);
        }
    }

    @Test
    @Order(18)
    public void deleteAdminTest() {
        int employeeId = 2; // Replace with a valid employee ID

        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .when()
                .delete("/admins/" + employeeId);

        int statusCode = response.getStatusCode();
        assertEquals(200, statusCode);

        String returnString = response.getBody().asString();
        try {
            JSONObject returnObj = new JSONObject(returnString);
            assertEquals("success", returnObj.get("message"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    @Test
    @Order(19)
    public void getAllTicketsTest() {
        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .when()
                .get("/tickets");

        int statusCode = response.getStatusCode();
        assertEquals(200, statusCode);

        String returnString = response.getBody().asString();
        try {
            JSONArray returnArray = new JSONArray(returnString);
            assertNotNull(returnArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Test
    @Order(20)
    public void getTicketByNumberTest() {
        int ticketNumber = 1; // Replace with a valid ticket number

        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .when()
                .get("/tickets/" + ticketNumber);

        int statusCode = response.getStatusCode();
        if (statusCode == 200) {
            String returnString = response.getBody().asString();
            try {
                JSONObject returnObj = new JSONObject(returnString);
                assertEquals(ticketNumber, returnObj.getInt("ticketNumber"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            assertEquals(404, statusCode);
        }
    }

    @Test
    @Order(21)
    public void getTicketsByAdminIdTest() {
        Long adminId = 1L; // Replace with a valid admin ID

        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .when()
                .get("/tickets/admin/" + adminId);

        int statusCode = response.getStatusCode();
        if (statusCode == 200) {
            String returnString = response.getBody().asString();
            try {
                JSONArray returnArray = new JSONArray(returnString);
                assertNotNull(returnArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            assertEquals(404, statusCode);
        }
    }

    @Test
    @Order(22)
    public void createTicketTest() {
        HashMap<String, Object> requestBody = new HashMap<>();
        requestBody.put("ticketNumber", 2); // Replace with a unique ticket number
        requestBody.put("description", "Test ticket description");

        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(requestBody)
                .when()
                .post("/tickets");

        int statusCode = response.getStatusCode();
        assertEquals(201, statusCode);

        String returnString = response.getBody().asString();
        try {
            JSONObject returnObj = new JSONObject(returnString);
            assertEquals("success", returnObj.get("message"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Test
    @Order(23)
    public void updateTicketTest() {
        int ticketNumber = 1; // Replace with a valid ticket number
        HashMap<String, Object> requestBody = new HashMap<>();
        requestBody.put("description", "Updated ticket description");

        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(requestBody)
                .when()
                .put("/tickets/" + ticketNumber);

        int statusCode = response.getStatusCode();
        if (statusCode == 200) {
            String returnString = response.getBody().asString();
            try {
                JSONObject returnObj = new JSONObject(returnString);
                assertEquals(ticketNumber, returnObj.getInt("ticketNumber"));
                assertEquals("Updated ticket description", returnObj.getString("description"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            assertEquals(404, statusCode);
        }
    }

    @Test
    @Order(24)
    public void deleteTicketTest() {
        int ticketNumber = 2; // Replace with a valid ticket number

        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .when()
                .delete("/tickets/" + ticketNumber);

        int statusCode = response.getStatusCode();
        assertEquals(200, statusCode);

        String returnString = response.getBody().asString();
        try {
            JSONObject returnObj = new JSONObject(returnString);
            assertEquals("success", returnObj.get("message"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
