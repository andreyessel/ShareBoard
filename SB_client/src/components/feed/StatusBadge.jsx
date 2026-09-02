function StatusBadge({ status }) {
  const config = {
    OPEN: { bg: "primary", label: "Open" },
    PARTIALLY_MATCHED: { bg: "warning", label: "Partially Matched" },
    MATCHED: { bg: "success", label: "Matched" },
    WITHDRAWN: { bg: "secondary", label: "Withdrawn" },
  };

  const { bg, label } = config[status] ?? config.OPEN;

  return <span className={`badge text-bg-${bg}`}>{label}</span>;
}

export default StatusBadge;