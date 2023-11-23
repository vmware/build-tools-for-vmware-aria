## Troubleshooting

- If maven error does not contain enough information re-run it with *-X* debug flag.
```bash
mvn -X <rest of the command>
```
- Sometimes maven might cache old artifacts. Force fetching new artifacts with *-U*. Alternatively remove *<home>/.m2/repository* folder.
```bash
mvn -U <rest of the command>
```
