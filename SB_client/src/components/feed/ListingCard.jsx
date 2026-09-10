import { Link } from "react-router-dom";
import UrgencyBadge from "./UrgencyBadge";
import StatusBadge from "./StatusBadge";
import { ClockIcon, PackageIcon } from "../icons/MiniIcons";

function formatDate(dateString) {
  if (!dateString) return null;
  return new Date(dateString).toLocaleDateString("en-US", {
    month: "short",
    day: "numeric",
  });
}

export default function ListingCard({ listing, justMatched}) {
  const isNeed = listing.type === "NEED";
  const isDone = listing.status === "MATCHED";
  const deadline = isNeed ? listing.neededBy : listing.availableUntil;
  const deadlineLabel = isNeed ? "by" : "thru";
  const pctMatched = listing.quantity > 0
    ? Math.round(((listing.quantity - listing.quantityRemaining) / listing.quantity) * 100)
    : 0;

  return (
    <Link
      to={`/listings/${listing.listingId}`}
      className="text-decoration-none text-reset sb-card-link"
    >
      <div
        className={`sb-listing-card ${
          isDone ? "sb-listing-card--done" : isNeed ? "sb-listing-card--need" : "sb-listing-card--surplus"
        } ${justMatched ? "match-flash" : ""}`}
      >
        <div className="sb-listing-card-body">
          <div className="d-flex justify-content-between align-items-start mb-2">
            <div>
              <h2 className="sb-listing-title">
                {listing.quantity} unit{listing.quantity === 1 ? "" : "s"}
              </h2>
              <p className="sb-listing-owner">
                {listing.ownerUsername}
                {listing.ownerIsOrganization && (
                  <span className="sb-verified-badge" title="Verified organization">✓ Org</span>
                )}
              </p>
            </div>
            {isDone ? (
              <span className="sb-done-badge">Done</span>
            ) : isNeed ? (
              <UrgencyBadge urgency={listing.urgency} />
            ) : (
              <StatusBadge status={listing.status} />
            )}
          </div>

          <p className="sb-listing-category">{listing.categoryName}</p>

          <div className="sb-progress-row">
            <div className="sb-remaining-track" aria-hidden="true">
              <div
                className={`sb-remaining-fill ${isDone ? "sb-remaining-fill--done" : isNeed ? "sb-remaining-fill--need" : "sb-remaining-fill--surplus"}`}
                style={{ width: `${pctMatched}%` }}
              />
            </div>
            <span className="sb-progress-label">{pctMatched}% matched</span>
          </div>

          <div className="sb-listing-meta">
            <span className="sb-listing-meta-item">
              <PackageIcon /> {listing.quantityRemaining} {isNeed ? "needed" : "available"}
            </span>
            <span className="sb-listing-meta-item">
              <ClockIcon /> {deadlineLabel} {formatDate(deadline) ?? "—"}
            </span>
          </div>

          {isNeed && !isDone && (
            <div className="sb-listing-footer">
              <StatusBadge status={listing.status} />
            </div>
          )}
        </div>
      </div>
    </Link>
  );
}