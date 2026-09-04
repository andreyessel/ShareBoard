import { Navigate } from "react-router-dom";
import { useAuth } from "../context/useAuth";
import LandingPage from "./LandingPage";

function HomeGate() {
  const { user } = useAuth();

  if (user) {
    return <Navigate to="/feed" replace />;
  }

  return <LandingPage />;
}

export default HomeGate;