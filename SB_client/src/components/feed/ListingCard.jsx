import { Link } from "react-router-dom";
import UrgencyBadge from "./UrgencyBadge";
import StatusBadge from "./StatusBadge";

function formatDate(dateString) {
  if (!dateString) return null;
  return new Date(dateString).toLocaleDateString("en-US", {
    month: "short",
    day: "numeric",
  });
}

export default function ListingCard({ listing }) {
  const isNeed = listing.type === "NEED";
  const deadline = isNeed ? listing.neededBy : listing.availableUntil;
  const deadlineLabel = isNeed ? "Needed by" : "Available thru";

  return (
    <Link
      to={`/listings/${listing.listingId}`}
      className="text-decoration-none text-reset"
    >
      <div className="card h-100 shadow-sm">
        <div className="card-body d-flex flex-column">
          <div className="d-flex justify-content-between align-items-start mb-2">
            <span
              className={`badge ${ isNeed
                  ? "text-bg-danger-subtle text-danger-emphasis"
                  : "text-bg-success-subtle text-success-emphasis"
              }`} > {isNeed ? "Need" : "Surplus"}
            </span> {isNeed && <UrgencyBadge urgency={listing.urgency} />}
          </div>

          <h2 className="h5 mb-1">
            {listing.quantity} unit{listing.quantity === 1 ? "" : "s"}
          </h2>
          <p className="text-muted small mb-3">  {listing.quantityRemaining} remaining</p>

          <div className="mt-auto d-flex justify-content-between align-items-center">
            <span className="small text-muted">
              {deadlineLabel} {formatDate(deadline) ?? "—"}
            </span>
            <StatusBadge status={listing.status} />
          </div>
        </div>
      </div>
    </Link>
  );
}