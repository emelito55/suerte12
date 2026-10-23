package com.scc.model;

/** One row of the users table. */
public record User(int id, String username, String password, String roles, String status) {
}