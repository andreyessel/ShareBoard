import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../context/useAuth";

function RegisterPage() {
  const [form, setForm] = useState({ username: "", passwordHash: "", organization: false });
  const [errors, setErrors] = useState([]);
  const { login } = useAuth();
  const navigate = useNavigate();

  function handleChange(event) {
    const { name, type, value, checked } = event.target;
    setForm({ ...form, [name]: type === "checkbox" ? checked : value });
  }

  async function handleSubmit(event) {
    event.preventDefault();
    setErrors([]);

    const response = await fetch("http://localhost:8080/api/auth/register", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(form),
    });

    if (response.ok) {
      login(await response.json());
      navigate("/");
    } else if (response.status === 400) {
      setErrors(await response.json());
    } else {
      setErrors(["Something went wrong. Please try again."]);
    }
  }

  return (
    <div className="py-5" style={{ maxWidth: "400px", margin: "0 auto" }}>
      <h1 className="h3 mb-4 text-center">Sign Up</h1>
      <form onSubmit={handleSubmit}>
        <div className="mb-3">
          <label className="form-label" htmlFor="username">Username</label>
          <input
            className="form-control"
            id="username"
            name="username"
            value={form.username}
            onChange={handleChange}
            required
          />
        </div>
        <div className="mb-3">
          <label className="form-label" htmlFor="passwordHash">Password</label>
          <input
            type="password"
            className="form-control"
            id="passwordHash"
            name="passwordHash"
            value={form.passwordHash}
            onChange={handleChange}
            required
          />
        </div>
        <div className="mb-3 form-check">
          <input
            type="checkbox"
            className="form-check-input"
            id="organization"
            name="organization"
            checked={form.organization}
            onChange={handleChange}
          />
          <label className="form-check-label" htmlFor="organization">
            I'm registering as an organization
          </label>
        </div>
        {errors.length > 0 && (
          <div className="alert alert-danger py-2">
            <ul className="mb-0">{errors.map((e) => <li key={e}>{e}</li>)}</ul>
          </div>
        )}
        <button type="submit" className="btn btn-primary w-100">Sign Up</button>
      </form>
      <p className="text-center mt-3 small">
        Already have an account? <Link to="/login">Log in</Link>
      </p>
    </div>
  );
}

export default RegisterPage;