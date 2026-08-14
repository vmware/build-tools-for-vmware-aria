### Operation Troubleshooting

Following is a list of tips on how to resolve issues that you might face when performing any of the supported operations.

- If a Maven error does not contain enough information, re-run the command with the `-X` debug flag.

    ```bash
    mvn -X <rest of the command>
    ```

- Sometimes Maven might cache old artifacts. To force the fetching of new artifacts, run the command with the `-U` flag. Alternatively, remove *< home >/.m2/repository* directory.

    ```bash
    mvn -U <rest of the command>
    ```
