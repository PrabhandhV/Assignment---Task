import { useState } from 'react';
import SearchBar from './components/SearchBar';
import StatusFilter from './components/StatusFilter';
import TaskTable from './components/TaskTable';
import PriorityFilter from './components/PriorityFilter';
import AssigneeFilter from './components/AssigneeFilter';
import { useTasks } from './hooks/useTasks';
import { useDebounce } from './hooks/useDebounce';

export default function App() {
  const [query, setQuery] = useState('');
  const [status, setStatus] = useState('');
  const [priority, setPriority] = useState('');
  const [assignee, setAssignee] = useState('');
  const [page, setPage] = useState(1);

  const handlePriorityChange = (value) => {
    setPriority(value);
    setPage(1);
  };

  const handleAssigneeChange = (value) => {
    setAssignee(value);
    setPage(1);
  };

  const handleQueryChange = (value) => {
    setQuery(value);
    setPage(1);
  };

  const handleStatusChange = (value) => {
    setStatus(value);
    setPage(1);
  };

  const debouncedQuery = useDebounce(query, 300);
  const Page_size = 10;
  const { tasks, total, loading, error } = useTasks(debouncedQuery, status, priority, assignee, page, Page_size);

  const totalPages = Math.ceil(total / Page_size);

  return (
    <div className="app">
      <header className="app-header">
        <h1>Task Tracker</h1>
        <p className="subtitle">Internal task management</p>
      </header>

      <div className="controls">
        <SearchBar value={query} onChange={handleQueryChange} />
        <StatusFilter value={status} onChange={handleStatusChange} />
        <PriorityFilter value={priority} onChange={handlePriorityChange} />
        <AssigneeFilter value={assignee} onChange={handleAssigneeChange} />
      </div>

      <TaskTable tasks={tasks} loading={loading} error={error} />

      {totalPages > 1 && (
        <div className="pagination">
          <button disabled={page <= 1} onClick={() => setPage((p) => p - 1)}>
            Previous
          </button>
          <span>
            Page {page} of {totalPages}
          </span>
          <button disabled={page >= totalPages} onClick={() => setPage((p) => p + 1)}>
            Next
          </button>
        </div>
      )}
    </div>
  );
}
