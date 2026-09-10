import { Outlet, useLocation } from "react-router-dom"
import NavBar from "../pages/NavBar"

function Layout() {
  const location = useLocation();
  return (
    <>
      <NavBar />
      <main className="sb-main">
        <div key={location.pathname} className="sb-page-transition">
          <Outlet />
        </div>
      </main>
    </>
  )
}

export default Layout