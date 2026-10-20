# Assignment evidence

Store real screenshots here. Never substitute generated mockups for running-app evidence.

The six screenshots below were captured on 20 October 2026. Application captures
come from Chromium connected to the running local server. Startup output is a
browser rendering of actual log lines (also saved verbatim in `startup.txt`),
not a terminal screenshot. The history capture is the public GitHub commits page.

| File | Required capture |
| --- | --- |
| `01-application.png` | Customer Directory running at localhost:8080. |
| `02-create.png` | A newly created fictional customer visible in the table. |
| `03-update.png` | The customer's edited values after saving. |
| `04-delete.png` | Delete confirmation and/or table after deletion. |
| `05-startup.png` | Terminal output showing successful Spring Boot startup. |
| `06-github-history.png` | GitHub main history with both students' commits. |

The original console app and the partner's merged exit-confirmation change are
preserved in `legacy/CustomerApp.java`. The main application is now Spring Boot +
Vaadin. Historical console screenshots, if present, document the earlier version.

Partner contribution to this repository: merged PR #1 (exit confirmation).
Contribution to partner repository: https://github.com/valeriia73438/First-Java-CRUD/pull/1
Review/merge of that PR is the partner's responsibility.

## Verification

Java 21 compilation and `./mvnw test` passed: **7 tests, 0 failures, 0 errors**.
Tests cover persistent database CRUD, validation, optional company, startup seed
idempotence, searching, and discarding unsaved edits.

Actual Chromium checks passed for creating, reading, updating, deleting,
retaining records after page reload, case-insensitive search, clearing unsaved
edits, cancelling deletion, and required-field validation. A mobile viewport was
also inspected. All test-created customer records were removed through the UI.

Submit the repository URL on Platon, not screenshots separately. Verify the
repository is public and all evidence is committed before submission.
