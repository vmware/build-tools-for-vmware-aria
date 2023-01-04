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
