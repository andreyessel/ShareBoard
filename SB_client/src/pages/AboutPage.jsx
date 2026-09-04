import { useAuth } from "../context/useAuth";
import LandingPage from "./LandingPage";


function AboutPage() {
  const { user } = useAuth();
  return <LandingPage hideAuthLinks={!!user} />;
}

export default AboutPage;