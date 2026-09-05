import { createBrowserRouter, RouterProvider } from "react-router-dom"
import Layout from "./components/Layout"
import HomeGate from "./pages/HomeGate"
import ListingFeedPage from "./pages/ListingFeedPage"
import ListingDetailPage from "./pages/ListingDetailPage"
import NewListingPage from "./pages/NewListingPage"
import MyListingsPage from "./pages/MyListingsPage"
import NotFound from "./pages/NotFound"
import EditListingPage from "./pages/EditListingPage"
import LoginPage from "./pages/LoginPage"
import RegisterPage from "./pages/RegisterPage"
import RequireAuth from "./components/RequireAuth"
import AboutPage from "./pages/AboutPage"

function AppRouter() {
  const routes = [
    { path: "/", element: <HomeGate /> },
    {
      element: <Layout />,
      children: [
        { path: "/about", element: <AboutPage /> },
        { path: "/feed", element: <ListingFeedPage /> },
        { path: "/listings/:listingId", element: <ListingDetailPage /> },
        { path: "/new-listing", element: <RequireAuth><NewListingPage /></RequireAuth> },
        { path: "/my-listings", element: <RequireAuth><MyListingsPage /></RequireAuth> },
        { path: "/listings/:listingId/edit", element: <RequireAuth><EditListingPage /></RequireAuth> },
        { path: "/login", element: <LoginPage /> },
        { path: "/register", element: <RegisterPage /> },
        { path: "*", element: <NotFound /> },
      ],
    },
  ]

  const router = createBrowserRouter(routes)
  return <RouterProvider router={router} />
}

export default AppRouter