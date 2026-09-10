import { useState, useEffect } from "react";
import { NavLink, useLocation, useNavigate } from "react-router-dom";
import { useAuth } from "../context/useAuth";
import Logo from "../components/Logo";

function NavBar() {
  const { user, logout } = useAuth();
  const [showLogoutConfirm, setShowLogoutConfirm] = useState(false);
  const [showUserMenu, setShowUserMenu] = useState(false);
  const location = useLocation();
  const navigate = useNavigate();

  const isLandingStyle = location.pathname === "/about";

  const [scrollY, setScrollY] = useState(() =>
    typeof window !== "undefined" ? window.scrollY : 0
  );

  useEffect(() => {
    if (!isLandingStyle) return;
    const onScroll = () => setScrollY(window.scrollY);
    window.addEventListener("scroll", onScroll, { passive: true });
    return () => window.removeEventListener("scroll", onScroll);
  }, [isLandingStyle]);

  const scrolled = !isLandingStyle || scrollY > 40;

  function handleConfirmLogout() {
    logout();
    setShowLogoutConfirm(false);
    navigate("/about");
  }

  const initials = user?.username ? user.username.slice(0, 2).toUpperCase() : "";

  return (
    <nav
      className={`navbar navbar-expand-lg shadow-sm sb-navbar ${
        isLandingStyle && !scrolled
          ? "navbar-light sb-navbar--transparent"
          : "navbar-dark sb-navbar--solid"
      }`}
    >
      <div className="container">
        <NavLink className="navbar-brand fw-bold d-flex align-items-center gap-2" to="/">
          <Logo size={26} /> ShareBoard
        </NavLink>
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
                "nav-link" + (isActive ? " active fw-semibold" : "")} to="/feed"> Feed </NavLink>
            </li>
            <li className="nav-item">
              <NavLink className={({ isActive }) =>
                "nav-link" + (isActive ? " active fw-semibold" : "")} to="/about"> About </NavLink>
            </li>
            <li className="nav-item">
              <NavLink className={({ isActive }) => "nav-link" + (isActive ? " active fw-semibold" : "")}
                to="/my-listings"> My Listings </NavLink>
            </li>
          </ul>

          <div className="d-flex align-items-center gap-2">
            {user ? (
              <>
                <NavLink to="/new-listing" className="btn btn-warning fw-semibold">
                  + Post Listing
                </NavLink>
                <div className="sb-user-menu">
                  <button
                    className="sb-avatar-btn"
                    onClick={() => setShowUserMenu((v) => !v)}
                    aria-haspopup="true"
                    aria-expanded={showUserMenu}
                  >
                    <span className="sb-avatar">{initials}</span>
                  </button>
                  {showUserMenu && (
                    <div className="sb-user-dropdown">
                      <p className="sb-user-dropdown-name">{user.username}</p>
                      <button
                        className="sb-user-dropdown-item"
                        onClick={() => {
                          setShowUserMenu(false);
                          setShowLogoutConfirm(true);
                        }}
                      >
                        Log Out
                      </button>
                    </div>
                  )}
                </div>
              </>
            ) : (
              <>
                <NavLink to="/login" className="btn btn-outline-light">Log In</NavLink>
                <NavLink to="/register" className="btn btn-warning fw-semibold">Sign Up</NavLink>
              </>
            )}
          </div>
        </div>
      </div>
      {showLogoutConfirm && (
        <>
          <div className="modal d-block" tabIndex="-1" role="dialog">
            <div className="modal-dialog">
              <div className="modal-content">
                <div className="modal-header">
                  <h5 className="modal-title">Log out?</h5>
                  <button type="button" className="btn-close" onClick={() => setShowLogoutConfirm(false)}></button>
                </div>
                <div className="modal-body">
                  <p>You'll need to log back in to post or manage listings.</p>
                </div>
                <div className="modal-footer">
                  <button className="btn btn-outline-secondary" onClick={() => setShowLogoutConfirm(false)}>Cancel</button>
                  <button className="btn btn-danger" onClick={handleConfirmLogout}>Log Out</button>
                </div>
              </div>
            </div>
          </div>
          <div className="modal-backdrop show"></div>
        </>
      )}
    </nav>
  );
}

export default NavBar;