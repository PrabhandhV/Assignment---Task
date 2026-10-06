const ASSIGNEES = ['Alice', 'Bob', 'Carol', 'Dave', 'Eve'];

export default function AssigneeFilter({ value, onChange }) {
  return (
    <select
      className = "status-filter"
      value = {value}
      onChange = {(e) => onChange(e.target.value)}
      aria-label = "Filter by assignee"
    >
      <option value="">All assignees</option>
      {ASSIGNEES.map((name) => (
        <option key={name} value={name}>
          {name}
        </option>
      ))}
    </select>
  );
}