import "./StatusBadge.css";

function StatusBadge({ status }) {
  const label = status
    ?.replaceAll("_", " ")
    .toLowerCase()
    .replace(/\b\w/g, (letter) => letter.toUpperCase());

  return (
    <span className={`status-badge status-${status?.toLowerCase()}`}>
      {label}
    </span>
  );
}

export default StatusBadge;