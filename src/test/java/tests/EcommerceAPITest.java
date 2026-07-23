package tests;

import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.path.json.JsonPath;
import io.restassured.specification.ResponseSpecification;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.Test;
import resources.pojo.ecommerce.CreateOrderRequest;
import resources.pojo.ecommerce.CreateOrderResponse;
import resources.pojo.ecommerce.LoginRequest;
import resources.pojo.ecommerce.LoginResponse;
import resources.pojo.ecommerce.Order;

import java.io.File;
import java.util.ArrayList;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

public class EcommerceAPITest {
    private static final String BASE_URI = "https://rahulshettyacademy.com";
    private static final String LOGIN_ENDPOINT = "/api/ecom/auth/login";
    private static final String ADD_PRODUCT_ENDPOINT = "/api/ecom/product/add-product";
    private static final String CREATE_ORDER_ENDPOINT = "/api/ecom/order/create-order";
    private static final String DELETE_ORDER_ENDPOINT = "/api/ecom/order/delete-order/{orderId}";
    private static final String DELETE_PRODUCT_ENDPOINT = "/api/ecom/product/delete-product/{productID}";

    private static final String USER_EMAIL = System.getProperty("ecom.user.email", "dungchungonline@gmail.com");
    private static final String USER_PASSWORD = System.getProperty("ecom.user.password", "123qwe");
    private static final String PRODUCT_NAME = "Hanh Shirt";
    private static final String PRODUCT_CATEGORY = "fashion";
    private static final String PRODUCT_SUB_CATEGORY = "shirts";
    private static final int PRODUCT_PRICE = 11500;
    private static final String PRODUCT_DESCRIPTION = "Adias Originals";
    private static final String PRODUCT_FOR = "women";
    private static final String ORDER_COUNTRY = "United States";
    private static final String PRODUCT_IMAGE_PATH = System.getProperty(
            "ecom.product.image.path",
            System.getProperty("user.home") + "\\Desktop\\Untitled.png"
    );

    private static final ResponseSpecification STATUS_OK = new ResponseSpecBuilder().expectStatusCode(200).build();
    private static final ResponseSpecification STATUS_CREATED = new ResponseSpecBuilder().expectStatusCode(201).build();

    @Test
    public void endToEndTest() {
        LoginResponse loginResponse = login(USER_EMAIL, USER_PASSWORD);
        assertNonBlank(loginResponse.getToken(), "Login token must be returned");
        assertNonBlank(loginResponse.getUserId(), "User ID must be returned");

        String productId = null;
        String orderId = null;
        try {
            productId = addProduct(loginResponse.getToken(), loginResponse.getUserId());
            assertNonBlank(productId, "Product ID must be returned");

            orderId = createOrder(loginResponse.getToken(), productId);
            assertNonBlank(orderId, "Order ID must be returned");
        } finally {
            if (orderId != null) {
                deleteOrder(loginResponse.getToken(), orderId);
            }
            if (productId != null) {
                deleteProduct(loginResponse.getToken(), productId);
            }
        }
    }

    private void assertNonBlank(String value, String message) {
        assertNotNull(value, message);
        assertFalse(value.trim().isEmpty(), message);
    }

    private LoginResponse login(String email, String password) {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUserEmail(email);
        loginRequest.setUserPassword(password);

        return given()
                .spec(baseRequestSpec())
                .contentType(ContentType.JSON)
                .body(loginRequest)
                .when()
                .post(LOGIN_ENDPOINT)
                .then()
                .spec(STATUS_OK)
                .extract()
                .as(LoginResponse.class);
    }

    private String addProduct(String token, String userId) {
        File productImage = new File(PRODUCT_IMAGE_PATH);
        assertTrue(productImage.exists(), "Product image file does not exist: " + PRODUCT_IMAGE_PATH);

        String responseStr = given()
                .spec(authorizedRequestSpec(token))
                .formParam("productName", PRODUCT_NAME)
                .formParam("productAddedBy", userId)
                .formParam("productCategory", PRODUCT_CATEGORY)
                .formParam("productSubCategory", PRODUCT_SUB_CATEGORY)
                .formParam("productPrice", PRODUCT_PRICE)
                .formParam("productDescription", PRODUCT_DESCRIPTION)
                .formParam("productFor", PRODUCT_FOR)
                .multiPart("productImage", productImage)
                .when()
                .post(ADD_PRODUCT_ENDPOINT)
                .then()
                .spec(STATUS_CREATED)
                .body("message", equalTo("Product Added Successfully"))
                .extract()
                .asString();

        return JsonPath.from(responseStr).getString("productId");
    }

    private String createOrder(String token, String productId) {
        Order order = new Order();
        order.setCountry(ORDER_COUNTRY);
        order.setProductOrderedId(productId);

        ArrayList<Order> orders = new ArrayList<>();
        orders.add(order);

        CreateOrderRequest createOrderRequest = new CreateOrderRequest();
        createOrderRequest.setOrders(orders);

        CreateOrderResponse createOrderResponse = given()
                .spec(authorizedRequestSpec(token))
                .contentType(ContentType.JSON)
                .body(createOrderRequest)
                .when()
                .post(CREATE_ORDER_ENDPOINT)
                .then()
                .spec(STATUS_CREATED)
                .body("message", equalTo("Order Placed Successfully"))
                .extract()
                .as(CreateOrderResponse.class);

        return createOrderResponse.getOrders().get(0);
    }

    private void deleteOrder(String token, String orderId) {
        given()
                .spec(authorizedRequestSpec(token))
                .pathParam("orderId", orderId)
                .when()
                .delete(DELETE_ORDER_ENDPOINT)
                .then()
                .spec(STATUS_OK)
                .body("message", equalTo("Orders Deleted Successfully"));
    }

    private void deleteProduct(String token, String productId) {
        given()
                .spec(authorizedRequestSpec(token))
                .pathParam("productID", productId)
                .when()
                .delete(DELETE_PRODUCT_ENDPOINT)
                .then()
                .spec(STATUS_OK)
                .body("message", equalTo("Product Deleted Successfully"));
    }

    private RequestSpecification baseRequestSpec() {
        return new RequestSpecBuilder()
                .setBaseUri(BASE_URI)
                .build();
    }

    private RequestSpecification authorizedRequestSpec(String token) {
        return new RequestSpecBuilder()
                .setBaseUri(BASE_URI)
                .addHeader("Authorization", token)
                .build();
    }
}
