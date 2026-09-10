import { useState, useEffect } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import { Fragment } from "react";
import { useAuth } from "../context/useAuth";

const INITIAL_LISTING = {
  categoryId: "",
  type: "NEED",
  quantity: "",
  urgency: "MEDIUM",
  neededBy: "",
  availableUntil: "",
  anonymous: false,
};

function NewListingPage() {
   const { user } = useAuth();
  const location = useLocation();
  const prefill = location.state?.prefill ?? null;
  const [listing, setListing] = useState(() =>
    prefill
      ? {
          ...INITIAL_LISTING,
          type: "SURPLUS",
          categoryId: String(prefill.categoryId),
          quantity: prefill.suggestedQuantity != null ? String(prefill.suggestedQuantity) : "",
        }
      : INITIAL_LISTING
  );
  const [categories, setCategories] = useState([]);
  const [errors, setErrors] = useState([]);
  const [submitting, setSubmitting] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    const fetchCategories = async () => {
      const response = await fetch("http://localhost:8080/api/categories");
      if (response.ok) {
        setCategories(await response.json());
      }
    };
    fetchCategories();
  }, []);

  function handleChange(event) {
    const updatedListing = { ...listing };
    updatedListing[event.target.name] =
      event.target.type === "checkbox" ? event.target.checked : event.target.value;
    setListing(updatedListing);
  }

  const isNeed = listing.type === "NEED";

  async function handleSubmit(event) {
    event.preventDefault();
    setSubmitting(true);
    setErrors([]);

    const payload = {
      userId: user.userId,
      categoryId: Number(listing.categoryId),
      type: listing.type,
      quantity: Number(listing.quantity),
      quantityRemaining: Number(listing.quantity),
      urgency: isNeed ? listing.urgency : null,
      neededBy: isNeed ? listing.neededBy : null,
      availableUntil: isNeed ? null : listing.availableUntil,
      status: "OPEN",
      anonymous: isNeed ? false : listing.anonymous,
    };


    const response = await fetch("http://localhost:8080/api/listings", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(payload),
    });

    if (response.ok) {
      navigate("/");
    } else if (response.status === 400) {
      setErrors(await response.json());
      setSubmitting(false);
    } else {
      setErrors(["Something went wrong. Please try again."]);
      setSubmitting(false);
    }
  }

  return (
    <div className="py-4" style={{ maxWidth: "480px", margin: "0 auto" }}>
      <div className="sb-form-card">
      <h1 className="sb-form-title">{prefill ? "Offer Surplus" : "New Listing"}</h1>

      {prefill && (
        <div className="sb-prefill-banner">
          Offering surplus for <strong>{prefill.categoryName}</strong>
          {prefill.suggestedQuantity != null && <> · they still need {prefill.suggestedQuantity} unit{prefill.suggestedQuantity === 1 ? "" : "s"}</>}
        </div>
      )}
      

      
      <form onSubmit={handleSubmit}>
        <div className="mb-3">
          <label className="form-label d-block">Type</label>
          <div className="btn-group w-100" role="group">
            <input
              type="radio"
              className="btn-check"
              name="type"
              id="typeNeed"
              value="NEED"
              checked={listing.type === "NEED"}
              onChange={handleChange}
              disabled={!!prefill}
            />
            <label className="btn btn-outline-danger" htmlFor="typeNeed">
              Need
            </label>

            <input
              type="radio"
              className="btn-check"
              name="type"
              id="typeSurplus"
              value="SURPLUS"
              checked={listing.type === "SURPLUS"}
              onChange={handleChange}
              disabled={!!prefill}
            />
            <label className="btn btn-outline-success" htmlFor="typeSurplus">
              Surplus
            </label>
          </div>
        </div>

        <div className="mb-3">
          <label className="form-label" htmlFor="categoryId">
            Category
          </label>
          <select
            className="form-select"
            id="categoryId"
            name="categoryId"
            value={listing.categoryId}
            onChange={handleChange}
            disabled={!!prefill}
            required
          >
            <option value="" disabled>
              Select a category
            </option>
            {categories.map((c) => (
              <option key={c.categoryId} value={c.categoryId}>
                {c.name}
              </option>
            ))}
          </select>
        </div>

        <div className="mb-3">
          <label className="form-label" htmlFor="quantity">
            Quantity
          </label>
          <input
            type="number"
            className="form-control"
            id="quantity"
            name="quantity"
            min="1"
            value={listing.quantity}
            onChange={handleChange}
            required
          />
        </div>

        {isNeed && (
          <div className="mb-3">
            <label className="form-label d-block">Urgency</label>
            <div className="btn-group w-100" role="group">
              {["LOW", "MEDIUM", "HIGH"].map((level) => (
                <Fragment key={level}>
                  <input
                    type="radio"
                    className="btn-check"
                    name="urgency"
                    id={`urgency${level}`}
                    value={level}
                    checked={listing.urgency === level}
                    onChange={handleChange}
                  />
                  <label className="btn btn-outline-secondary" htmlFor={`urgency${level}`}>
                    {level.charAt(0) + level.slice(1).toLowerCase()}
                  </label>
                </Fragment>
              ))}
            </div>
          </div>
        )}

        <div className="mb-4">
          <label className="form-label" htmlFor="deadline">
            {isNeed ? "Needed by" : "Available until"}
          </label>
          <input
            type="date"
            className="form-control"
            id="deadline"
            name={isNeed ? "neededBy" : "availableUntil"}
            value={isNeed ? listing.neededBy : listing.availableUntil}
            onChange={handleChange}
            required
          />
        </div>

        {!isNeed && (
          <div className="form-check mb-4 sb-anon-check">
            <input
              type="checkbox"
              className="form-check-input"
              id="anonymous"
              name="anonymous"
              checked={listing.anonymous}
              onChange={handleChange}
            />
            <label className="form-check-label" htmlFor="anonymous">
              Give anonymously — your username won't be shown to anyone
            </label>
          </div>
        )}

        {errors.length > 0 && (
          <div className="alert alert-danger">
            <ul className="mb-0">
              {errors.map((err) => (
                <li key={err}>{err}</li>
              ))}
            </ul>
          </div>
        )}

        <button
          type="submit"
          className="btn btn-primary w-100"
          disabled={submitting}
        >
          {submitting ? "Posting…" : "Post Listing"}
        </button>
        
      </form>
      </div>
    </div>
  );
}

export default NewListingPage;