/*-
 * #%L
 * vropkg
 * %%
 * Copyright (C) 2023 VMware
 * %%
 * Build Tools for VMware Aria
 * Copyright 2023 VMware, Inc.
 * 
 * This product is licensed to you under the BSD-2 license (the "License"). You may not use this product except in compliance with the BSD-2 License.  
 * 
 * This product may include a number of subcomponents with separate copyright notices and license terms. Your use of these subcomponents is subject to the terms and conditions of the subcomponent's license, as noted in the LICENSE file.
 * #L%
 */
import * as Path from "path";
import * as FileSystem from "fs-extra";
import getLogger from "../logger";
import * as glob from "glob";

export class CleanDefinition {
    //Add here others definitions to be removed
    //Each empty definition can not contain white spaces
    private emptyDefinition = [
        {
            "definition": "/***@return{Any}*/(function(){varexports={};returnexports;});"
        }
    ];

    public removeEmptyDefinitions(vroJsFolderPath: string): void {
        const JS_EXTENSION = ".js";

		let globPath = Path.normalize(Path.join(vroJsFolderPath, "src", "main", "resources", "**", "*" + JS_EXTENSION))
			.replace(/[\\/]+/gm, Path.posix.sep)
		const files = glob.sync(globPath);
		getLogger().debug(`Base Path : "${vroJsFolderPath}", ${JSON.stringify({ globPath, files })} `);
		files.forEach(jsFile => {
            let content = FileSystem.readFileSync(jsFile);
            let source = content.toString();

            if (this.isVroEmptyAction(source)) {
                try {
                    if (FileSystem.existsSync(jsFile)) {
                        FileSystem.unlinkSync(jsFile);
						getLogger().info(`File deleted : "${jsFile}"`);
                    }
                }
                catch (error) {
                    throw error;
                }
            }

        });
    }

    private isVroEmptyAction(fileContent: string): boolean {
        let result = false;
        {
            let stringWithoutLineBreaks = fileContent
                .replace(/(\r\n|\n|\r)/gm, "")
                .replace(/ /g, "")
                .trim();
				getLogger().debug(`File content : ${stringWithoutLineBreaks}`);

            //This cicle goes until the end if result is always 'false'
            //If result has a match with any empty definition this file 
            //will be erased
            const length = this.emptyDefinition.length;
            for (let i = 0; i < length && !result; i++) {
                const emptyDef = this.emptyDefinition[i].definition;
                if (emptyDef == stringWithoutLineBreaks)
                    result = true;
            }
        }
        return result;
    }
}
