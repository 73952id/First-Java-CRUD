# First Java CRUD

A small Java console application for managing customers. Create, list, update,
and delete records using a numbered menu. Changes are saved locally after every
successful operation and loaded again on startup.

## Run locally

Install a **JDK 17 or newer** (a JRE alone cannot compile the project). From the
repository root:

```sh
java -version
javac -version
mkdir -p out
javac -d out src/CustomerApp.java
java -cp out CustomerApp
```

On Windows PowerShell, use `New-Item -ItemType Directory -Force out` instead of
`mkdir -p out`. The remaining commands are the same.

No Maven, database server, or external Java libraries are required. The app uses
`data/customers.tsv` by default. Pass a different file as the first argument:

```sh
java -cp out CustomerApp /tmp/demo-customers.tsv
```

Use fictional customer details when taking screenshots. Local data is ignored by
Git. This is a learning exercise; email checking only validates a basic format.

## Try the CRUD operations

1. Choose **2**, enter `Alex Student` and `alex@example.com`.
2. Choose **1** to see the new customer and its ID.
3. Choose **3**, enter that ID, then a new name and email.
4. Choose **1** to check the updated record.
5. Exit with **0**, confirm with `yes`, and restart to check persistence.
6. Choose **4**, enter the ID, and type `yes` to delete it.
7. Choose **1** to verify that it was removed.

Invalid menu choices, missing IDs, blank fields, invalid email formats, and
cancelled deletions are handled without terminating the application.

Choosing **0** asks for exit confirmation. Enter `yes` (case-insensitive) to exit;
`no` or any other answer returns to the menu. Invalid answers show a short message.

## Project structure

- `src/CustomerApp.java`: the customer record, console menu, validation, CRUD
  operations, and file persistence. `main` loads the file and starts the menu.
- `data/customers.tsv`: generated local storage. Names and emails are Base64
  encoded so tabs and special characters do not break the file format. This is
  encoding, not encryption.
- `doc/`: instructions and screenshots documenting the assignment.

The app writes a temporary file before replacing the saved data. Failed saves
do not update the in-memory customer list. Run one instance per data file.

## Pair collaboration

Each student needs a separate course account (including their student ID), public
repository, and locally running app. Invite your partner as a collaborator, or
have them fork the repository.

Your partner should create a branch, make a small useful change, commit with
their own Git identity, push the branch, and open a Pull Request. Review it and
merge using **Create a merge commit** to preserve their commit. Do the equivalent
in your partner's repository. Do not manufacture commits on their behalf.

Example commands for a collaborator after cloning this repository:

```sh
git switch -c improve-menu
# Make a small change, then compile and run the app.
git add src/CustomerApp.java
git commit -m "Improve customer menu wording"
git push -u origin improve-menu
```

Open the Pull Request on GitHub. After it is merged, the owner should run
`git switch main` and `git pull --ff-only`, then verify the app again.

## Submission checklist

- [ ] Course GitHub username contains your student ID.
- [ ] Repository is public and accessible while signed out.
- [ ] Application compiles and runs locally.
- [ ] You opened a PR in your partner's repository and it was reviewed and merged.
- [ ] Your partner's PR here was reviewed and merged; both authors appear in history.
- [ ] Actual screenshots described in [doc/README.md](doc/README.md) are committed.
- [ ] Submit `https://github.com/73952id/First-Java-CRUD` as **Online text** on Platon.

Deadline: **25 October 2026 at 17:00 (Europe/Warsaw)**. Screenshots belong in this
repository, not as separate Platon uploads. Early completion only qualifies for
the attendance exemption when all assignment requirements are met and the link
is submitted before your scheduled laboratory.
