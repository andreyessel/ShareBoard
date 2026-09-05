import { Link } from "react-router-dom";

export default function NotFound() {
  return (
    <div className="sb-notfound">
      <div className="sb-notfound-code">404</div>
      <h1 className="sb-notfound-title">This listing left the board.</h1>
      <p className="sb-notfound-sub">
        The page you're looking for doesn't exist, or it may have moved.
      </p>
      <Link to="/feed" className="btn btn-primary sb-btn-brand">
        Back to the feed
      </Link>
    </div>
  );
}