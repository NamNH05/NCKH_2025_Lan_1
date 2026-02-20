import { useState } from "react";
import axios from "axios";
import logger from "../../utils/logger";;
import "./TestAPI.css";

export default function TestAPI() {
  const [results, setResults] = useState([]);
  const [loading, setLoading] = useState(false);

  const runTests = async () => {
    setLoading(true);
    setResults([]);
    const newResults = [];

    try {
      // Test 1: Register
      console.log("Starting register test...");
      newResults.push({
        name: "Register User",
        status: "testing",
      });
      setResults([...newResults]);

      console.log("Sending register request to /api/auth/register");
      const registerRes = await axios.post("/api/auth/register", {
        username: "testuser" + Date.now(),
        email: `test${Date.now()}@example.com`,
        password: "password123",
        fullName: "Test User",
        phone: "0123456789",
      });

      newResults[newResults.length - 1] = {
        name: "Register User",
        status: "success",
        data: registerRes.data,
      };
      setResults([...newResults]);

      // Test 2: Login
      newResults.push({
        name: "Login User",
        status: "testing",
      });
      setResults([...newResults]);

      const loginRes = await axios.post("/api/auth/login", {
        username: registerRes.data.username,
        password: "password123",
      });

      const token = loginRes.data.token;
      newResults[newResults.length - 1] = {
        name: "Login User",
        status: "success",
        data: loginRes.data,
      };
      setResults([...newResults]);

      // Test 3: Validate Token
      newResults.push({
        name: "Validate Token",
        status: "testing",
      });
      setResults([...newResults]);

      const validateRes = await axios.post(
        "/api/auth/validate",
        {},
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );

      newResults[newResults.length - 1] = {
        name: "Validate Token",
        status: "success",
        data: validateRes.data,
      };
      setResults([...newResults]);

      // Test 4: Get Current User
      newResults.push({
        name: "Get User Info",
        status: "testing",
      });
      setResults([...newResults]);

      const userRes = await axios.get("/api/users/1", {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });

      newResults[newResults.length - 1] = {
        name: "Get User Info",
        status: "success",
        data: userRes.data,
      };
      setResults([...newResults]);
    } catch (error) {
      console.error("Test error:", error);
      console.error("Error response:", error.response);
      console.error("Error config:", error.config);
      const lastResult = newResults[newResults.length - 1];
      if (lastResult) {
        lastResult.status = "error";
        lastResult.error = error.response?.data || error.message;
      }
      newResults.push({
        name: "Error",
        status: "error",
        error: error.message,
      });
    }

    setLoading(false);
  };

  return (
    <div className="test-api-container">
      <h1>API Connection Test</h1>
      <button onClick={runTests} disabled={loading} className="test-btn">
        {loading ? "Testing..." : "Run Tests"}
      </button>

      <div className="results">
        {results.map((result, index) => (
          <div key={index} className={`result-item ${result.status}`}>
            <div className="result-name">{result.name}</div>
            <div className="result-status">{result.status}</div>
            {result.data && (
              <pre className="result-data">{JSON.stringify(result.data, null, 2)}</pre>
            )}
            {result.error && <div className="result-error">{result.error}</div>}
          </div>
        ))}
      </div>
    </div>
  );
}
