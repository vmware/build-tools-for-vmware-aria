import * as fs from "fs-extra";
export const exist = (file) => fs.existsSync(file);
export function isDirectory(path:string) : boolean {
    try {
        var stat = fs.lstatSync(path);
        return stat.isDirectory();
    } catch (e) {
        // lstatSync throws an error if path doesn't exist
        return false;
    }
}

