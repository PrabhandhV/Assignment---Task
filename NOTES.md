# NOTES

## Summary of changes
Fixed (details in handwritten/):
1. Search SQL: AND/OR precedence (missing parentheses) leaked archived rows and ignored the status filter. Fixed in the repository, search_tasks.sql and both Oracle queries.
2. Removed a Thread.sleep that delayed every request by up to 1s.
3. page/pageSize are clamped (page=0 used to return 500).
4. Invalid status now returns 400, not 500.
5. Frontend: debounced search and ignored stale responses (race condition).
6. Loading/error state now resets correctly in useTasks.
7. Page resets to 1 when search or filters change.
8. LIKE wildcards (% and _) are escaped.
9. Added id DESC as a sort tie-breaker for stable pagination.
10. Small cleanups: removed debug console.log, PAGE_SIZE constant, aria-labels.

## Extra Implementations 
- priority and assignee filters (backend + UI) and colour-coded priority badges.

## Not changed
- Pagination is still done in memory.
- Oracle file: new priority/assignee filters not mirrored; still uses ROWNUM.
- Hardcoded CORS origin, show-sql, H2 console, println logging, no tests.

## Biggest remaining risk
In-memory pagination: every request loads all matching rows. Fine for 47 rows, not for millions. Should move to SQL LIMIT/OFFSET plus a count query.

## Tools / AI
Used Claude Code to walk through the code, explain each bug and suggest fixes. I applied every change myself, and Claude reviewed my diffs for typos (e.g. a missing comma, a wrong variable). I verified with curl and the browser.