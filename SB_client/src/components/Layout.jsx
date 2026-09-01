import { Outlet } from "react-router-dom"
import NavBar from "../pages/NavBar"

function Layout() {
  return (
    <>
      <NavBar />
      <Outlet />
    </>
  )
}

export default Layout