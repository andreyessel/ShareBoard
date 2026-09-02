function UrgencyBadge({ urgency }) {
  if (!urgency) return null;

  const config = {
    HIGH: { bg: "danger", icon: "bi-exclamation-triangle-fill", label: "Urgent" },
    MEDIUM: { bg: "warning", icon: "bi-clock-fill", label: "Medium" },
    LOW: { bg: "secondary", icon: "bi-hourglass-split", label: "Low" },
  };

  const { bg, icon, label } = config[urgency] ?? config.LOW;

  return (
    <span className={`badge text-bg-${bg} d-inline-flex align-items-center gap-1`}>
      <i className={`bi ${icon}`}></i>
      {label}
    </span>
  );
}

export default UrgencyBadge;