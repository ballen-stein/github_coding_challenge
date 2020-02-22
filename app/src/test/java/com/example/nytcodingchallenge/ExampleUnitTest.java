package com.example.nytcodingchallenge;

import com.example.nytcodingchallenge.apai_connections.ApiCall;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }


    @Test
    public void api_call_test(){
        ApiCall apiCall = new ApiCall();
        String responseString = "";
        try {
            responseString = apiCall.getResponse("https://api.github.com");
        } catch (IOException e) {
            e.printStackTrace();
        }

        assertEquals("n/a", responseString);
    }
}