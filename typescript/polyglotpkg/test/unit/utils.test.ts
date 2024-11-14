import * as assert from "assert";
import { findFiles } from "../../src/lib/file-system";

describe("Utils", () => {

    describe("File system", () => {

        describe("Search for files using regular expressions", () => {

            const comparable = (strArr: string[]) => JSON.stringify(strArr.sort());
            const options = { path: "test/unit/file-system/find-files" };

            it("Finds files by name in path", () => {
                assert.equal(
                    comparable(findFiles([ "my.file" ], options)),
                    comparable([
                        "my.file"
                    ])
                )
            })

            it("Finds files by name in shallow nested path", () => {
                assert.equal(
                    comparable(findFiles([ "*/my.file" ], options)),
                    comparable([
                        "exclude/my.file",
                        "include/my.file"
                    ])
                )
            })

            it("Finds files in deep nested paths", () => {
                assert.equal(
                    comparable(findFiles([ "**/my.file" ], options)),
                    comparable([
                        "exclude/my.file",
                        "include/my.file",
                        "include/exclude/my.file",
                        "include/include/my.file",
                        "include/include/exclude/my.file"
                    ])
                )
            })

            it("Finds files in shallow and deep nested paths", () => {
                assert.equal(
                    comparable(findFiles([ "**my.file" ], options)),
                    comparable([
                        "my.file",
                        "exclude/my.file",
                        "include/my.file",
                        "include/exclude/my.file",
                        "include/include/my.file",
                        "include/include/exclude/my.file"
                    ])
                )
            })

            const excludeOptions = { ...options, exclude: [ "**/exclude/**", "exclude/**" ] };

            it("Finds files by name in shallow nested path with path exclusion", () => {
                assert.equal(
                    comparable(findFiles([ "*/my.file" ], excludeOptions)),
                    comparable([
                        "include/my.file"
                    ])
                )
            })

            it("Finds files in deep nested paths with path exclusion", () => {
                assert.equal(
                    comparable(findFiles([ "**/my.file" ], excludeOptions)),
                    comparable([
                        "include/my.file",
                        "include/include/my.file",
                    ])
                )
            })

            it("Finds files in shallow and deep nested paths with path exclusion", () => {
                assert.equal(
                    comparable(findFiles([ "**my.file" ], excludeOptions)),
                    comparable([
                        "my.file",
                        "include/my.file",
                        "include/include/my.file",
                    ])
                )
            })

            it("Finds all files in path", () => {
                assert.equal(
                    comparable(findFiles([ "**" ], options)),
                    comparable([
                        'another.file',
                        'my.file',
                        'exclude/another.file',
                        'exclude/my.file',
                        'include/another.file',
                        'include/my.file',
                        'include/exclude/another.file',
                        'include/exclude/my.file',
                        'include/include/another.file',
                        'include/include/my.file',
                        'include/include/exclude/another.file',
                        'include/include/exclude/my.file'
                      ])
                )
            })

        })

    })

})
