import { Outlet } from "react-router-dom"
import NavBar from "../pages/NavBar"

function Layout() {
  return (
    <>
      <NavBar />
       <main className="container-fluid px-3 px-md-4 px-lg-5">
      <Outlet />
      </main>
    </>
  )
}

export default Layout