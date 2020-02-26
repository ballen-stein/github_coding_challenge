package com.example.githubcodingchallenge;

import org.junit.Test;

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
    public void api_split_test(){
        String repoInfo1 = "square/retrofit";
        String repoInfo2 = "bma33/android_projects";
        String repoInfo3 = "airbnb/exampleProject";
        String[] repoDetails = repoInfo1.split("/", 2);
        assertEquals(repoDetails[0], "square");
        assertEquals(repoDetails[1], "android_projects");
    }
}