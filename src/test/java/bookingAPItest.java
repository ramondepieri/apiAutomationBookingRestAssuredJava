import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;
import io.restassured.response.ValidatableResponse;
import org.hamcrest.Matchers;
import org.testng.annotations.Test;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.TimeZone;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class bookingAPItest {

    @Test
    public void getBookingsTest() {
        //BaseURL
        RestAssured.baseURI = "https://automationintesting.online/booking";

        // action
        ValidatableResponse response = given().when().get("/").then();

        // validation that is bigger or equal 2
        assertThat(response.extract().jsonPath().getList("bookings").size(), Matchers.greaterThanOrEqualTo(2));

        // validation status code and content type
        response.assertThat().statusCode(200).and().contentType(ContentType.JSON);
    }

    @Test
    public void getBookingTest() {
        // BaseURL
        RestAssured.baseURI = "https://automationintesting.online/booking";

        // action
        ValidatableResponse response = given().when().get("/1").then();

        // validation of data returned
        Booking booking = response.extract().as(Booking.class);
        assertThat(booking.bookingid, equalTo(1l));
        assertThat(booking.roomid, equalTo(1l));
        assertThat(booking.firstname, equalTo("James"));
        assertThat(booking.lastname, equalTo("Dean"));
        assertThat(booking.depositpaid, equalTo(true));
        assertThat(booking.bookingdates.checkin, equalTo("2019-01-01"));
        assertThat(booking.bookingdates.checkout, equalTo("2019-01-05"));
    }

    @Test
    public void createBookingTest(){
        // BaseURL
        RestAssured.baseURI = "https://automationintesting.online/booking";

        // data
        BookingDates bookingDates = new BookingDates(getDateToday(), getDateInFuture(3));
        Booking booking = new Booking("Ramon", "Saraiva", "99999999999", "ramon@gmail.com", getRandomRoom(), bookingDates, true);

        // action and validation (check that the status code is 201 - created)
        ValidatableResponse response =  given()
                .contentType("application/json")
                .body(booking)
        .when()
                .post("/")
        .then()
                .assertThat().statusCode(201);
    }

    private String getDateToday(){
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        return sdf.format(cal.getTime());
    }

    private String getDateInFuture(int days){
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        cal.add(Calendar.DAY_OF_MONTH,days);
        return sdf.format(cal.getTime());
    }

    private Long getRandomRoom(){
        Random random = new Random();
        int roomId = random.nextInt(1000000);
        return new Long(roomId);
    }

}
