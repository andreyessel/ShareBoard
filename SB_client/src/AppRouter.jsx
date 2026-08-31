import { createBrowserRouter, RouterProvider } from "react-router-dom"
import Layout from "./components/Layout"
import ListingFeedPage from "./pages/ListingFeedPage"
import ListingDetailPage from "./pages/ListingDetailPage"
import NewListingPage from "./pages/NewListingPage"
import MyListingsPage from "./pages/MyListingsPage"
import NotFound from "./pages/NotFound"


function AppRouter() {
  const routes = [
    {
      path: "/",
      element: <Layout />,
      children: [
        { index: true, element: <ListingFeedPage /> },
        { path: "listings/:listingId", element: <ListingDetailPage /> },
        { path: "new-listing", element: <NewListingPage /> },
        { path: "my-listings", element: <MyListingsPage /> },
        { path: "*", element: <NotFound />},
      ],
    },
  ]

  const router = createBrowserRouter(routes)
  return <RouterProvider router={router} />
}

export default AppRouter
