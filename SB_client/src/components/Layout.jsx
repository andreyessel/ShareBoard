import { Outlet } from "react-router-dom"
import NavBar from "../pages/NavBar"

function Layout() {
  return (
    <>
      <NavBar />
       <main className="sb-main">
      <Outlet />
      </main>
    </>
  )
}

export default Layout