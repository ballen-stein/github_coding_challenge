package com.example.githubcodingchallenge.model;

public class OrganizationRepoCount {
    private String name;
    private Integer public_repos;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPublic_repos() {
        return public_repos;
    }

    public void setPublic_repos(Integer public_repos) {
        this.public_repos = public_repos;
    }
}
