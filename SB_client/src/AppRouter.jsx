import { createBrowserRouter, RouterProvider } from "react-router-dom"
import Layout from "./components/Layout"
import ListingFeedPage from "./components/ListingFeedPage"
import ListingDetailPage from "./components/ListingDetailPage"
import NewListingPage from "./components/NewListingPage"
import MyListingsPage from "./components/MyListingsPage"


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
      ],
    },
  ]

  const router = createBrowserRouter(routes)
  return <RouterProvider router={router} />
}

export default AppRouter
