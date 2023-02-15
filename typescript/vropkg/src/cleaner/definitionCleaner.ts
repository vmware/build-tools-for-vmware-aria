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
import * as winston from 'winston';
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

        winston.loggers.get("vrbt").debug(`Base Path : "${vroJsFolderPath}"`);
        let baseDir = Path.join(vroJsFolderPath, "src", "main", "resources");
        glob.sync(Path.join(baseDir, "**", "*" + JS_EXTENSION)).forEach(jsFile => {
            var content = FileSystem.readFileSync(jsFile);
            let source = content.toString();

            if (this.isVroEmptyAction(source)) {
                try {
                    if (FileSystem.existsSync(jsFile)) {
                        FileSystem.unlinkSync(jsFile);
                        winston.loggers.get("vrbt").info(`File deleted : "${jsFile}"`);
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
            var stringWithoutLineBreaks = fileContent
                .replace(/(\r\n|\n|\r)/gm, "")
                .replace(/ /g, "")
                .trim();
            winston.loggers.get("vrbt").debug(`File content : ${stringWithoutLineBreaks}`);

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
