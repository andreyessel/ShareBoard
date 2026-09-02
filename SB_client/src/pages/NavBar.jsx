import { NavLink } from "react-router-dom";

function NavBar() {
  return (
    <nav className="navbar navbar-expand-lg navbar-dark bg-dark shadow-sm">
      <div className="container">
        <NavLink className="navbar-brand fw-bold" to="/"> ShareBoard</NavLink>

        <button
          className="navbar-toggler"
          type="button"
          data-bs-toggle="collapse"
          data-bs-target="#navMenu"
          aria-controls="navMenu"
          aria-expanded="false"
          aria-label="Toggle navigation"
        >
          <span className="navbar-toggler-icon"></span>
        </button>

        <div className="collapse navbar-collapse" id="navMenu">
          <ul className="navbar-nav me-auto">
            <li className="nav-item">
              <NavLink className={({ isActive }) =>
                  "nav-link" + (isActive ? " active fw-semibold" : "") } to="/" > Feed </NavLink>
            </li>
            <li className="nav-item">
              <NavLink className={({ isActive }) =>"nav-link" + (isActive ? " active fw-semibold" : "") }
                to="/my-listings" > My Listings </NavLink>
            </li>
          </ul>

          <NavLink to="/new-listing" className="btn btn-warning fw-semibold">
            + Post Listing
          </NavLink>
        </div>
      </div>
    </nav>
  );
}

export default NavBar;