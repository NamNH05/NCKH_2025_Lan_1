import axiosClient from "./axiosClient";

export const login = (data) =>
  axiosClient.post("/users/login", data);

export const getProfile = () =>
  axiosClient.get("/users/me");
